-- Script pour mettre à jour la contrainte de rôle pour inclure CITIZEN

-- Supprimer l'ancienne contrainte
ALTER TABLE users DROP CONSTRAINT users_role_check;

-- Ajouter la nouvelle contrainte avec CITIZEN
ALTER TABLE users ADD CONSTRAINT users_role_check 
CHECK (role::text = ANY (ARRAY['USER'::character varying, 'ADMIN'::character varying, 'AGENT'::character varying, 'CITIZEN'::character varying]::text[]));

-- Vérifier la nouvelle contrainte
SELECT conname, pg_get_constraintdef(oid) 
FROM pg_constraint 
WHERE conname = 'users_role_check'; 