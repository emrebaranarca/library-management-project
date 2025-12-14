package com.example.library_management.models;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Loan {
    private String id;
    private String userId;
    private String bookId;
    private LocalDateTime borrowedAt;
    private LocalDateTime dueDate;
    private LocalDateTime returnedAt;
    private Status status;

    public enum Status {
        ACTIVE, RETURNED, OVERDUE
    }
}
