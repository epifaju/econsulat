-- Script pour ajouter la colonne user_id à la table citizens
-- et associer les demandes existantes à l'utilisateur admin

-- 1. Ajouter la colonne user_id
ALTER TABLE citizens ADD COLUMN user_id BIGINT;

-- 2. Ajouter la contrainte de clé étrangère
ALTER TABLE citizens ADD CONSTRAINT fk_citizens_user 
    FOREIGN KEY (user_id) REFERENCES users(id);

-- 3. Associer les demandes existantes à l'utilisateur admin (si il existe)
-- Remplacer 'admin@econsulat.com' par l'email de l'admin si différent
UPDATE citizens 
SET user_id = (SELECT id FROM users WHERE email = 'admin@econsulat.com' LIMIT 1)
WHERE user_id IS NULL;

-- 4. Rendre la colonne user_id NOT NULL après avoir mis à jour les données
ALTER TABLE citizens ALTER COLUMN user_id SET NOT NULL; 