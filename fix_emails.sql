-- Script de correction des emails pour eConsulat
-- Ce script met à jour les emails existants avec le bon format

-- Mettre à jour les emails existants
UPDATE users SET 
    email = CASE 
        WHEN email = 'admin' THEN 'admin@econsulat.com'
        WHEN email = 'user' THEN 'user@econsulat.com'
        ELSE email
    END
WHERE email IN ('admin', 'user');

-- Vérifier les modifications
SELECT 'Emails corrigés:' as status;
SELECT id, first_name, last_name, email, role, email_verified, created_at FROM users; 