-- Script pour corriger l'encodage des pays dans eConsulat
-- Problème: Les accents sont mal encodés (ex: SÃ©nÃ©gal au lieu de Sénégal)

-- 1. Vérifier l'encodage actuel de la base de données
SELECT datname, pg_encoding_to_char(encoding) as encoding 
FROM pg_database 
WHERE datname = 'econsulat';

-- 2. Vérifier les pays avec des problèmes d'encodage
SELECT id, libelle, 
       encode(convert_to(libelle, 'UTF8'), 'hex') as utf8_hex,
       encode(convert_to(libelle, 'LATIN1'), 'hex') as latin1_hex
FROM pays 
WHERE libelle LIKE '%Ã©%' OR libelle LIKE '%Ã¨%' OR libelle LIKE '%Ã ';

-- 3. Supprimer et recréer la table pays avec le bon encodage
DROP TABLE IF EXISTS pays CASCADE;

-- Recréer la table pays
CREATE TABLE pays (
    id SERIAL PRIMARY KEY,
    libelle VARCHAR(100) NOT NULL UNIQUE
);

-- 4. Insérer les pays avec le bon encodage UTF-8
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
('Nouvelle-Zélande');

-- 5. Vérifier que les pays sont correctement encodés
SELECT id, libelle 
FROM pays 
WHERE libelle LIKE '%é%' OR libelle LIKE '%è%' OR libelle LIKE '%à%'
ORDER BY libelle;

-- 6. Afficher le nombre total de pays
SELECT COUNT(*) as total_pays FROM pays;

-- 7. Vérifier l'encodage de quelques pays problématiques
SELECT 'Nouvelle-Calédonie' as expected, 
       (SELECT libelle FROM pays WHERE libelle = 'Nouvelle-Calédonie') as actual;

SELECT 'Guinée-Bissau' as expected, 
       (SELECT libelle FROM pays WHERE libelle = 'Guinée-Bissau') as actual;

SELECT 'Sénégal' as expected, 
       (SELECT libelle FROM pays WHERE libelle = 'Sénégal') as actual; 