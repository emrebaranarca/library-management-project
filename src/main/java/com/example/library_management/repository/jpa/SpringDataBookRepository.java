package com.example.library_management.repository.jpa;

import com.example.library_management.models.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpringDataBookRepository extends JpaRepository<Book, String> {
    
    Optional<Book> findByIsbn(String isbn);
    
    boolean existsByIsbn(String isbn);
    
    boolean existsByIsbnAndIdNot(String isbn, String excludeId);
}
