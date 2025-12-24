package com.example.library_management.repository.jpa;

import com.example.library_management.models.Loan;
import com.example.library_management.repository.LoanRepository;
import com.example.library_management.repository.jpa.SpringDataLoanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Profile("prod")
@RequiredArgsConstructor
public class JpaLoanRepository implements LoanRepository {

    private final SpringDataLoanRepository springDataLoanRepository;

    @Override
    public Optional<Loan> findById(String id) {
        return springDataLoanRepository.findById(id);
    }

    @Override
    public List<Loan> findAll() {
        return springDataLoanRepository.findAll();
    }

    @Override
    public List<Loan> findByUserId(String userId) {
        return springDataLoanRepository.findByUserId(userId);
    }

    @Override
    public List<Loan> findByBookId(String bookId) {
        return springDataLoanRepository.findByBookId(bookId);
    }

    @Override
    public List<Loan> findByStatus(Loan.Status status) {
        return springDataLoanRepository.findByStatus(status);
    }

    @Override
    public List<Loan> findByUserIdAndStatus(String userId, Loan.Status status) {
        return springDataLoanRepository.findByUserIdAndStatus(userId, status);
    }

    @Override
    public Optional<Loan> findByUserIdAndBookIdAndStatus(String userId, String bookId, Loan.Status status) {
        return springDataLoanRepository.findByUserIdAndBookIdAndStatus(userId, bookId, status);
    }

    @Override
    public boolean existsByUserIdAndStatus(String userId, Loan.Status status) {
        return springDataLoanRepository.existsByUserIdAndStatus(userId, status);
    }

    @Override
    public boolean existsByBookIdAndStatus(String bookId, Loan.Status status) {
        return springDataLoanRepository.existsByBookIdAndStatus(bookId, status);
    }

    @Override
    public long countByUserIdAndStatus(String userId, Loan.Status status) {
        return springDataLoanRepository.countByUserIdAndStatus(userId, status);
    }

    @Override
    public Loan save(Loan loan) {
        return springDataLoanRepository.save(loan);
    }

    @Override
    public List<Loan> saveAll(List<Loan> loans) {
        return springDataLoanRepository.saveAll(loans);
    }

    @Override
    public void deleteById(String id) {
        springDataLoanRepository.deleteById(id);
    }

    @Override
    public long count() {
        return springDataLoanRepository.count();
    }

    @Override
    public Optional<Loan> findByIdWithDetails(String id) {
        return springDataLoanRepository.findByIdWithDetails(id);
    }

    @Override
    public List<Loan> findAllWithDetails() {
        return springDataLoanRepository.findAllWithDetails();
    }

    @Override
    public List<Loan> findByUserIdWithDetails(String userId) {
        return springDataLoanRepository.findByUserIdWithDetails(userId);
    }
}
