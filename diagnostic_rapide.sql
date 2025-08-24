-- Script de diagnostic rapide pour voir l'état actuel
-- Exécuter ce script pour identifier ce qui manque

-- 1. Vérifier la structure actuelle de la table demandes
SELECT '=== STRUCTURE TABLE DEMANDES ===' as info;
SELECT 
    column_name,
    data_type,
    is_nullable,
    column_default
FROM information_schema.columns 
WHERE table_name = 'demandes' 
ORDER BY ordinal_position;

-- 2. Vérifier les contraintes existantes sur demandes
SELECT '=== CONTRAINTES EXISTANTES SUR DEMANDES ===' as info;
SELECT 
    constraint_name,
    constraint_type,
    column_name
FROM information_schema.table_constraints tc
JOIN information_schema.key_column_usage kcu ON tc.constraint_name = kcu.constraint_name
WHERE tc.table_name = 'demandes'
ORDER BY constraint_type, column_name;

-- 3. Vérifier les données de référence
SELECT '=== DONNÉES DE RÉFÉRENCE ===' as info;

SELECT 'Civilités:' as type, COUNT(*) as count FROM civilites;
SELECT 'Pays:' as type, COUNT(*) as count FROM pays;
SELECT 'Types de documents:' as type, COUNT(*) as count FROM document_types;

-- 4. Vérifier s'il y a des demandes existantes
SELECT '=== DEMANDES EXISTANTES ===' as info;
SELECT COUNT(*) as total_demandes FROM demandes;

-- 5. Vérifier la structure de la table adresses
SELECT '=== STRUCTURE TABLE ADRESSES ===' as info;
SELECT 
    column_name,
    data_type,
    is_nullable,
    column_default
FROM information_schema.columns 
WHERE table_name = 'adresses' 
ORDER BY ordinal_position;

-- 6. Vérifier les utilisateurs
SELECT '=== UTILISATEURS ===' as info;
SELECT id, email, first_name, last_name, role FROM users LIMIT 5;

-- 7. Test de création d'une adresse simple
SELECT '=== TEST CRÉATION ADRESSE ===' as info;
DO $$
DECLARE
    new_adresse_id INTEGER;
BEGIN
    -- Créer une adresse de test
    INSERT INTO adresses (street_name, street_number, city, postal_code, country_id) 
    VALUES ('Rue de Test', '123', 'Ville Test', '12345', 1)
    RETURNING id INTO new_adresse_id;
    
    RAISE NOTICE 'Adresse de test créée avec ID: %', new_adresse_id;
    
    -- Supprimer l'adresse de test
    DELETE FROM adresses WHERE id = new_adresse_id;
    RAISE NOTICE 'Adresse de test supprimée';
    
EXCEPTION WHEN OTHERS THEN
    RAISE NOTICE 'Erreur lors du test: %', SQLERRM;
END $$;
