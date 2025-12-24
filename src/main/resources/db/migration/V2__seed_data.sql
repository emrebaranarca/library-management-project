-- Seed data for library management system
-- Passwords are BCrypt encoded (all passwords are 'password123')

-- Insert users
-- Admin user
INSERT INTO users (id, name, email, password, role, created_at) VALUES
    ('550e8400-e29b-41d4-a716-446655440001', 'Admin User', 'admin@library.com', 
     '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n9RP1sFgYsTV./gLJ/gC2', 'ADMIN', '2024-01-01 10:00:00');

-- Member users
INSERT INTO users (id, name, email, password, role, created_at) VALUES
    ('550e8400-e29b-41d4-a716-446655440002', 'John Doe', 'john.doe@email.com',
     '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n9RP1sFgYsTV./gLJ/gC2', 'MEMBER', '2024-01-15 14:30:00'),
    ('550e8400-e29b-41d4-a716-446655440003', 'Jane Smith', 'jane.smith@email.com',
     '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n9RP1sFgYsTV./gLJ/gC2', 'MEMBER', '2024-02-01 09:15:00'),
    ('550e8400-e29b-41d4-a716-446655440004', 'Bob Wilson', 'bob.wilson@email.com',
     '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n9RP1sFgYsTV./gLJ/gC2', 'MEMBER', '2024-02-20 11:45:00'),
    ('550e8400-e29b-41d4-a716-446655440005', 'Alice Brown', 'alice.brown@email.com',
     '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n9RP1sFgYsTV./gLJ/gC2', 'MEMBER', '2024-03-10 16:20:00');

-- Insert books
INSERT INTO books (id, title, author, isbn, quantity, available_quantity, created_at) VALUES
    ('660e8400-e29b-41d4-a716-446655440001', 'The Great Gatsby', 'F. Scott Fitzgerald', '978-0743273565', 5, 4, '2024-01-01 10:00:00'),
    ('660e8400-e29b-41d4-a716-446655440002', 'To Kill a Mockingbird', 'Harper Lee', '978-0061120084', 3, 2, '2024-01-01 10:00:00'),
    ('660e8400-e29b-41d4-a716-446655440003', '1984', 'George Orwell', '978-0451524935', 4, 4, '2024-01-05 14:00:00'),
    ('660e8400-e29b-41d4-a716-446655440004', 'Pride and Prejudice', 'Jane Austen', '978-0141439518', 3, 3, '2024-01-10 09:00:00'),
    ('660e8400-e29b-41d4-a716-446655440005', 'The Catcher in the Rye', 'J.D. Salinger', '978-0316769488', 2, 1, '2024-01-15 11:00:00'),
    ('660e8400-e29b-41d4-a716-446655440006', 'One Hundred Years of Solitude', 'Gabriel Garcia Marquez', '978-0060883287', 3, 3, '2024-02-01 10:00:00'),
    ('660e8400-e29b-41d4-a716-446655440007', 'Brave New World', 'Aldous Huxley', '978-0060850524', 4, 3, '2024-02-10 15:30:00'),
    ('660e8400-e29b-41d4-a716-446655440008', 'The Lord of the Rings', 'J.R.R. Tolkien', '978-0618640157', 5, 5, '2024-02-15 12:00:00'),
    ('660e8400-e29b-41d4-a716-446655440009', 'Harry Potter and the Sorcerers Stone', 'J.K. Rowling', '978-0590353427', 6, 5, '2024-03-01 10:00:00'),
    ('660e8400-e29b-41d4-a716-446655440010', 'The Hobbit', 'J.R.R. Tolkien', '978-0547928227', 4, 4, '2024-03-05 14:00:00');

-- Insert loans (some active, some returned)
INSERT INTO loans (id, user_id, book_id, borrowed_at, due_date, returned_at, status) VALUES
    -- Active loans
    ('770e8400-e29b-41d4-a716-446655440001', '550e8400-e29b-41d4-a716-446655440002', '660e8400-e29b-41d4-a716-446655440001',
     '2024-12-01 10:00:00', '2024-12-15 10:00:00', NULL, 'ACTIVE'),
    ('770e8400-e29b-41d4-a716-446655440002', '550e8400-e29b-41d4-a716-446655440003', '660e8400-e29b-41d4-a716-446655440002',
     '2024-12-05 14:30:00', '2024-12-19 14:30:00', NULL, 'ACTIVE'),
    ('770e8400-e29b-41d4-a716-446655440003', '550e8400-e29b-41d4-a716-446655440004', '660e8400-e29b-41d4-a716-446655440005',
     '2024-12-10 09:00:00', '2024-12-24 09:00:00', NULL, 'ACTIVE'),
    ('770e8400-e29b-41d4-a716-446655440004', '550e8400-e29b-41d4-a716-446655440002', '660e8400-e29b-41d4-a716-446655440007',
     '2024-12-15 11:00:00', '2024-12-29 11:00:00', NULL, 'ACTIVE'),
    ('770e8400-e29b-41d4-a716-446655440005', '550e8400-e29b-41d4-a716-446655440005', '660e8400-e29b-41d4-a716-446655440009',
     '2024-12-18 16:00:00', '2025-01-01 16:00:00', NULL, 'ACTIVE'),
    
    -- Returned loans
    ('770e8400-e29b-41d4-a716-446655440006', '550e8400-e29b-41d4-a716-446655440002', '660e8400-e29b-41d4-a716-446655440003',
     '2024-10-01 10:00:00', '2024-10-15 10:00:00', '2024-10-12 14:00:00', 'RETURNED'),
    ('770e8400-e29b-41d4-a716-446655440007', '550e8400-e29b-41d4-a716-446655440003', '660e8400-e29b-41d4-a716-446655440004',
     '2024-10-10 09:00:00', '2024-10-24 09:00:00', '2024-10-20 11:30:00', 'RETURNED'),
    ('770e8400-e29b-41d4-a716-446655440008', '550e8400-e29b-41d4-a716-446655440004', '660e8400-e29b-41d4-a716-446655440006',
     '2024-11-01 14:00:00', '2024-11-15 14:00:00', '2024-11-14 10:00:00', 'RETURNED'),
    ('770e8400-e29b-41d4-a716-446655440009', '550e8400-e29b-41d4-a716-446655440005', '660e8400-e29b-41d4-a716-446655440008',
     '2024-11-15 11:00:00', '2024-11-29 11:00:00', '2024-11-28 16:45:00', 'RETURNED'),
    ('770e8400-e29b-41d4-a716-446655440010', '550e8400-e29b-41d4-a716-446655440003', '660e8400-e29b-41d4-a716-446655440010',
     '2024-11-20 10:00:00', '2024-12-04 10:00:00', '2024-12-01 09:00:00', 'RETURNED');

