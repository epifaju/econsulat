-- Script de correction simplifié pour la création de demandes
-- Ce script évite les erreurs de contraintes existantes

-- 1. Vérifier et insérer les données de référence manquantes
DO $$
BEGIN
    -- Insérer des civilités si elles n'existent pas
    IF NOT EXISTS (SELECT 1 FROM civilites WHERE libelle = 'Monsieur') THEN
        INSERT INTO civilites (libelle) VALUES ('Monsieur');
        RAISE NOTICE 'Civilité Monsieur ajoutée';
    ELSE
        RAISE NOTICE 'Civilité Monsieur existe déjà';
    END IF;
    
    IF NOT EXISTS (SELECT 1 FROM civilites WHERE libelle = 'Madame') THEN
        INSERT INTO civilites (libelle) VALUES ('Madame');
        RAISE NOTICE 'Civilité Madame ajoutée';
    ELSE
        RAISE NOTICE 'Civilité Madame existe déjà';
    END IF;
    
    IF NOT EXISTS (SELECT 1 FROM civilites WHERE libelle = 'Mademoiselle') THEN
        INSERT INTO civilites (libelle) VALUES ('Mademoiselle');
        RAISE NOTICE 'Civilité Mademoiselle ajoutée';
    ELSE
        RAISE NOTICE 'Civilité Mademoiselle existe déjà';
    END IF;
END $$;

-- 2. Insérer des pays si aucun n'existe
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Guinée-Bissau') THEN
        INSERT INTO pays (libelle) VALUES ('Guinée-Bissau');
        RAISE NOTICE 'Pays Guinée-Bissau ajouté';
    ELSE
        RAISE NOTICE 'Pays Guinée-Bissau existe déjà';
    END IF;
    
    IF NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'France') THEN
        INSERT INTO pays (libelle) VALUES ('France');
        RAISE NOTICE 'Pays France ajouté';
    ELSE
        RAISE NOTICE 'Pays France existe déjà';
    END IF;
    
    IF NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Portugal') THEN
        INSERT INTO pays (libelle) VALUES ('Portugal');
        RAISE NOTICE 'Pays Portugal ajouté';
    ELSE
        RAISE NOTICE 'Pays Portugal existe déjà';
    END IF;
    
    IF NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Sénégal') THEN
        INSERT INTO pays (libelle) VALUES ('Sénégal');
        RAISE NOTICE 'Pays Sénégal ajouté';
    ELSE
        RAISE NOTICE 'Pays Sénégal existe déjà';
    END IF;
    
    IF NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Mali') THEN
        INSERT INTO pays (libelle) VALUES ('Mali');
        RAISE NOTICE 'Pays Mali ajouté';
    ELSE
        RAISE NOTICE 'Pays Mali existe déjà';
    END IF;
END $$;

-- 3. Insérer des types de documents si aucun n'existe
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM document_types WHERE libelle = 'Passeport') THEN
        INSERT INTO document_types (libelle, description, is_active) VALUES ('Passeport', 'Document de voyage officiel', true);
        RAISE NOTICE 'Type de document Passeport ajouté';
    ELSE
        RAISE NOTICE 'Type de document Passeport existe déjà';
    END IF;
    
    IF NOT EXISTS (SELECT 1 FROM document_types WHERE libelle = 'Acte de naissance') THEN
        INSERT INTO document_types (libelle, description, is_active) VALUES ('Acte de naissance', 'Certificat de naissance officiel', true);
        RAISE NOTICE 'Type de document Acte de naissance ajouté';
    ELSE
        RAISE NOTICE 'Type de document Acte de naissance existe déjà';
    END IF;
    
    IF NOT EXISTS (SELECT 1 FROM document_types WHERE libelle = 'Certificat de mariage') THEN
        INSERT INTO document_types (libelle, description, is_active) VALUES ('Certificat de mariage', 'Certificat de mariage officiel', true);
        RAISE NOTICE 'Type de document Certificat de mariage ajouté';
    ELSE
        RAISE NOTICE 'Type de document Certificat de mariage existe déjà';
    END IF;
    
    IF NOT EXISTS (SELECT 1 FROM document_types WHERE libelle = 'Carte d''identité') THEN
        INSERT INTO document_types (libelle, description, is_active) VALUES ('Carte d''identité', 'Carte d''identité nationale', true);
        RAISE NOTICE 'Type de document Carte d''identité ajouté';
    ELSE
        RAISE NOTICE 'Type de document Carte d''identité existe déjà';
    END IF;
    
    IF NOT EXISTS (SELECT 1 FROM document_types WHERE libelle = 'Certificat d''hébergement') THEN
        INSERT INTO document_types (libelle, description, is_active) VALUES ('Certificat d''hébergement', 'Certificat d''hébergement', true);
        RAISE NOTICE 'Type de document Certificat d''hébergement ajouté';
    ELSE
        RAISE NOTICE 'Type de document Certificat d''hébergement existe déjà';
    END IF;
END $$;

-- 4. Vérifier que toutes les colonnes nécessaires existent
DO $$
BEGIN
    -- Vérifier la colonne document_type_id dans demandes
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'demandes' AND column_name = 'document_type_id'
    ) THEN
        ALTER TABLE demandes ADD COLUMN document_type_id INTEGER;
        RAISE NOTICE 'Colonne document_type_id ajoutée à la table demandes';
    ELSE
        RAISE NOTICE 'Colonne document_type_id existe déjà';
    END IF;
END $$;

-- 5. Vérifier que toutes les colonnes nécessaires existent dans adresses
DO $$
BEGIN
    -- Vérifier la colonne street_name dans adresses
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'adresses' AND column_name = 'street_name'
    ) THEN
        ALTER TABLE adresses ADD COLUMN street_name VARCHAR(255);
        RAISE NOTICE 'Colonne street_name ajoutée à la table adresses';
    ELSE
        RAISE NOTICE 'Colonne street_name existe déjà';
    END IF;
    
    -- Vérifier la colonne street_number dans adresses
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'adresses' AND column_name = 'street_number'
    ) THEN
        ALTER TABLE adresses ADD COLUMN street_number VARCHAR(20);
        RAISE NOTICE 'Colonne street_number ajoutée à la table adresses';
    ELSE
        RAISE NOTICE 'Colonne street_number existe déjà';
    END IF;
    
    -- Vérifier la colonne box_number dans adresses
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'adresses' AND column_name = 'box_number'
    ) THEN
        ALTER TABLE adresses ADD COLUMN box_number VARCHAR(20);
        RAISE NOTICE 'Colonne box_number ajoutée à la table adresses';
    ELSE
        RAISE NOTICE 'Colonne box_number existe déjà';
    END IF;
    
    -- Vérifier la colonne city dans adresses
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'adresses' AND column_name = 'city'
    ) THEN
        ALTER TABLE adresses ADD COLUMN city VARCHAR(100);
        RAISE NOTICE 'Colonne city ajoutée à la table adresses';
    ELSE
        RAISE NOTICE 'Colonne city existe déjà';
    END IF;
    
    -- Vérifier la colonne postal_code dans adresses
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'adresses' AND column_name = 'postal_code'
    ) THEN
        ALTER TABLE adresses ADD COLUMN postal_code VARCHAR(20);
        RAISE NOTICE 'Colonne postal_code ajoutée à la table adresses';
    ELSE
        RAISE NOTICE 'Colonne postal_code existe déjà';
    END IF;
    
    -- Vérifier la colonne country_id dans adresses
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'adresses' AND column_name = 'country_id'
    ) THEN
        ALTER TABLE adresses ADD COLUMN country_id INTEGER;
        RAISE NOTICE 'Colonne country_id ajoutée à la table adresses';
    ELSE
        RAISE NOTICE 'Colonne country_id existe déjà';
    END IF;
END $$;

-- 6. Afficher un résumé de l'état actuel
SELECT '=== RÉSUMÉ DE L''ÉTAT ACTUEL ===' as info;

SELECT 'Structure des tables:' as section;
SELECT 
    table_name,
    COUNT(*) as colonnes
FROM information_schema.columns 
WHERE table_name IN ('demandes', 'adresses', 'document_types', 'civilites', 'pays')
GROUP BY table_name
ORDER BY table_name;

SELECT 'Données de référence:' as section;
SELECT 'Civilités:' as type, COUNT(*) as count FROM civilites
UNION ALL
SELECT 'Pays:' as type, COUNT(*) as count FROM pays
UNION ALL
SELECT 'Types de documents:' as type, COUNT(*) as count FROM document_types;

SELECT '=== SCRIPT TERMINÉ AVEC SUCCÈS ===' as info;
