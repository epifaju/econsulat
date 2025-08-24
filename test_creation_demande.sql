-- Script de test pour la création de demandes
-- Ce script teste chaque étape de la création

-- 1. Vérifier qu'un utilisateur existe
SELECT 'Test utilisateur:' as info;
SELECT id, username, email, role FROM users LIMIT 1;

-- 2. Vérifier qu'une civilité existe
SELECT 'Test civilité:' as info;
SELECT id, libelle FROM civilites LIMIT 1;

-- 3. Vérifier qu'un pays existe
SELECT 'Test pays:' as info;
SELECT id, libelle FROM pays LIMIT 1;

-- 4. Vérifier qu'un type de document existe
SELECT 'Test type de document:' as info;
SELECT id, libelle, is_active FROM document_types WHERE is_active = true LIMIT 1;

-- 5. Test de création d'une adresse
SELECT 'Test création adresse:' as info;
DO $$
DECLARE
    adresse_id INTEGER;
    pays_id INTEGER;
BEGIN
    -- Récupérer un pays
    SELECT id INTO pays_id FROM pays LIMIT 1;
    
    IF pays_id IS NULL THEN
        RAISE EXCEPTION 'Aucun pays trouvé dans la base';
    END IF;
    
    -- Créer une adresse de test
    INSERT INTO adresses (street_name, street_number, postal_code, city, country_id)
    VALUES ('Rue de Test', '123', '75001', 'Paris', pays_id)
    RETURNING id INTO adresse_id;
    
    RAISE NOTICE '✅ Adresse créée avec succès, ID: %', adresse_id;
    
    -- Nettoyer
    DELETE FROM adresses WHERE id = adresse_id;
    RAISE NOTICE '🧹 Adresse de test supprimée';
    
EXCEPTION WHEN OTHERS THEN
    RAISE NOTICE '❌ Erreur lors de la création d''adresse: %', SQLERRM;
END $$;

-- 6. Test de création d'une demande complète
SELECT 'Test création demande complète:' as info;
DO $$
DECLARE
    user_id INTEGER;
    civilite_id INTEGER;
    pays_id INTEGER;
    document_type_id INTEGER;
    adresse_id INTEGER;
    demande_id INTEGER;
BEGIN
    -- Récupérer les données nécessaires
    SELECT id INTO user_id FROM users LIMIT 1;
    SELECT id INTO civilite_id FROM civilites LIMIT 1;
    SELECT id INTO pays_id FROM pays LIMIT 1;
    SELECT id INTO document_type_id FROM document_types WHERE is_active = true LIMIT 1;
    
    -- Vérifications
    IF user_id IS NULL THEN RAISE EXCEPTION 'Aucun utilisateur trouvé'; END IF;
    IF civilite_id IS NULL THEN RAISE EXCEPTION 'Aucune civilité trouvée'; END IF;
    IF pays_id IS NULL THEN RAISE EXCEPTION 'Aucun pays trouvé'; END IF;
    IF document_type_id IS NULL THEN RAISE EXCEPTION 'Aucun type de document trouvé'; END IF;
    
    RAISE NOTICE '📋 Données récupérées - User: %, Civilité: %, Pays: %, Type: %', 
                 user_id, civilite_id, pays_id, document_type_id;
    
    -- Créer une adresse
    INSERT INTO adresses (street_name, street_number, postal_code, city, country_id)
    VALUES ('Rue de Test', '123', '75001', 'Paris', pays_id)
    RETURNING id INTO adresse_id;
    
    RAISE NOTICE '🏠 Adresse créée, ID: %', adresse_id;
    
    -- Créer une demande
    INSERT INTO demandes (
        civilite_id, first_name, last_name, birth_date, birth_place, birth_country_id,
        adresse_id, father_first_name, father_last_name, father_birth_date, 
        father_birth_place, father_birth_country_id, mother_first_name, mother_last_name,
        mother_birth_date, mother_birth_place, mother_birth_country_id,
        document_type_id, user_id, status, created_at, updated_at
    ) VALUES (
        civilite_id, 'Jean', 'Dupont', '1990-01-01', 'Paris', pays_id,
        adresse_id, 'Pierre', 'Dupont', '1960-01-01', 'Paris', pays_id,
        'Marie', 'Dupont', '1965-01-01', 'Paris', pays_id,
        document_type_id, user_id, 'PENDING', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    ) RETURNING id INTO demande_id;
    
    RAISE NOTICE '✅ Demande créée avec succès, ID: %', demande_id;
    
    -- Vérifier que la demande a été créée
    SELECT COUNT(*) INTO demande_id FROM demandes WHERE id = demande_id;
    RAISE NOTICE '🔍 Vérification: % demande(s) trouvée(s)', demande_id;
    
    -- Nettoyer
    DELETE FROM demandes WHERE id = demande_id;
    DELETE FROM adresses WHERE id = adresse_id;
    RAISE NOTICE '🧹 Données de test supprimées';
    
EXCEPTION WHEN OTHERS THEN
    RAISE NOTICE '❌ Erreur lors de la création de la demande: %', SQLERRM;
    RAISE NOTICE '🔍 Détail de l''erreur: %', SQLSTATE;
    
    -- Nettoyer en cas d'erreur
    IF adresse_id IS NOT NULL THEN
        DELETE FROM adresses WHERE id = adresse_id;
    END IF;
END $$;

-- 7. Vérifier les contraintes de la table demandes
SELECT 'Contraintes sur la table demandes:' as info;
SELECT 
    tc.constraint_name,
    tc.constraint_type,
    kcu.column_name,
    ccu.table_name AS referenced_table,
    ccu.column_name AS referenced_column
FROM information_schema.table_constraints tc
JOIN information_schema.key_column_usage kcu 
    ON tc.constraint_name = kcu.constraint_name
LEFT JOIN information_schema.constraint_column_usage ccu 
    ON tc.constraint_name = ccu.constraint_name
WHERE tc.table_name = 'demandes'
ORDER BY tc.constraint_type, kcu.ordinal_position;
