package com.example.library_management.repository.jpa;

import com.example.library_management.models.LoanStatistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LoanStatisticsRepository extends JpaRepository<LoanStatistics, String> {

    // En çok kitap ödünç alanlar
    List<LoanStatistics> findTop10ByOrderByTotalLoansDesc();

    // Gecikmiş kitabı olanlar
    List<LoanStatistics> findByOverdueLoansGreaterThan(Long count);

    // Custom JPQL
    @Query("SELECT ls FROM LoanStatistics ls WHERE ls.activeLoans > 3")
    List<LoanStatistics> findUsersWithManyActiveLoans();
}