package com.example.library_management.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "loans")
public class Loan {
    @Id
    private String id;

    // Read-only fields for convenience (actual writes go through entity relationships)
    @Column(name = "user_id", nullable = false, insertable = false, updatable = false)
    private String userId;

    @Column(name = "book_id", nullable = false, insertable = false, updatable = false)
    private String bookId;

    // Entity relationships (used for writes and JOIN FETCH)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(name = "borrowed_at", nullable = false)
    private LocalDateTime borrowedAt;

    @Column(name = "due_date", nullable = false)
    private LocalDateTime dueDate;

    @Column(name = "returned_at")
    private LocalDateTime returnedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    public enum Status {
        ACTIVE, RETURNED, OVERDUE
    }

    // Custom getters for backward compatibility
    // Returns ID from entity relationship if available, falls back to direct field
    public String getUserId() {
        return user != null ? user.getId() : userId;
    }

    public String getBookId() {
        return book != null ? book.getId() : bookId;
    }
}
