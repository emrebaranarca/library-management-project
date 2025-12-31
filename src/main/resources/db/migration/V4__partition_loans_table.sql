-- 0. Drop dependent views
DROP VIEW IF EXISTS loan_statistics;

-- 1. Rename the existing table
ALTER TABLE loans RENAME TO loans_old;

-- 2. Create the new partitioned table
CREATE TABLE loans (
    id VARCHAR(36) NOT NULL,
    user_id VARCHAR(36) NOT NULL,
    book_id VARCHAR(36) NOT NULL,
    borrowed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    due_date TIMESTAMP NOT NULL,
    returned_at TIMESTAMP,
    status VARCHAR(20) NOT NULL,
    -- The primary key must include the partition key
    PRIMARY KEY (id, borrowed_at),
    CONSTRAINT fk_loans_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_loans_book FOREIGN KEY (book_id) REFERENCES books(id)
) PARTITION BY RANGE (borrowed_at);

-- 3. Create partitions
-- Partition for 2023 and earlier
CREATE TABLE loans_2023 PARTITION OF loans
    FOR VALUES FROM (MINVALUE) TO ('2024-01-01 00:00:00');

-- Partition for 2024
CREATE TABLE loans_2024 PARTITION OF loans
    FOR VALUES FROM ('2024-01-01 00:00:00') TO ('2025-01-01 00:00:00');

-- Partition for 2025
CREATE TABLE loans_2025 PARTITION OF loans
    FOR VALUES FROM ('2025-01-01 00:00:00') TO ('2026-01-01 00:00:00');

-- Default partition for future years or unexpected values
CREATE TABLE loans_default PARTITION OF loans DEFAULT;

-- 4. Copy data from the old table to the new one
INSERT INTO loans (id, user_id, book_id, borrowed_at, due_date, returned_at, status)
SELECT id, user_id, book_id, borrowed_at, due_date, returned_at, status
FROM loans_old;

-- 5. Drop the old table (this effectively removes the old indexes and constraints)
DROP TABLE loans_old;

-- 6. Re-create indexes
-- Note: Indexes are created on the parent table and automatically propagate to partitions
CREATE INDEX idx_loans_user_id ON loans(user_id);
CREATE INDEX idx_loans_book_id ON loans(book_id);
CREATE INDEX idx_loans_status ON loans(status);

-- 7. Re-create the view
CREATE VIEW loan_statistics AS
SELECT 
    u.id AS user_id,
    u.name AS user_name,
    u.email,
    COUNT(l.id) AS total_loans,
    SUM(CASE WHEN l.status = 'ACTIVE' THEN 1 ELSE 0 END) AS active_loans,
    SUM(CASE WHEN l.status = 'OVERDUE' THEN 1 ELSE 0 END) AS overdue_loans,
    SUM(CASE WHEN l.status = 'RETURNED' THEN 1 ELSE 0 END) AS returned_loans
FROM users u
LEFT JOIN loans l ON u.id = l.user_id
GROUP BY u.id, u.name, u.email;
