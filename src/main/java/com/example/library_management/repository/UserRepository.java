package com.example.library_management.repository;

import com.example.library_management.models.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    /**
     * Find a user by their unique ID.
     */
    Optional<User> findById(String id);

    /**
     * Find a user by their email address (case-insensitive).
     */
    Optional<User> findByEmail(String email);

    /**
     * Check if a user with the given email exists.
     */
    boolean existsByEmail(String email);

    /**
     * Get all users.
     */
    List<User> findAll();

    /**
     * Save a new user or update an existing one.
     * @return the saved user
     */
    User save(User user);

    /**
     * Delete a user by their ID.
     */
    void deleteById(String id);

    /**
     * Count total number of users.
     */
    long count();
}
