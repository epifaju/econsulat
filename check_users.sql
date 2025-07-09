-- Vérifier les utilisateurs et leurs rôles
SELECT id, email, first_name, last_name, role, email_verified, created_at 
FROM users 
ORDER BY created_at DESC;

-- Vérifier les citoyens et leurs utilisateurs associés
SELECT c.id, c.first_name, c.last_name, c.document_type, c.status, c.user_id,
       u.email, u.role
FROM citizens c
LEFT JOIN users u ON c.user_id = u.id
ORDER BY c.created_at DESC; 