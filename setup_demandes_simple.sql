-- Script simplifié pour créer les tables et données des demandes eConsulat

-- Créer la table civilites si elle n'existe pas
CREATE TABLE IF NOT EXISTS civilites (
    id SERIAL PRIMARY KEY,
    libelle VARCHAR(50) NOT NULL UNIQUE
);

-- Créer la table pays si elle n'existe pas
CREATE TABLE IF NOT EXISTS pays (
    id SERIAL PRIMARY KEY,
    libelle VARCHAR(100) NOT NULL UNIQUE
);

-- Créer la table adresses si elle n'existe pas
CREATE TABLE IF NOT EXISTS adresses (
    id SERIAL PRIMARY KEY,
    rue VARCHAR(255),
    ville VARCHAR(100),
    code_postal VARCHAR(20),
    pays_id INTEGER REFERENCES pays(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Créer la table demandes si elle n'existe pas
CREATE TABLE IF NOT EXISTS demandes (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    civilite_id INTEGER REFERENCES civilites(id),
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    date_naissance DATE,
    lieu_naissance VARCHAR(100),
    pays_naissance_id INTEGER REFERENCES pays(id),
    adresse_id INTEGER REFERENCES adresses(id),
    pere_nom VARCHAR(100),
    pere_prenom VARCHAR(100),
    pere_nationalite VARCHAR(100),
    mere_nom VARCHAR(100),
    mere_prenom VARCHAR(100),
    mere_nationalite VARCHAR(100),
    document_type VARCHAR(50) NOT NULL,
    document_files TEXT,
    status VARCHAR(20) DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insérer les civilités (seulement si elles n'existent pas)
INSERT INTO civilites (libelle) VALUES ('Monsieur') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO civilites (libelle) VALUES ('Madame') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO civilites (libelle) VALUES ('Mademoiselle') ON CONFLICT (libelle) DO NOTHING;

-- Insérer les pays principaux (seulement s'ils n'existent pas)
INSERT INTO pays (libelle) VALUES ('Guinée-Bissau') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('France') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Portugal') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Sénégal') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Cap-Vert') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Mali') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Burkina Faso') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Côte d''Ivoire') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Togo') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Bénin') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Niger') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Tchad') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Cameroun') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Gabon') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Congo') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('République démocratique du Congo') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('République centrafricaine') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Soudan') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Éthiopie') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Somalie') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Kenya') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Tanzanie') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Ouganda') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Rwanda') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Burundi') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Angola') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Zambie') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Zimbabwe') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Mozambique') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Madagascar') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Maurice') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Seychelles') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Comores') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Djibouti') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Érythrée') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Algérie') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Maroc') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Tunisie') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Libye') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Égypte') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Soudan du Sud') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Guinée') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Guinée équatoriale') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('São Tomé-et-Principe') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Canada') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('États-Unis') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Brésil') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Argentine') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Chili') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Uruguay') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Paraguay') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Bolivie') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Pérou') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Équateur') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Colombie') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Venezuela') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Guyana') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Suriname') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Guyane française') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Royaume-Uni') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Allemagne') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Italie') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Espagne') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Pays-Bas') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Belgique') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Suisse') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Autriche') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Suède') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Norvège') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Danemark') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Finlande') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Pologne') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('République tchèque') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Slovaquie') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Hongrie') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Roumanie') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Bulgarie') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Grèce') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Chypre') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Malte') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Slovénie') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Croatie') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Bosnie-Herzégovine') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Serbie') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Monténégro') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Kosovo') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Macédoine du Nord') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Albanie') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Moldavie') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Ukraine') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Biélorussie') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Lituanie') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Lettonie') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Estonie') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Russie') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Turquie') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Géorgie') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Arménie') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Azerbaïdjan') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Iran') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Irak') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Syrie') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Liban') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Jordanie') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Israël') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Palestine') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Arabie saoudite') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Yémen') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Oman') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Émirats arabes unis') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Qatar') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Bahreïn') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Koweït') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Afghanistan') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Pakistan') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Inde') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Népal') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Bhoutan') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Bangladesh') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Sri Lanka') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Maldives') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Chine') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Mongolie') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Corée du Nord') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Corée du Sud') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Japon') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Taïwan') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Vietnam') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Laos') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Cambodge') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Thaïlande') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Myanmar') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Malaisie') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Singapour') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Indonésie') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Philippines') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Brunei') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Timor oriental') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Papouasie-Nouvelle-Guinée') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Fidji') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Vanuatu') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Nouvelle-Calédonie') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Australie') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Nouvelle-Zélande') ON CONFLICT (libelle) DO NOTHING;

-- Afficher les résultats
SELECT 'Tables créées avec succès!' as message;
SELECT 'Civilités:' as type, COUNT(*) as nombre FROM civilites;
SELECT 'Pays:' as type, COUNT(*) as nombre FROM pays; 