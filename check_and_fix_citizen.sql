-- Script pour vérifier et corriger l'utilisateur citizen
-- Mot de passe: citizen123 (hashé avec BCrypt)

-- Vérifier si l'utilisateur existe
SELECT 'Vérification de l''utilisateur citizen:' as info;
SELECT id, email, first_name, last_name, role, email_verified 
FROM users 
WHERE email = 'citizen@econsulat.com';

-- Supprimer l'ancien utilisateur citizen s'il existe
DELETE FROM users WHERE email = 'citizen@econsulat.com';

-- Créer un nouvel utilisateur citizen avec le bon mot de passe
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

-- Vérifier que l'utilisateur a été créé
SELECT 'Utilisateur citizen créé:' as info;
SELECT id, email, first_name, last_name, role, email_verified 
FROM users 
WHERE email = 'citizen@econsulat.com';

-- Afficher tous les utilisateurs pour vérification
SELECT 'Tous les utilisateurs:' as info;
SELECT id, email, first_name, last_name, role, email_verified 
FROM users 
ORDER BY role, email; 