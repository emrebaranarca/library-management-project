package com.example.library_management.service;

import com.example.library_management.dto.loan.LoanRequest;
import com.example.library_management.dto.loan.LoanResponse;
import com.example.library_management.exception.BadRequestException;
import com.example.library_management.exception.ForbiddenException;
import com.example.library_management.exception.ResourceNotFoundException;
import com.example.library_management.models.Book;
import com.example.library_management.models.Loan;
import com.example.library_management.models.LoanStatistics;
import com.example.library_management.models.User;
import com.example.library_management.repository.LoanRepository;
import com.example.library_management.repository.UserRepository;
import com.example.library_management.repository.jpa.LoanStatisticsRepository;
import com.example.library_management.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class LoanService {
    private final LoanRepository loanRepository;
    private final UserRepository userRepository;
    private final BookService bookService;
    private final LoanStatisticsRepository loanStatisticsRepository;

    @Value("${app.loan.max-active-loans:3}")
    private  int MAX_ACTIVE_LOANS;

    @Value("${app.loan.duration-days:14}")
    private int loanDurationDays;

    public List<LoanStatistics> getAllLoanStatistics() {
        return loanStatisticsRepository.findAll();
    }

    public LoanStatistics getUserLoanStatistics(String userId) {
        return loanStatisticsRepository.findById(userId)
                .orElseThrow(()-> new ResourceNotFoundException("LoanStatistics","userId",userId));
    }

    public List<LoanStatistics> getTopBorrowers(){
        return loanStatisticsRepository.findTop10ByOrderByTotalLoansDesc();
    }

    public LoanResponse borrowBook(LoanRequest request, UserPrincipal currentUser) {
        String userId = currentUser.getId();
        String bookId = request.getBookId();

        // Update overdue loans first
        updateOverdueLoans();

        // Get User entity (needed for @ManyToOne relationship)
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        // Check if book exists and is available
        Book book = bookService.findBookById(bookId);
        if (book.getAvailableQuantity() <= 0) {
            throw new BadRequestException("Book is currently out of stock");
        }

        // Check user's active loans count
        long activeLoansCount = loanRepository.countByUserIdAndStatus(userId, Loan.Status.ACTIVE);
        if (activeLoansCount >= MAX_ACTIVE_LOANS) {
            throw new BadRequestException("Maximum number of active loans (" + MAX_ACTIVE_LOANS + ") reached");
        }

        // Check if user has overdue loans
        if (loanRepository.existsByUserIdAndStatus(userId, Loan.Status.OVERDUE)) {
            throw new BadRequestException("Cannot borrow new books while having overdue loans. Please return your overdue books first.");
        }

        // Check if user already has this book borrowed
        if (loanRepository.findByUserIdAndBookIdAndStatus(userId, bookId, Loan.Status.ACTIVE).isPresent()) {
            throw new BadRequestException("You already have this book borrowed");
        }

        // Create new loan with entity relationships
        LocalDateTime now = LocalDateTime.now();
        Loan newLoan = Loan.builder()
                .id(UUID.randomUUID().toString())
                .user(user)   // Set User entity (writes to user_id column)
                .book(book)   // Set Book entity (writes to book_id column)
                .borrowedAt(now)
                .dueDate(now.plusDays(loanDurationDays))
                .status(Loan.Status.ACTIVE)
                .build();

        loanRepository.save(newLoan);

        // Decrement available quantity
        bookService.decrementAvailableQuantity(bookId);

        log.info("Book '{}' borrowed by user '{}'", book.getTitle(), currentUser.getEmail());

        return buildLoanResponse(newLoan);
    }

    /**
     * Builds LoanResponse from Loan entity.
     * Uses entity relationships if available (JOIN FETCH), falls back to manual lookup.
     */
    private LoanResponse buildLoanResponse(Loan loan) {
        LoanResponse response = LoanResponse.fromLoan(loan);

        // Get book title - prefer entity relationship (no extra query if JOIN FETCH was used)
        if (loan.getBook() != null) {
            response.setBookTitle(loan.getBook().getTitle());
        } else {
            try {
                Book book = bookService.findBookById(loan.getBookId());
                response.setBookTitle(book.getTitle());
            } catch (ResourceNotFoundException e) {
                response.setBookTitle("Unknown Book");
            }
        }

        // Get user name - prefer entity relationship (no extra query if JOIN FETCH was used)
        if (loan.getUser() != null) {
            response.setUserName(loan.getUser().getName());
        } else {
            userRepository.findById(loan.getUserId())
                    .ifPresentOrElse(
                            user -> response.setUserName(user.getName()),
                            () -> response.setUserName("Unknown User")
                    );
        }

        return response;
    }

    private void updateOverdueLoans() {
        LocalDateTime now = LocalDateTime.now();

        // Get all active loans
        List<Loan> activeLoans = loanRepository.findByStatus(Loan.Status.ACTIVE);

        // Find and update overdue loans
        List<Loan> overdueLoans = new ArrayList<>();

        for (Loan loan : activeLoans) {
            if (loan.getDueDate().isBefore(now)) {
                loan.setStatus(Loan.Status.OVERDUE);
                overdueLoans.add(loan);
            }
        }

        // Save if any loans became overdue
        if (!overdueLoans.isEmpty()) {
            loanRepository.saveAll(overdueLoans);
            log.info("Marked {} loans as overdue", overdueLoans.size());
        }
    }

    /**
     * Get all loans with User and Book details.
     * Uses JOIN FETCH - single query instead of N+1!
     */
    public List<LoanResponse> getAllLoans() {
        updateOverdueLoans();

        return loanRepository.findAllWithDetails().stream()
                .map(this::buildLoanResponse)
                .toList();
    }

    /**
     * Get current user's loans with Book details.
     * Uses JOIN FETCH - single query instead of N+1!
     */
    public List<LoanResponse> getCurrentUserLoans(UserPrincipal currentUser) {
        updateOverdueLoans();

        return loanRepository.findByUserIdWithDetails(currentUser.getId()).stream()
                .map(this::buildLoanResponse)
                .toList();
    }

    public LoanResponse returnBook(String loanId, UserPrincipal currentUser) {
        // Use findByIdWithDetails - single query with JOIN FETCH
        Loan loan = loanRepository.findByIdWithDetails(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan", "id", loanId));

        // Check if the loan belongs to the current user or user is admin
        if (!loan.getUserId().equals(currentUser.getId())
                && currentUser.getRole() != User.Role.ADMIN) {
            throw new ForbiddenException("You can only return your own loans");
        }

        if (loan.getStatus() == Loan.Status.RETURNED) {
            throw new BadRequestException("This book has already been returned");
        }

        // Update loan
        loan.setReturnedAt(LocalDateTime.now());
        loan.setStatus(Loan.Status.RETURNED);

        loanRepository.save(loan);

        // Increment available quantity
        bookService.incrementAvailableQuantity(loan.getBookId());

        log.info("Book '{}' returned by user '{}'", 
                loan.getBook() != null ? loan.getBook().getTitle() : loan.getBookId(),
                loan.getUser() != null ? loan.getUser().getName() : loan.getUserId());

        return buildLoanResponse(loan);
    }
}
