package com.example.library_management.dto.book;

import com.example.library_management.models.Book;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookResponse {
    private String id;
    private String title;
    private String author;
    private String isbn;
    private int quantity;
    private int availableQuantity;
    private LocalDateTime createdAt;

    public static BookResponse fromBook(Book book) {
        return  BookResponse.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .isbn(book.getIsbn())
                .quantity(book.getQuantity())
                .availableQuantity(book.getAvailableQuantity())
                .createdAt(book.getCreatedAt())
                .build();
    }
}
