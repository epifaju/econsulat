-- Script pour vérifier la structure de la table users
-- Exécutez ce script pour voir quelles colonnes existent

-- 1. Vérifier si la table users existe
SELECT 'Existence de la table users:' as info;
SELECT EXISTS (
    SELECT FROM information_schema.tables 
    WHERE table_schema = 'public' 
    AND table_name = 'users'
) as table_exists;

-- 2. Vérifier la structure de la table users
SELECT 'Structure de la table users:' as info;
SELECT column_name, data_type, is_nullable, column_default
FROM information_schema.columns 
WHERE table_name = 'users' 
ORDER BY ordinal_position;

-- 3. Vérifier les données dans users
SELECT 'Données dans users (premiers 5):' as info;
SELECT * FROM users LIMIT 5;

-- 4. Vérifier les contraintes sur users
SELECT 'Contraintes sur users:' as info;
SELECT 
    tc.constraint_name,
    tc.constraint_type,
    kcu.column_name
FROM information_schema.table_constraints tc
JOIN information_schema.key_column_usage kcu 
    ON tc.constraint_name = kcu.constraint_name
WHERE tc.table_name = 'users'
ORDER BY tc.constraint_type, kcu.ordinal_position;
