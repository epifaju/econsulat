-- Script de diagnostic pour la création de demandes
-- Exécuter ce script pour identifier les problèmes

-- 1. Vérifier la structure de la table demandes
SELECT 
    column_name,
    data_type,
    is_nullable,
    column_default
FROM information_schema.columns 
WHERE table_name = 'demandes' 
ORDER BY ordinal_position;

-- 2. Vérifier la structure de la table document_types
SELECT 
    column_name,
    data_type,
    is_nullable,
    column_default
FROM information_schema.columns 
WHERE table_name = 'document_types' 
ORDER BY ordinal_position;

-- 3. Vérifier les données dans document_types
SELECT * FROM document_types ORDER BY id;

-- 4. Vérifier les données dans civilites
SELECT * FROM civilites ORDER BY id;

-- 5. Vérifier les données dans pays
SELECT * FROM pays ORDER BY id LIMIT 10;

-- 6. Vérifier les contraintes de clés étrangères
SELECT 
    tc.table_name, 
    kcu.column_name, 
    ccu.table_name AS foreign_table_name,
    ccu.column_name AS foreign_column_name 
FROM 
    information_schema.table_constraints AS tc 
    JOIN information_schema.key_column_usage AS kcu
      ON tc.constraint_name = kcu.constraint_name
      AND tc.table_schema = kcu.table_schema
    JOIN information_schema.constraint_column_usage AS ccu
      ON ccu.constraint_name = tc.constraint_name
      AND ccu.table_schema = tc.table_schema
WHERE tc.constraint_type = 'FOREIGN KEY' 
    AND tc.table_name = 'demandes';

-- 7. Vérifier s'il y a des demandes existantes
SELECT COUNT(*) as total_demandes FROM demandes;

-- 8. Vérifier la structure de la table adresses
SELECT 
    column_name,
    data_type,
    is_nullable,
    column_default
FROM information_schema.columns 
WHERE table_name = 'adresses' 
ORDER BY ordinal_position;

-- 9. Vérifier les utilisateurs existants
SELECT id, email, first_name, last_name, role FROM users LIMIT 10;

-- 10. Vérifier les séquences
SELECT 
    sequence_name,
    last_value,
    increment_by
FROM information_schema.sequences 
WHERE sequence_name LIKE '%demandes%' 
   OR sequence_name LIKE '%adresses%'
   OR sequence_name LIKE '%document_types%';

-- 11. Vérifier les index sur la table demandes
SELECT 
    indexname,
    indexdef
FROM pg_indexes 
WHERE tablename = 'demandes';

-- 12. Vérifier les permissions sur les tables
SELECT 
    schemaname,
    tablename,
    tableowner,
    hasindexes,
    hasrules,
    hastriggers
FROM pg_tables 
WHERE tablename IN ('demandes', 'document_types', 'civilites', 'pays', 'adresses');
