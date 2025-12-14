package com.example.library_management.dto.loan;

import com.example.library_management.models.Loan;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanResponse {
    private String id;
    private String userId;
    private String bookId;
    private String bookTitle;
    private String userName;
    private LocalDateTime borrowedAt;
    private LocalDateTime dueDate;
    private LocalDateTime returnedAt;
    private Loan.Status status;

    public static LoanResponse fromLoan(Loan loan) {
        return LoanResponse.builder()
                .id(loan.getId())
                .userId(loan.getUserId())
                .bookId(loan.getBookId())
                .borrowedAt(loan.getBorrowedAt())
                .dueDate(loan.getDueDate())
                .returnedAt(loan.getReturnedAt())
                .status(loan.getStatus())
                .build();
    }
}
