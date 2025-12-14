package com.example.library_management.repository.inmemory;

import com.example.library_management.models.User;
import com.example.library_management.repository.UserRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@Primary
public class InMemoryUserRepository implements UserRepository {
    private final ConcurrentHashMap<String, User> users = new ConcurrentHashMap<>();

    @Override
    public Optional<User> findById(String id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        if (email == null) {
            return Optional.empty();
        }
        return users.values().stream()
                .filter(user -> email.equalsIgnoreCase(user.getEmail()))
                .findFirst();
    }

    @Override
    public boolean existsByEmail(String email) {
        if (email == null) {
            return false;
        }
        return users.values().stream()
                .anyMatch(user -> email.equalsIgnoreCase(user.getEmail()));
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User save(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        if (user.getId() == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void deleteById(String id) {
        users.remove(id);
    }

    @Override
    public long count() {
        return users.size();
    }
}
