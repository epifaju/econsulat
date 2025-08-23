-- Script de diagnostic et correction des utilisateurs admin/agent
-- Exécuter ce script pour résoudre les problèmes d'autorisation

-- 1. Vérifier l'état actuel des utilisateurs admin/agent
SELECT 
    id,
    email,
    role,
    email_verified,
    created_at,
    CASE 
        WHEN email_verified = true THEN '✅ Email vérifié'
        ELSE '❌ Email non vérifié'
    END as status_email,
    CASE 
        WHEN role IN ('ADMIN', 'AGENT') THEN '👑 Rôle autorisé'
        ELSE '🚫 Rôle insuffisant'
    END as status_role
FROM users 
WHERE role IN ('ADMIN', 'AGENT')
ORDER BY role, email_verified DESC;

-- 2. Vérifier le nombre total d'utilisateurs par rôle
SELECT 
    role,
    COUNT(*) as total_users,
    COUNT(CASE WHEN email_verified = true THEN 1 END) as verified_users,
    COUNT(CASE WHEN email_verified = false THEN 1 END) as unverified_users
FROM users 
GROUP BY role
ORDER BY role;

-- 3. Corriger les utilisateurs admin/agent (décommenter si nécessaire)
-- UPDATE users 
-- SET email_verified = true 
-- WHERE role IN ('ADMIN', 'AGENT') AND email_verified = false;

-- 4. Créer un utilisateur admin de test si aucun n'existe
-- INSERT INTO users (first_name, last_name, email, password, role, email_verified, created_at)
-- SELECT 
--     'Admin',
--     'Test',
--     'admin@test.com',
--     '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', -- password: password
--     'ADMIN',
--     true,
--     NOW()
-- WHERE NOT EXISTS (SELECT 1 FROM users WHERE role = 'ADMIN');

-- 5. Vérifier les contraintes de la table users
SELECT 
    constraint_name,
    constraint_type,
    table_name
FROM information_schema.table_constraints 
WHERE table_name = 'users';

-- 6. Vérifier les index sur la table users
SELECT 
    indexname,
    indexdef
FROM pg_indexes 
WHERE tablename = 'users';

-- 7. Vérifier les permissions sur la table users
SELECT 
    grantee,
    privilege_type,
    is_grantable
FROM information_schema.role_table_grants 
WHERE table_name = 'users';

-- 8. Test de connexion pour un utilisateur admin spécifique
-- SELECT 
--     u.id,
--     u.email,
--     u.role,
--     u.email_verified,
--     'ROLE_' || u.role as spring_security_role
-- FROM users u 
-- WHERE u.email = 'admin@test.com'; -- Remplacer par l'email de l'admin à tester
