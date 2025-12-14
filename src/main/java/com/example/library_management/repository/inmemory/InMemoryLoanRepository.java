package com.example.library_management.repository.inmemory;

import com.example.library_management.models.Loan;
import com.example.library_management.repository.LoanRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryLoanRepository implements LoanRepository {

    private final ConcurrentHashMap<String, Loan> loans = new ConcurrentHashMap<>();

    @Override
    public Optional<Loan> findById(String id) {
        return Optional.ofNullable(loans.get(id));
    }

    @Override
    public List<Loan> findAll() {
        return new ArrayList<>(loans.values());
    }

    @Override
    public List<Loan> findByUserId(String userId) {
        return loans.values().stream()
                .filter(loan -> userId.equals(loan.getUserId()))
                .toList();
    }

    @Override
    public List<Loan> findByBookId(String bookId) {
        return loans.values().stream()
                .filter(loan -> bookId.equals(loan.getBookId()))
                .toList();
    }

    @Override
    public List<Loan> findByStatus(Loan.Status status) {
        return loans.values().stream()
                .filter(loan -> status == loan.getStatus())
                .toList();
    }

    @Override
    public List<Loan> findByUserIdAndStatus(String userId, Loan.Status status) {
        return loans.values().stream()
                .filter(loan -> userId.equals(loan.getUserId()) && status == loan.getStatus())
                .toList();
    }

    @Override
    public Optional<Loan> findByUserIdAndBookIdAndStatus(String userId, String bookId, Loan.Status status) {
        return loans.values().stream()
                .filter(loan -> userId.equals(loan.getUserId())
                        && bookId.equals(loan.getBookId())
                        && status == loan.getStatus())
                .findFirst();
    }

    @Override
    public boolean existsByUserIdAndStatus(String userId, Loan.Status status) {
        return loans.values().stream()
                .anyMatch(loan -> userId.equals(loan.getUserId()) && status == loan.getStatus());
    }

    @Override
    public boolean existsByBookIdAndStatus(String bookId, Loan.Status status) {
        return loans.values().stream()
                .anyMatch(loan -> bookId.equals(loan.getBookId()) && status == loan.getStatus());
    }

    @Override
    public long countByUserIdAndStatus(String userId, Loan.Status status) {
        return loans.values().stream()
                .filter(loan -> userId.equals(loan.getUserId()) && status == loan.getStatus())
                .count();
    }

    @Override
    public Loan save(Loan loan) {
        if (loan == null) {
            throw new IllegalArgumentException("Loan cannot be null");
        }
        if (loan.getId() == null) {
            throw new IllegalArgumentException("Loan ID cannot be null");
        }
        loans.put(loan.getId(), loan);
        return loan;
    }

    @Override
    public List<Loan> saveAll(List<Loan> loanList) {
        if (loanList == null) {
            throw new IllegalArgumentException("Loan list cannot be null");
        }
        loanList.forEach(this::save);
        return loanList;
    }

    @Override
    public void deleteById(String id) {
        loans.remove(id);
    }

    @Override
    public long count() {
        return loans.size();
    }

}
