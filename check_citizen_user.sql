-- Vérifier si l'utilisateur citizen existe
SELECT email, role, first_name, last_name FROM users WHERE email = 'citizen@econsulat.com';

-- Si l'utilisateur n'existe pas, le créer
INSERT INTO users (email, password, first_name, last_name, role, email_verified, created_at)
SELECT 'citizen@econsulat.com', 
       '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', -- password
       'Citizen', 
       'User', 
       'CITIZEN', 
       true, 
       NOW()
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'citizen@econsulat.com');

-- Vérifier tous les utilisateurs et leurs rôles
SELECT email, role, first_name, last_name FROM users ORDER BY email; 