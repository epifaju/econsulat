-- Script pour créer un utilisateur citoyen avec le rôle CITIZEN
-- Mot de passe: citizen123 (hashé avec BCrypt)

INSERT INTO users (email, password, first_name, last_name, role, email_verified, created_at)
VALUES (
    'citizen@econsulat.com',
    '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', -- password: citizen123
    'Jean',
    'Dupont',
    'CITIZEN',
    true,
    CURRENT_TIMESTAMP
);

-- Vérification
SELECT id, email, first_name, last_name, role, email_verified FROM users WHERE email = 'citizen@econsulat.com'; 