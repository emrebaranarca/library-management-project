package com.example.library_management.repository;

import com.example.library_management.models.Book;

import java.util.List;
import java.util.Optional;

public interface BookRepository {
    /**
     * Find a book by its unique ID.
     */
    Optional<Book> findById(String id);

    /**
     * Find a book by its ISBN.
     */
    Optional<Book> findByIsbn(String isbn);

    /**
     * Check if a book with the given ISBN exists.
     */
    boolean existsByIsbn(String isbn);

    /**
     * Check if a book with the given ISBN exists, excluding a specific book ID.
     * Useful for update operations.
     */
    boolean existsByIsbnAndIdNot(String isbn, String excludeId);

    /**
     * Get all books.
     */
    List<Book> findAll();

    /**
     * Save a new book or update an existing one.
     * @return the saved book
     */
    Book save(Book book);

    /**
     * Delete a book by its ID.
     */
    void deleteById(String id);

    /**
     * Count total number of books.
     */
    long count();
}
