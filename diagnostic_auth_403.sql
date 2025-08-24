-- ========================================
-- Diagnostic Erreur 403 - eConsulat
-- ========================================

-- 1. Vérification de la structure de la table users
\echo '=== STRUCTURE DE LA TABLE USERS ==='
\d users

-- 2. Vérification du contenu de la table users
\echo '=== CONTENU DE LA TABLE USERS ==='
SELECT 
    id,
    email,
    role,
    enabled,
    created_at,
    updated_at
FROM users 
ORDER BY id;

-- 3. Vérification des rôles disponibles
\echo '=== ROLES DISPONIBLES ==='
SELECT DISTINCT role, COUNT(*) as count
FROM users 
GROUP BY role
ORDER BY role;

-- 4. Vérification des utilisateurs activés/désactivés
\echo '=== UTILISATEURS PAR STATUT ==='
SELECT 
    enabled,
    COUNT(*) as count
FROM users 
GROUP BY enabled
ORDER BY enabled;

-- 5. Vérification des utilisateurs sans rôle
\echo '=== UTILISATEURS SANS ROLE ==='
SELECT 
    id,
    email,
    role,
    enabled
FROM users 
WHERE role IS NULL OR role = '';

-- 6. Vérification des utilisateurs avec des rôles invalides
\echo '=== ROLES INVALIDES ==='
SELECT 
    id,
    email,
    role,
    enabled
FROM users 
WHERE role NOT IN ('USER', 'ADMIN', 'AGENT', 'CITIZEN')
    AND role IS NOT NULL;

-- 7. Vérification de la table des demandes
\echo '=== STRUCTURE DE LA TABLE DEMANDES ==='
\d demandes

-- 8. Vérification des permissions sur les tables
\echo '=== PERMISSIONS SUR LES TABLES ==='
SELECT 
    schemaname,
    tablename,
    tableowner,
    hasinsert,
    hasselect,
    hasupdate,
    hasdelete
FROM pg_tables 
WHERE schemaname = 'public' 
    AND tablename IN ('users', 'demandes', 'adresses', 'civilites', 'pays', 'document_types');

-- 9. Vérification des séquences
\echo '=== SEQUENCES ==='
SELECT 
    sequence_name,
    last_value,
    is_called
FROM information_schema.sequences 
WHERE sequence_schema = 'public';

-- 10. Création d'un utilisateur de test si nécessaire
\echo '=== CREATION UTILISATEUR DE TEST ==='
DO $$
BEGIN
    -- Vérifier si l'utilisateur de test existe
    IF NOT EXISTS (SELECT 1 FROM users WHERE email = 'user@test.com') THEN
        -- Insérer un utilisateur de test
        INSERT INTO users (email, password, role, enabled, created_at, updated_at)
        VALUES (
            'user@test.com',
            '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', -- password123
            'USER',
            true,
            NOW(),
            NOW()
        );
        
        RAISE NOTICE 'Utilisateur de test créé: user@test.com / password123';
    ELSE
        RAISE NOTICE 'Utilisateur de test existe déjà: user@test.com';
    END IF;
    
    -- Vérifier si l'admin existe
    IF NOT EXISTS (SELECT 1 FROM users WHERE email = 'admin@econsulat.com') THEN
        -- Insérer un admin de test
        INSERT INTO users (email, password, role, enabled, created_at, updated_at)
        VALUES (
            'admin@econsulat.com',
            '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', -- password123
            'ADMIN',
            true,
            NOW(),
            NOW()
        );
        
        RAISE NOTICE 'Admin de test créé: admin@econsulat.com / password123';
    ELSE
        RAISE NOTICE 'Admin de test existe déjà: admin@econsulat.com';
    END IF;
END $$;

-- 11. Vérification finale des utilisateurs
\echo '=== VERIFICATION FINALE ==='
SELECT 
    id,
    email,
    role,
    enabled,
    created_at
FROM users 
ORDER BY role, email;

-- 12. Test de connexion avec l'utilisateur de test
\echo '=== TEST DE CONNEXION ==='
\echo 'Pour tester l''authentification, utilisez:'
\echo 'Email: user@test.com'
\echo 'Mot de passe: password123'
\echo ''
\echo 'Ou pour l''admin:'
\echo 'Email: admin@econsulat.com'
\echo 'Mot de passe: password123'

-- 13. Vérification des contraintes de sécurité
\echo '=== CONTRAINTES DE SECURITE ==='
SELECT 
    tc.constraint_name,
    tc.table_name,
    kcu.column_name,
    tc.constraint_type
FROM information_schema.table_constraints tc
JOIN information_schema.key_column_usage kcu 
    ON tc.constraint_name = kcu.constraint_name
WHERE tc.table_schema = 'public' 
    AND tc.table_name IN ('users', 'demandes')
ORDER BY tc.table_name, tc.constraint_type;

