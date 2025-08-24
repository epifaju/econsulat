-- Script de correction automatique pour la création de demandes
-- Ce script corrige les problèmes de structure de base de données

BEGIN;

-- 1. Vérifier et corriger la structure de la table demandes
DO $$
BEGIN
    -- Ajouter la colonne document_type_id si elle n'existe pas
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                   WHERE table_name = 'demandes' AND column_name = 'document_type_id') THEN
        ALTER TABLE demandes ADD COLUMN document_type_id INTEGER;
        RAISE NOTICE '✅ Colonne document_type_id ajoutée';
    ELSE
        RAISE NOTICE 'ℹ️ Colonne document_type_id existe déjà';
    END IF;
    
    -- Ajouter la colonne user_id si elle n'existe pas
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                   WHERE table_name = 'demandes' AND column_name = 'user_id') THEN
        ALTER TABLE demandes ADD COLUMN user_id INTEGER;
        RAISE NOTICE '✅ Colonne user_id ajoutée';
    ELSE
        RAISE NOTICE 'ℹ️ Colonne user_id existe déjà';
    END IF;
    
    -- Ajouter la colonne status si elle n'existe pas
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                   WHERE table_name = 'demandes' AND column_name = 'status') THEN
        ALTER TABLE demandes ADD COLUMN status VARCHAR(20) DEFAULT 'PENDING';
        RAISE NOTICE '✅ Colonne status ajoutée';
    ELSE
        RAISE NOTICE 'ℹ️ Colonne status existe déjà';
    END IF;
    
    -- Ajouter la colonne created_at si elle n'existe pas
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                   WHERE table_name = 'demandes' AND column_name = 'created_at') THEN
        ALTER TABLE demandes ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
        RAISE NOTICE '✅ Colonne created_at ajoutée';
    ELSE
        RAISE NOTICE 'ℹ️ Colonne created_at existe déjà';
    END IF;
    
    -- Ajouter la colonne updated_at si elle n'existe pas
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                   WHERE table_name = 'demandes' AND column_name = 'updated_at') THEN
        ALTER TABLE demandes ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
        RAISE NOTICE '✅ Colonne updated_at ajoutée';
    ELSE
        RAISE NOTICE 'ℹ️ Colonne updated_at existe déjà';
    END IF;
    
END $$;

-- 2. Créer la table document_types si elle n'existe pas
CREATE TABLE IF NOT EXISTS document_types (
    id SERIAL PRIMARY KEY,
    libelle VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    template_path VARCHAR(255),
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 3. Insérer les types de documents de base s'ils n'existent pas
INSERT INTO document_types (libelle, description, is_active) VALUES
    ('Passeport', 'Document de voyage officiel', true),
    ('Acte de naissance', 'Certificat de naissance officiel', true),
    ('Certificat de mariage', 'Certificat de mariage officiel', true),
    ('Carte d''identité', 'Carte d''identité nationale', true),
    ('Certificat d''hébergement', 'Certificat d''hébergement', true),
    ('Autre', 'Autre type de document', true)
ON CONFLICT (libelle) DO NOTHING;

-- 4. Créer la table civilites si elle n'existe pas
CREATE TABLE IF NOT EXISTS civilites (
    id SERIAL PRIMARY KEY,
    libelle VARCHAR(50) NOT NULL UNIQUE
);

-- 5. Insérer les civilités de base
INSERT INTO civilites (libelle) VALUES
    ('Monsieur'),
    ('Madame'),
    ('Mademoiselle')
ON CONFLICT (libelle) DO NOTHING;

-- 6. Créer la table pays si elle n'existe pas
CREATE TABLE IF NOT EXISTS pays (
    id SERIAL PRIMARY KEY,
    libelle VARCHAR(100) NOT NULL UNIQUE
);

-- 7. Insérer les pays de base
INSERT INTO pays (libelle) VALUES
    ('Guinée-Bissau'),
    ('France'),
    ('Portugal'),
    ('Sénégal'),
    ('Cap-Vert'),
    ('Mali'),
    ('Burkina Faso'),
    ('Côte d''Ivoire'),
    ('Togo'),
    ('Bénin'),
    ('Niger'),
    ('Tchad'),
    ('Cameroun'),
    ('Gabon'),
    ('Congo'),
    ('République démocratique du Congo'),
    ('République centrafricaine'),
    ('Soudan'),
    ('Éthiopie'),
    ('Somalie'),
    ('Kenya'),
    ('Tanzanie'),
    ('Ouganda'),
    ('Rwanda'),
    ('Burundi'),
    ('Angola'),
    ('Zambie'),
    ('Zimbabwe'),
    ('Mozambique'),
    ('Madagascar'),
    ('Maurice'),
    ('Seychelles'),
    ('Comores'),
    ('Djibouti'),
    ('Érythrée'),
    ('Algérie'),
    ('Maroc'),
    ('Tunisie'),
    ('Libye'),
    ('Égypte'),
    ('Soudan du Sud'),
    ('Équateur'),
    ('Guinée'),
    ('Guinée équatoriale'),
    ('São Tomé-et-Principe'),
    ('Canada'),
    ('États-Unis'),
    ('Brésil'),
    ('Argentine'),
    ('Chili'),
    ('Uruguay'),
    ('Paraguay'),
    ('Bolivie'),
    ('Pérou'),
    ('Équateur'),
    ('Colombie'),
    ('Venezuela'),
    ('Guyana'),
    ('Suriname'),
    ('Guyane française'),
    ('Royaume-Uni'),
    ('Allemagne'),
    ('Italie'),
    ('Espagne'),
    ('Pays-Bas'),
    ('Belgique'),
    ('Suisse'),
    ('Autriche'),
    ('Suède'),
    ('Norvège'),
    ('Danemark'),
    ('Finlande'),
    ('Pologne'),
    ('République tchèque'),
    ('Slovaquie'),
    ('Hongrie'),
    ('Roumanie'),
    ('Bulgarie'),
    ('Grèce'),
    ('Chypre'),
    ('Malte'),
    ('Slovénie'),
    ('Croatie'),
    ('Bosnie-Herzégovine'),
    ('Serbie'),
    ('Monténégro'),
    ('Kosovo'),
    ('Macédoine du Nord'),
    ('Albanie'),
    ('Moldavie'),
    ('Ukraine'),
    ('Biélorussie'),
    ('Lituanie'),
    ('Lettonie'),
    ('Estonie'),
    ('Russie'),
    ('Turquie'),
    ('Géorgie'),
    ('Arménie'),
    ('Azerbaïdjan'),
    ('Iran'),
    ('Irak'),
    ('Syrie'),
    ('Liban'),
    ('Jordanie'),
    ('Israël'),
    ('Palestine'),
    ('Arabie saoudite'),
    ('Yémen'),
    ('Oman'),
    ('Émirats arabes unis'),
    ('Qatar'),
    ('Bahreïn'),
    ('Koweït'),
    ('Afghanistan'),
    ('Pakistan'),
    ('Inde'),
    ('Népal'),
    ('Bhoutan'),
    ('Bangladesh'),
    ('Sri Lanka'),
    ('Maldives'),
    ('Chine'),
    ('Mongolie'),
    ('Corée du Nord'),
    ('Corée du Sud'),
    ('Japon'),
    ('Taïwan'),
    ('Vietnam'),
    ('Laos'),
    ('Cambodge'),
    ('Thaïlande'),
    ('Myanmar'),
    ('Malaisie'),
    ('Singapour'),
    ('Indonésie'),
    ('Philippines'),
    ('Brunei'),
    ('Timor oriental'),
    ('Papouasie-Nouvelle-Guinée'),
    ('Fidji'),
    ('Vanuatu'),
    ('Nouvelle-Calédonie'),
    ('Australie'),
    ('Nouvelle-Zélande')
ON CONFLICT (libelle) DO NOTHING;

-- 8. Créer la table adresses si elle n'existe pas
CREATE TABLE IF NOT EXISTS adresses (
    id SERIAL PRIMARY KEY,
    street_name VARCHAR(255) NOT NULL,
    street_number VARCHAR(20) NOT NULL,
    box_number VARCHAR(20),
    postal_code VARCHAR(20) NOT NULL,
    city VARCHAR(100) NOT NULL,
    country_id INTEGER NOT NULL REFERENCES pays(id)
);

-- 9. Créer la table users si elle n'existe pas
CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(100) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'USER' CHECK (role IN ('ADMIN', 'USER', 'AGENT')),
    email_verified BOOLEAN DEFAULT false,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 10. Ajouter les contraintes de clé étrangère sur demandes
DO $$
BEGIN
    -- Contrainte pour civilite_id
    IF NOT EXISTS (SELECT 1 FROM information_schema.table_constraints 
                   WHERE table_name = 'demandes' AND constraint_name = 'fk_demandes_civilite') THEN
        ALTER TABLE demandes ADD CONSTRAINT fk_demandes_civilite 
        FOREIGN KEY (civilite_id) REFERENCES civilites(id);
        RAISE NOTICE '✅ Contrainte fk_demandes_civilite ajoutée';
    END IF;
    
    -- Contrainte pour birth_country_id
    IF NOT EXISTS (SELECT 1 FROM information_schema.table_constraints 
                   WHERE table_name = 'demandes' AND constraint_name = 'fk_demandes_birth_country') THEN
        ALTER TABLE demandes ADD CONSTRAINT fk_demandes_birth_country 
        FOREIGN KEY (birth_country_id) REFERENCES pays(id);
        RAISE NOTICE '✅ Contrainte fk_demandes_birth_country ajoutée';
    END IF;
    
    -- Contrainte pour adresse_id
    IF NOT EXISTS (SELECT 1 FROM information_schema.table_constraints 
                   WHERE table_name = 'demandes' AND constraint_name = 'fk_demandes_adresse') THEN
        ALTER TABLE demandes ADD CONSTRAINT fk_demandes_adresse 
        FOREIGN KEY (adresse_id) REFERENCES adresses(id);
        RAISE NOTICE '✅ Contrainte fk_demandes_adresse ajoutée';
    END IF;
    
    -- Contrainte pour father_birth_country_id
    IF NOT EXISTS (SELECT 1 FROM information_schema.table_constraints 
                   WHERE table_name = 'demandes' AND constraint_name = 'fk_demandes_father_birth_country') THEN
        ALTER TABLE demandes ADD CONSTRAINT fk_demandes_father_birth_country 
        FOREIGN KEY (father_birth_country_id) REFERENCES pays(id);
        RAISE NOTICE '✅ Contrainte fk_demandes_father_birth_country ajoutée';
    END IF;
    
    -- Contrainte pour mother_birth_country_id
    IF NOT EXISTS (SELECT 1 FROM information_schema.table_constraints 
                   WHERE table_name = 'demandes' AND constraint_name = 'fk_demandes_mother_birth_country') THEN
        ALTER TABLE demandes ADD CONSTRAINT fk_demandes_mother_birth_country 
        FOREIGN KEY (mother_birth_country_id) REFERENCES pays(id);
        RAISE NOTICE '✅ Contrainte fk_demandes_mother_birth_country ajoutée';
    END IF;
    
    -- Contrainte pour document_type_id
    IF NOT EXISTS (SELECT 1 FROM information_schema.table_constraints 
                   WHERE table_name = 'demandes' AND constraint_name = 'fk_demandes_document_type') THEN
        ALTER TABLE demandes ADD CONSTRAINT fk_demandes_document_type 
        FOREIGN KEY (document_type_id) REFERENCES document_types(id);
        RAISE NOTICE '✅ Contrainte fk_demandes_document_type ajoutée';
    END IF;
    
    -- Contrainte pour user_id
    IF NOT EXISTS (SELECT 1 FROM information_schema.table_constraints 
                   WHERE table_name = 'demandes' AND constraint_name = 'fk_demandes_user') THEN
        ALTER TABLE demandes ADD CONSTRAINT fk_demandes_user 
        FOREIGN KEY (user_id) REFERENCES users(id);
        RAISE NOTICE '✅ Contrainte fk_demandes_user ajoutée';
    END IF;
    
END $$;

-- 11. Créer les index pour optimiser les performances
CREATE INDEX IF NOT EXISTS idx_demandes_user_id ON demandes(user_id);
CREATE INDEX IF NOT EXISTS idx_demandes_status ON demandes(status);
CREATE INDEX IF NOT EXISTS idx_demandes_created_at ON demandes(created_at);
CREATE INDEX IF NOT EXISTS idx_demandes_document_type_id ON demandes(document_type_id);

-- 12. Vérification finale
SELECT '✅ CORRECTION TERMINÉE - Vérification de la structure:' as info;

SELECT 'Structure de la table demandes:' as table_info;
SELECT column_name, data_type, is_nullable, column_default
FROM information_schema.columns 
WHERE table_name = 'demandes' 
ORDER BY ordinal_position;

SELECT 'Contraintes de clé étrangère sur demandes:' as constraints_info;
SELECT 
    tc.constraint_name,
    kcu.column_name,
    ccu.table_name AS referenced_table,
    ccu.column_name AS referenced_column
FROM information_schema.table_constraints AS tc
JOIN information_schema.key_column_usage AS kcu
    ON tc.constraint_name = kcu.constraint_name
JOIN information_schema.constraint_column_usage AS ccu
    ON ccu.constraint_name = tc.constraint_name
WHERE tc.constraint_type = 'FOREIGN KEY' 
    AND tc.table_name = 'demandes'
ORDER BY kcu.ordinal_position;

COMMIT;

SELECT '🎉 CORRECTION AUTOMATIQUE TERMINÉE AVEC SUCCÈS !' as success_message;
