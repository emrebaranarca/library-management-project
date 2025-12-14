package com.example.library_management.service;

import com.example.library_management.dto.book.BookRequest;
import com.example.library_management.dto.book.BookResponse;
import com.example.library_management.exception.BadRequestException;
import com.example.library_management.exception.ResourceNotFoundException;
import com.example.library_management.models.Book;
import com.example.library_management.models.Loan;
import com.example.library_management.repository.BookRepository;
import com.example.library_management.repository.LoanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookService {
    private final BookRepository bookRepository;
    private final LoanRepository loanRepository;

    public List<BookResponse> getAllBooks() {
        List<BookResponse> bookResponseList = new ArrayList<>();
        List<Book> bookList = bookRepository.findAll();
        for (Book book : bookList) {
            bookResponseList.add(BookResponse.fromBook(book));
        }
        return bookResponseList;
    }

    public Book findBookById(String id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book", "id", id));
    }

    public BookResponse getBookById(String id) {
        Book book = findBookById(id);
        return BookResponse.fromBook(book);
    }

    public BookResponse createBook(BookRequest request) {
        // Check if ISBN already exists
        if (bookRepository.existsByIsbn(request.getIsbn())) {
            throw new BadRequestException("A book with this ISBN already exists");
        }

        Book newBook = Book.builder()
                .id(UUID.randomUUID().toString())
                .title(request.getTitle())
                .author(request.getAuthor())
                .isbn(request.getIsbn())
                .quantity(request.getQuantity())
                .availableQuantity(request.getQuantity())
                .createdAt(LocalDateTime.now())
                .build();

        bookRepository.save(newBook);

        log.info("New book created: {} by {}", newBook.getTitle(), newBook.getAuthor());

        return BookResponse.fromBook(newBook);
    }

    public BookResponse updateBook(String id, BookRequest request) {
        Book existingBook = findBookById(id);

        // Check if ISBN already exists for another book
        if (bookRepository.existsByIsbnAndIdNot(request.getIsbn(), id)) {
            throw new BadRequestException("A book with this ISBN already exists");
        }

        // Calculate the difference in quantity
        int quantityDiff = request.getQuantity() - existingBook.getQuantity();
        int newAvailableQuantity = existingBook.getAvailableQuantity() + quantityDiff;

        if (newAvailableQuantity < 0) {
            throw new BadRequestException("Cannot reduce quantity below the number of borrowed copies");
        }

        existingBook.setTitle(request.getTitle());
        existingBook.setAuthor(request.getAuthor());
        existingBook.setIsbn(request.getIsbn());
        existingBook.setQuantity(request.getQuantity());
        existingBook.setAvailableQuantity(newAvailableQuantity);

        bookRepository.save(existingBook);

        log.info("Book updated: {}", existingBook.getTitle());

        return BookResponse.fromBook(existingBook);
    }

    public void deleteBook(String id) {
        Book book = findBookById(id);

        // Check if book has active loans
        if (loanRepository.existsByBookIdAndStatus(id, Loan.Status.ACTIVE)) {
            throw new BadRequestException("Cannot delete book with active loans");
        }

        bookRepository.deleteById(id);

        log.info("Book deleted: {}", book.getTitle());
    }

    public void decrementAvailableQuantity(String bookId) {
        Book book = findBookById(bookId);
        book.setAvailableQuantity(book.getAvailableQuantity() - 1);
        bookRepository.save(book);
    }

    public void incrementAvailableQuantity(String bookId) {
        Book book = findBookById(bookId);
        book.setAvailableQuantity(book.getAvailableQuantity() + 1);
        bookRepository.save(book);
    }

}
