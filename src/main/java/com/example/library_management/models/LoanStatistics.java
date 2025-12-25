package com.example.library_management.models;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Immutable;

@Data
@Entity
@Immutable  // view'lar read-only
@Table(name = "loan_statistics")
public class LoanStatistics {
    @Id
    @Column(name = "user_id")
    private String userId;

    @Column(name = "user_name")
    private String userName;

    private String email;

    @Column(name = "total_loans")
    private Long totalLoans;

    @Column(name = "active_loans")
    private Long activeLoans;

    @Column(name = "overdue_loans")
    private Long overdueLoans;

    @Column(name = "returned_loans")
    private Long returnedLoans;
}