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