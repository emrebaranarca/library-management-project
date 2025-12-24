package com.example.library_management.repository.jpa;

import com.example.library_management.models.Book;
import com.example.library_management.repository.BookRepository;
import com.example.library_management.repository.jpa.SpringDataBookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Profile("prod")
@RequiredArgsConstructor
public class JpaBookRepository implements BookRepository {

    private final SpringDataBookRepository springDataBookRepository;

    @Override
    public Optional<Book> findById(String id) {
        return springDataBookRepository.findById(id);
    }

    @Override
    public Optional<Book> findByIsbn(String isbn) {
        return springDataBookRepository.findByIsbn(isbn);
    }

    @Override
    public boolean existsByIsbn(String isbn) {
        return springDataBookRepository.existsByIsbn(isbn);
    }

    @Override
    public boolean existsByIsbnAndIdNot(String isbn, String excludeId) {
        return springDataBookRepository.existsByIsbnAndIdNot(isbn, excludeId);
    }

    @Override
    public List<Book> findAll() {
        return springDataBookRepository.findAll();
    }

    @Override
    public Book save(Book book) {
        return springDataBookRepository.save(book);
    }

    @Override
    public void deleteById(String id) {
        springDataBookRepository.deleteById(id);
    }

    @Override
    public long count() {
        return springDataBookRepository.count();
    }
}
