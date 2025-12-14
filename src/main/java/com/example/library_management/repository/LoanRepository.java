package com.example.library_management.repository;

import com.example.library_management.models.Loan;

import java.util.List;
import java.util.Optional;

public interface LoanRepository {
    /**
     * Find a loan by its unique ID.
     */
    Optional<Loan> findById(String id);

    /**
     * Get all loans.
     */
    List<Loan> findAll();

    /**
     * Find all loans for a specific user.
     */
    List<Loan> findByUserId(String userId);

    /**
     * Find all loans for a specific book.
     */
    List<Loan> findByBookId(String bookId);

    /**
     * Find all loans with a specific status.
     */
    List<Loan> findByStatus(Loan.Status status);

    /**
     * Find all active loans for a specific user.
     */
    List<Loan> findByUserIdAndStatus(String userId, Loan.Status status);

    /**
     * Find an active loan for a specific user and book combination.
     */
    Optional<Loan> findByUserIdAndBookIdAndStatus(String userId, String bookId, Loan.Status status);

    /**
     * Check if a user has any loans with the given status.
     */
    boolean existsByUserIdAndStatus(String userId, Loan.Status status);

    /**
     * Check if a book has any loans with the given status.
     */
    boolean existsByBookIdAndStatus(String bookId, Loan.Status status);

    /**
     * Count the number of loans for a user with a specific status.
     */
    long countByUserIdAndStatus(String userId, Loan.Status status);

    /**
     * Save a new loan or update an existing one.
     * @return the saved loan
     */
    Loan save(Loan loan);

    /**
     * Save multiple loans (useful for batch updates like overdue status).
     * @return the saved loans
     */
    List<Loan> saveAll(List<Loan> loans);

    /**
     * Delete a loan by its ID.
     */
    void deleteById(String id);

    /**
     * Count total number of loans.
     */
    long count();
}
