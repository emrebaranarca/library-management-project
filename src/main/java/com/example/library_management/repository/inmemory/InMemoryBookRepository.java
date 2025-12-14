package com.example.library_management.repository.inmemory;

import com.example.library_management.models.Book;
import com.example.library_management.repository.BookRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryBookRepository implements BookRepository {

    private final ConcurrentHashMap<String, Book> books=new  ConcurrentHashMap<>();

    @Override
    public Optional<Book> findById(String id) {
        return Optional.ofNullable(books.get(id));

    }

    @Override
    public Optional<Book> findByIsbn(String isbn) {
        if(isbn==null){
            return Optional.empty();
        }
        return books.values().stream().filter(book -> book.getIsbn().equals(isbn)).findFirst();

    }

    @Override
    public boolean existsByIsbn(String isbn) {
        if(isbn==null){
            return false;
        }
        return books.values().stream().anyMatch(book -> book.getIsbn().equals(isbn));
    }

    @Override
    public boolean existsByIsbnAndIdNot(String isbn, String excludeId) {
        if (isbn == null) {
            return false;
        }
        return books.values().stream()
                .anyMatch(book -> isbn.equals(book.getIsbn()) && !book.getId().equals(excludeId));
    }

    @Override
    public List<Book> findAll() {
        return new ArrayList<>(books.values());
    }

    @Override
    public Book save(Book book) {
        if(book==null){
            throw new IllegalArgumentException("Book cannot be null");
        }
        if(book.getId()==null){
            throw new IllegalArgumentException("Book Id cannot be null");
        }
        books.put(book.getId(),book);
        return book;
    }

    @Override
    public void deleteById(String id) {
        books.remove(id);
    }

    @Override
    public long count() {
        return books.size();
    }
}
