-- Script de correction pour la création de demandes
-- Exécuter ce script pour résoudre les problèmes

-- 1. Vérifier et corriger la structure de la table demandes
-- Si la colonne document_type_id n'existe pas, l'ajouter
DO $$
BEGIN
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

-- 2. Vérifier et corriger la structure de la table adresses
-- Si la colonne street_name n'existe pas, l'ajouter
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'adresses' AND column_name = 'street_name'
    ) THEN
        ALTER TABLE adresses ADD COLUMN street_name VARCHAR(255);
        RAISE NOTICE 'Colonne street_name ajoutée à la table adresses';
    ELSE
        RAISE NOTICE 'Colonne street_name existe déjà';
    END IF;
END $$;

-- 3. Vérifier et corriger la structure de la table adresses
-- Si la colonne street_number n'existe pas, l'ajouter
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'adresses' AND column_name = 'street_number'
    ) THEN
        ALTER TABLE adresses ADD COLUMN street_number VARCHAR(20);
        RAISE NOTICE 'Colonne street_number ajoutée à la table adresses';
    ELSE
        RAISE NOTICE 'Colonne street_number existe déjà';
    END IF;
END $$;

-- 4. Vérifier et corriger la structure de la table adresses
-- Si la colonne box_number n'existe pas, l'ajouter
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'adresses' AND column_name = 'box_number'
    ) THEN
        ALTER TABLE adresses ADD COLUMN box_number VARCHAR(20);
        RAISE NOTICE 'Colonne box_number ajoutée à la table adresses';
    ELSE
        RAISE NOTICE 'Colonne box_number existe déjà';
    END IF;
END $$;

-- 5. Vérifier et corriger la structure de la table adresses
-- Si la colonne city n'existe pas, l'ajouter
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'adresses' AND column_name = 'city'
    ) THEN
        ALTER TABLE adresses ADD COLUMN city VARCHAR(100);
        RAISE NOTICE 'Colonne city ajoutée à la table adresses';
    ELSE
        RAISE NOTICE 'Colonne city existe déjà';
    END IF;
END $$;

-- 6. Vérifier et corriger la structure de la table adresses
-- Si la colonne postal_code n'existe pas, l'ajouter
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'adresses' AND column_name = 'postal_code'
    ) THEN
        ALTER TABLE adresses ADD COLUMN postal_code VARCHAR(20);
        RAISE NOTICE 'Colonne postal_code ajoutée à la table adresses';
    ELSE
        RAISE NOTICE 'Colonne postal_code existe déjà';
    END IF;
END $$;

-- 7. Vérifier et corriger la structure de la table adresses
-- Si la colonne country_id n'existe pas, l'ajouter
DO $$
BEGIN
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

-- 8. Ajouter les contraintes de clés étrangères si elles n'existent pas
DO $$
BEGIN
    -- Contrainte pour document_type_id
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.table_constraints 
        WHERE table_name = 'demandes' 
        AND constraint_name LIKE '%document_type_id%'
        AND constraint_type = 'FOREIGN KEY'
    ) THEN
        ALTER TABLE demandes 
        ADD CONSTRAINT fk_demandes_document_type 
        FOREIGN KEY (document_type_id) REFERENCES document_types(id);
        RAISE NOTICE 'Contrainte FK document_type_id ajoutée';
    ELSE
        RAISE NOTICE 'Contrainte FK document_type_id existe déjà';
    END IF;
END $$;

-- 9. Ajouter les contraintes de clés étrangères si elles n'existent pas
DO $$
BEGIN
    -- Contrainte pour adresse_id
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.table_constraints 
        WHERE table_name = 'demandes' 
        AND constraint_name LIKE '%adresse_id%'
        AND constraint_type = 'FOREIGN KEY'
    ) THEN
        ALTER TABLE demandes 
        ADD CONSTRAINT fk_demandes_adresse 
        FOREIGN KEY (adresse_id) REFERENCES adresses(id);
        RAISE NOTICE 'Contrainte FK adresse_id ajoutée';
    ELSE
        RAISE NOTICE 'Contrainte FK adresse_id existe déjà';
    END IF;
END $$;

-- 10. Ajouter les contraintes de clés étrangères si elles n'existent pas
DO $$
BEGIN
    -- Contrainte pour civilite_id
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.table_constraints 
        WHERE table_name = 'demandes' 
        AND constraint_name LIKE '%civilite_id%'
        AND constraint_type = 'FOREIGN KEY'
    ) THEN
        ALTER TABLE demandes 
        ADD CONSTRAINT fk_demandes_civilite 
        FOREIGN KEY (civilite_id) REFERENCES civilites(id);
        RAISE NOTICE 'Contrainte FK civilite_id ajoutée';
    ELSE
        RAISE NOTICE 'Contrainte FK civilite_id existe déjà';
    END IF;
END $$;

-- 11. Ajouter les contraintes de clés étrangères si elles n'existent pas
DO $$
BEGIN
    -- Contrainte pour birth_country_id
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.table_constraints 
        WHERE table_name = 'demandes' 
        AND constraint_name LIKE '%birth_country_id%'
        AND constraint_type = 'FOREIGN KEY'
    ) THEN
        ALTER TABLE demandes 
        ADD CONSTRAINT fk_demandes_birth_country 
        FOREIGN KEY (birth_country_id) REFERENCES pays(id);
        RAISE NOTICE 'Contrainte FK birth_country_id ajoutée';
    ELSE
        RAISE NOTICE 'Contrainte FK birth_country_id existe déjà';
    END IF;
END $$;

-- 12. Ajouter les contraintes de clés étrangères si elles n'existent pas
DO $$
BEGIN
    -- Contrainte pour father_birth_country_id
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.table_constraints 
        WHERE table_name = 'demandes' 
        AND constraint_name LIKE '%father_birth_country_id%'
        AND constraint_type = 'FOREIGN KEY'
    ) THEN
        ALTER TABLE demandes 
        ADD CONSTRAINT fk_demandes_father_birth_country 
        FOREIGN KEY (father_birth_country_id) REFERENCES pays(id);
        RAISE NOTICE 'Contrainte FK father_birth_country_id ajoutée';
    ELSE
        RAISE NOTICE 'Contrainte FK father_birth_country_id existe déjà';
    END IF;
END $$;

-- 13. Ajouter les contraintes de clés étrangères si elles n'existent pas
DO $$
BEGIN
    -- Contrainte pour mother_birth_country_id
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.table_constraints 
        WHERE table_name = 'demandes' 
        AND constraint_name LIKE '%mother_birth_country_id%'
        AND constraint_type = 'FOREIGN KEY'
    ) THEN
        ALTER TABLE demandes 
        ADD CONSTRAINT fk_demandes_mother_birth_country 
        FOREIGN KEY (mother_birth_country_id) REFERENCES pays(id);
        RAISE NOTICE 'Contrainte FK mother_birth_country_id ajoutée';
    ELSE
        RAISE NOTICE 'Contrainte FK mother_birth_country_id existe déjà';
    END IF;
END $$;

-- 14. Ajouter les contraintes de clés étrangères si elles n'existent pas
DO $$
BEGIN
    -- Contrainte pour user_id
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.table_constraints 
        WHERE table_name = 'demandes' 
        AND constraint_name LIKE '%user_id%'
        AND constraint_type = 'FOREIGN KEY'
    ) THEN
        ALTER TABLE demandes 
        ADD CONSTRAINT fk_demandes_user 
        FOREIGN KEY (user_id) REFERENCES users(id);
        RAISE NOTICE 'Contrainte FK user_id ajoutée';
    ELSE
        RAISE NOTICE 'Contrainte FK user_id existe déjà';
    END IF;
END $$;

-- 15. Ajouter les contraintes de clés étrangères si elles n'existent pas
DO $$
BEGIN
    -- Contrainte pour adresses.country_id
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.table_constraints 
        WHERE table_name = 'adresses' 
        AND constraint_name LIKE '%country_id%'
        AND constraint_type = 'FOREIGN KEY'
    ) THEN
        ALTER TABLE adresses 
        ADD CONSTRAINT fk_adresses_country 
        FOREIGN KEY (country_id) REFERENCES pays(id);
        RAISE NOTICE 'Contrainte FK adresses.country_id ajoutée';
    ELSE
        RAISE NOTICE 'Contrainte FK adresses.country_id existe déjà';
    END IF;
END $$;

-- 16. Vérifier que les données de référence existent
-- Insérer des civilités si elles n'existent pas
DO $$
BEGIN
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

-- 17. Insérer des pays si aucun n'existe
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

-- 18. Insérer des types de documents si aucun n'existe
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

-- 19. Vérifier la structure finale
SELECT 'Structure finale des tables' as info;
SELECT 
    table_name,
    column_name,
    data_type,
    is_nullable
FROM information_schema.columns 
WHERE table_name IN ('demandes', 'adresses', 'document_types', 'civilites', 'pays')
ORDER BY table_name, ordinal_position;
