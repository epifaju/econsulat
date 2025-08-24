-- Script de diagnostic pour la création de demandes
-- Exécutez ce script pour identifier les problèmes

-- 1. Vérifier la structure de la table demandes
SELECT 'Structure de la table demandes:' as info;
SELECT column_name, data_type, is_nullable, column_default
FROM information_schema.columns 
WHERE table_name = 'demandes' 
ORDER BY ordinal_position;

-- 2. Vérifier la structure de la table document_types
SELECT 'Structure de la table document_types:' as info;
SELECT column_name, data_type, is_nullable, column_default
FROM information_schema.columns 
WHERE table_name = 'document_types' 
ORDER BY ordinal_position;

-- 3. Vérifier les données dans document_types
SELECT 'Types de documents disponibles:' as info;
SELECT id, libelle, description, is_active, created_at, updated_at
FROM document_types 
ORDER BY id;

-- 4. Vérifier les contraintes de clé étrangère
SELECT 'Contraintes de clé étrangère sur demandes:' as info;
SELECT 
    tc.constraint_name,
    tc.table_name,
    kcu.column_name,
    ccu.table_name AS foreign_table_name,
    ccu.column_name AS foreign_column_name
FROM information_schema.table_constraints AS tc
JOIN information_schema.key_column_usage AS kcu
    ON tc.constraint_name = kcu.constraint_name
JOIN information_schema.constraint_column_usage AS ccu
    ON ccu.constraint_name = tc.constraint_name
WHERE tc.constraint_type = 'FOREIGN KEY' 
    AND tc.table_name = 'demandes';

-- 5. Vérifier les séquences
SELECT 'Séquences:' as info;
SELECT sequence_name, last_value, increment_by
FROM information_schema.sequences
WHERE sequence_name LIKE '%demandes%'
   OR sequence_name LIKE '%adresses%'
   OR sequence_name LIKE '%document_types%';

-- 6. Vérifier les données existantes
SELECT 'Demandes existantes:' as info;
SELECT COUNT(*) as total_demandes FROM demandes;

-- 7. Vérifier les adresses existantes
SELECT 'Adresses existantes:' as info;
SELECT COUNT(*) as total_adresses FROM adresses;

-- 8. Vérifier les utilisateurs
SELECT 'Utilisateurs existants:' as info;
SELECT COUNT(*) as total_users FROM users;

-- 9. Vérifier les civilités
SELECT 'Civilités disponibles:' as info;
SELECT * FROM civilites ORDER BY id;

-- 10. Vérifier les pays
SELECT 'Pays disponibles (premiers 10):' as info;
SELECT id, libelle FROM pays ORDER BY id LIMIT 10;

-- 11. Test de création d'une adresse
SELECT 'Test de création d''adresse:' as info;
DO $$
DECLARE
    adresse_id INTEGER;
BEGIN
    INSERT INTO adresses (street_name, street_number, postal_code, city, country_id)
    VALUES ('Rue Test', '123', '75001', 'Paris', 1)
    RETURNING id INTO adresse_id;
    
    RAISE NOTICE 'Adresse créée avec ID: %', adresse_id;
    
    -- Nettoyer le test
    DELETE FROM adresses WHERE id = adresse_id;
    
EXCEPTION WHEN OTHERS THEN
    RAISE NOTICE 'Erreur lors de la création d''adresse: %', SQLERRM;
END $$;

-- 12. Vérifier les permissions
SELECT 'Permissions sur les tables:' as info;
SELECT table_name, privilege_type
FROM information_schema.table_privileges
WHERE table_name IN ('demandes', 'adresses', 'document_types', 'civilites', 'pays', 'users')
ORDER BY table_name, privilege_type;
