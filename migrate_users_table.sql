-- Script de migration de la table users pour eConsulat
-- Ce script préserve les données existantes et ajoute les nouvelles colonnes

-- 1. Sauvegarder les données existantes
CREATE TEMP TABLE users_backup AS SELECT * FROM users;

-- 2. Supprimer les contraintes existantes
ALTER TABLE users DROP CONSTRAINT IF EXISTS users_role_check;
ALTER TABLE users DROP CONSTRAINT IF EXISTS users_username_key;

-- 3. Ajouter les nouvelles colonnes
ALTER TABLE users ADD COLUMN IF NOT EXISTS first_name VARCHAR(100);
ALTER TABLE users ADD COLUMN IF NOT EXISTS last_name VARCHAR(100);
ALTER TABLE users ADD COLUMN IF NOT EXISTS email VARCHAR(150);
ALTER TABLE users ADD COLUMN IF NOT EXISTS email_verified BOOLEAN DEFAULT FALSE;
ALTER TABLE users ADD COLUMN IF NOT EXISTS created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

-- 4. Migrer les données existantes
-- Pour les utilisateurs existants, utiliser le username comme email et first_name
UPDATE users SET 
    email = username,
    first_name = CASE 
        WHEN username = 'admin' THEN 'Admin'
        WHEN username = 'user' THEN 'User'
        ELSE 'Utilisateur'
    END,
    last_name = CASE 
        WHEN username = 'admin' THEN 'System'
        WHEN username = 'user' THEN 'Test'
        ELSE 'Default'
    END,
    email_verified = TRUE
WHERE email IS NULL;

-- 5. Rendre les nouvelles colonnes obligatoires
ALTER TABLE users ALTER COLUMN first_name SET NOT NULL;
ALTER TABLE users ALTER COLUMN last_name SET NOT NULL;
ALTER TABLE users ALTER COLUMN email SET NOT NULL;
ALTER TABLE users ALTER COLUMN email_verified SET NOT NULL;
ALTER TABLE users ALTER COLUMN created_at SET NOT NULL;

-- 6. Supprimer l'ancienne colonne username
ALTER TABLE users DROP COLUMN username;

-- 7. Ajouter les nouvelles contraintes
ALTER TABLE users ADD CONSTRAINT users_email_key UNIQUE (email);
ALTER TABLE users ADD CONSTRAINT users_role_check CHECK (role IN ('USER', 'ADMIN', 'AGENT'));

-- 8. Créer un index sur l'email
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);

-- 9. Vérifier la migration
SELECT 'Migration terminée. Nouveaux utilisateurs:' as status;
SELECT id, first_name, last_name, email, role, email_verified, created_at FROM users;

-- 10. Nettoyer la table temporaire
DROP TABLE users_backup; 