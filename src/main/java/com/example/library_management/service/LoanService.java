package com.example.library_management.service;

import com.example.library_management.dto.loan.LoanRequest;
import com.example.library_management.dto.loan.LoanResponse;
import com.example.library_management.exception.BadRequestException;
import com.example.library_management.exception.ForbiddenException;
import com.example.library_management.exception.ResourceNotFoundException;
import com.example.library_management.models.Book;
import com.example.library_management.models.Loan;
import com.example.library_management.models.User;
import com.example.library_management.repository.LoanRepository;
import com.example.library_management.repository.UserRepository;
import com.example.library_management.security.UserPrincipal;
import lombok.NoArgsConstructor;
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

    private static final int MAX_ACTIVE_LOANS = 3;

    @Value("${app.loan.duration-days:14}")
    private int loanDurationDays;

    public LoanResponse borrowBook(LoanRequest request, UserPrincipal currentUser) {
        String userId = currentUser.getId();
        String bookId = request.getBookId();

        // Update overdue loans first
        updateOverdueLoans();

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

        // Create new loan
        LocalDateTime now = LocalDateTime.now();
        Loan newLoan = Loan.builder()
                .id(UUID.randomUUID().toString())
                .userId(userId)
                .bookId(bookId)
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

    private LoanResponse buildLoanResponse(Loan loan) {
        LoanResponse response = LoanResponse.fromLoan(loan);

        // Add book title
        try {
            Book book = bookService.findBookById(loan.getBookId());
            response.setBookTitle(book.getTitle());
        } catch (ResourceNotFoundException e) {
            response.setBookTitle("Unknown Book");
        }

        // Add user name
        userRepository.findById(loan.getUserId())
                .ifPresentOrElse(
                        user -> response.setUserName(user.getName()),
                        () -> response.setUserName("Unknown User")
                );

        return response;
    }
    private void updateOverdueLoans() {
        LocalDateTime now = LocalDateTime.now();

        // Tüm aktif loan'ları al
        List<Loan> activeLoans = loanRepository.findByStatus(Loan.Status.ACTIVE);

        // Vadesi geçmiş loan'ları bul ve güncelle
        List<Loan> overdueLoans = new ArrayList<>();

        for (Loan loan : activeLoans) {
            if (loan.getDueDate().isBefore(now)) {  // Vade geçmiş mi?
                loan.setStatus(Loan.Status.OVERDUE);  // Statüyü OVERDUE yap
                overdueLoans.add(loan);  // Listeye ekle
            }
        }

        // Varsa database'e kaydet
        if (!overdueLoans.isEmpty()) {
            loanRepository.saveAll(overdueLoans);
        }
    }

    public List<LoanResponse> getAllLoans() {
        updateOverdueLoans();

        return loanRepository.findAll().stream()
                .map(this::buildLoanResponse)
                .toList();
    }

    public List<LoanResponse> getCurrentUserLoans(UserPrincipal currentUser) {
        updateOverdueLoans();

        return loanRepository.findByUserId(currentUser.getId()).stream()
                .map(this::buildLoanResponse)
                .toList();
    }

    public LoanResponse returnBook(String loanId, UserPrincipal currentUser) {
        Loan loan = loanRepository.findById(loanId)
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

        Book book = bookService.findBookById(loan.getBookId());
        log.info("Book '{}' returned by user", book.getTitle());

        return buildLoanResponse(loan);
    }
}
