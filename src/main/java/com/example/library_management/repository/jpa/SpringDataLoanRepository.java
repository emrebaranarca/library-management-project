package com.example.library_management.repository.jpa;

import com.example.library_management.models.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SpringDataLoanRepository extends JpaRepository<Loan, String> {
    
    List<Loan> findByUserId(String userId);
    
    List<Loan> findByBookId(String bookId);
    
    List<Loan> findByStatus(Loan.Status status);
    
    List<Loan> findByUserIdAndStatus(String userId, Loan.Status status);
    
    Optional<Loan> findByUserIdAndBookIdAndStatus(String userId, String bookId, Loan.Status status);
    
    boolean existsByUserIdAndStatus(String userId, Loan.Status status);
    
    boolean existsByBookIdAndStatus(String bookId, Loan.Status status);
    
    long countByUserIdAndStatus(String userId, Loan.Status status);

    @Query("SELECT l FROM Loan l JOIN FETCH l.user JOIN FETCH l.book WHERE l.id = :id")
    Optional<Loan> findByIdWithDetails(@Param("id") String id);

    @Query("SELECT l FROM Loan l JOIN FETCH l.user JOIN FETCH l.book")
    List<Loan> findAllWithDetails();

    @Query("SELECT l FROM Loan l JOIN FETCH l.user JOIN FETCH l.book WHERE l.user.id = :userId")
    List<Loan> findByUserIdWithDetails(@Param("userId") String userId);
}
