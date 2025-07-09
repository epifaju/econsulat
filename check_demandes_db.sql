-- Script pour vérifier et corriger la base de données des demandes eConsulat

-- Vérifier si les tables existent
SELECT 'Vérification des tables:' as info;

SELECT table_name 
FROM information_schema.tables 
WHERE table_schema = 'public' 
AND table_name IN ('civilites', 'pays', 'adresses', 'demandes')
ORDER BY table_name;

-- Vérifier les données dans civilites
SELECT 'Données dans civilites:' as info;
SELECT id, libelle FROM civilites ORDER BY id;

-- Si pas de données, les insérer
INSERT INTO civilites (libelle) 
SELECT 'Monsieur' WHERE NOT EXISTS (SELECT 1 FROM civilites WHERE libelle = 'Monsieur');

INSERT INTO civilites (libelle) 
SELECT 'Madame' WHERE NOT EXISTS (SELECT 1 FROM civilites WHERE libelle = 'Madame');

INSERT INTO civilites (libelle) 
SELECT 'Mademoiselle' WHERE NOT EXISTS (SELECT 1 FROM civilites WHERE libelle = 'Mademoiselle');

-- Vérifier les données dans pays
SELECT 'Nombre de pays dans la base:' as info;
SELECT COUNT(*) as nombre_pays FROM pays;

-- Si pas assez de pays, insérer les principaux
INSERT INTO pays (libelle) 
SELECT 'Guinée-Bissau' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Guinée-Bissau');

INSERT INTO pays (libelle) 
SELECT 'France' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'France');

INSERT INTO pays (libelle) 
SELECT 'Portugal' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Portugal');

INSERT INTO pays (libelle) 
SELECT 'Sénégal' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Sénégal');

INSERT INTO pays (libelle) 
SELECT 'Cap-Vert' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Cap-Vert');

INSERT INTO pays (libelle) 
SELECT 'Mali' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Mali');

INSERT INTO pays (libelle) 
SELECT 'Burkina Faso' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Burkina Faso');

INSERT INTO pays (libelle) 
SELECT 'Côte d''Ivoire' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Côte d''Ivoire');

INSERT INTO pays (libelle) 
SELECT 'Togo' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Togo');

INSERT INTO pays (libelle) 
SELECT 'Bénin' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Bénin');

INSERT INTO pays (libelle) 
SELECT 'Niger' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Niger');

INSERT INTO pays (libelle) 
SELECT 'Tchad' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Tchad');

INSERT INTO pays (libelle) 
SELECT 'Cameroun' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Cameroun');

INSERT INTO pays (libelle) 
SELECT 'Gabon' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Gabon');

INSERT INTO pays (libelle) 
SELECT 'Congo' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Congo');

INSERT INTO pays (libelle) 
SELECT 'République démocratique du Congo' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'République démocratique du Congo');

INSERT INTO pays (libelle) 
SELECT 'République centrafricaine' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'République centrafricaine');

INSERT INTO pays (libelle) 
SELECT 'Soudan' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Soudan');

INSERT INTO pays (libelle) 
SELECT 'Éthiopie' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Éthiopie');

INSERT INTO pays (libelle) 
SELECT 'Somalie' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Somalie');

INSERT INTO pays (libelle) 
SELECT 'Kenya' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Kenya');

INSERT INTO pays (libelle) 
SELECT 'Tanzanie' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Tanzanie');

INSERT INTO pays (libelle) 
SELECT 'Ouganda' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Ouganda');

INSERT INTO pays (libelle) 
SELECT 'Rwanda' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Rwanda');

INSERT INTO pays (libelle) 
SELECT 'Burundi' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Burundi');

INSERT INTO pays (libelle) 
SELECT 'Angola' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Angola');

INSERT INTO pays (libelle) 
SELECT 'Zambie' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Zambie');

INSERT INTO pays (libelle) 
SELECT 'Zimbabwe' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Zimbabwe');

INSERT INTO pays (libelle) 
SELECT 'Mozambique' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Mozambique');

INSERT INTO pays (libelle) 
SELECT 'Madagascar' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Madagascar');

INSERT INTO pays (libelle) 
SELECT 'Maurice' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Maurice');

INSERT INTO pays (libelle) 
SELECT 'Seychelles' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Seychelles');

INSERT INTO pays (libelle) 
SELECT 'Comores' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Comores');

INSERT INTO pays (libelle) 
SELECT 'Djibouti' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Djibouti');

INSERT INTO pays (libelle) 
SELECT 'Érythrée' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Érythrée');

INSERT INTO pays (libelle) 
SELECT 'Algérie' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Algérie');

INSERT INTO pays (libelle) 
SELECT 'Maroc' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Maroc');

INSERT INTO pays (libelle) 
SELECT 'Tunisie' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Tunisie');

INSERT INTO pays (libelle) 
SELECT 'Libye' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Libye');

INSERT INTO pays (libelle) 
SELECT 'Égypte' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Égypte');

INSERT INTO pays (libelle) 
SELECT 'Soudan du Sud' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Soudan du Sud');

INSERT INTO pays (libelle) 
SELECT 'Guinée' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Guinée');

INSERT INTO pays (libelle) 
SELECT 'Guinée équatoriale' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Guinée équatoriale');

INSERT INTO pays (libelle) 
SELECT 'São Tomé-et-Principe' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'São Tomé-et-Principe');

INSERT INTO pays (libelle) 
SELECT 'Canada' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Canada');

INSERT INTO pays (libelle) 
SELECT 'États-Unis' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'États-Unis');

INSERT INTO pays (libelle) 
SELECT 'Brésil' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Brésil');

INSERT INTO pays (libelle) 
SELECT 'Argentine' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Argentine');

INSERT INTO pays (libelle) 
SELECT 'Chili' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Chili');

INSERT INTO pays (libelle) 
SELECT 'Uruguay' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Uruguay');

INSERT INTO pays (libelle) 
SELECT 'Paraguay' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Paraguay');

INSERT INTO pays (libelle) 
SELECT 'Bolivie' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Bolivie');

INSERT INTO pays (libelle) 
SELECT 'Pérou' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Pérou');

INSERT INTO pays (libelle) 
SELECT 'Équateur' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Équateur');

INSERT INTO pays (libelle) 
SELECT 'Colombie' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Colombie');

INSERT INTO pays (libelle) 
SELECT 'Venezuela' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Venezuela');

INSERT INTO pays (libelle) 
SELECT 'Guyana' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Guyana');

INSERT INTO pays (libelle) 
SELECT 'Suriname' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Suriname');

INSERT INTO pays (libelle) 
SELECT 'Guyane française' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Guyane française');

INSERT INTO pays (libelle) 
SELECT 'Royaume-Uni' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Royaume-Uni');

INSERT INTO pays (libelle) 
SELECT 'Allemagne' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Allemagne');

INSERT INTO pays (libelle) 
SELECT 'Italie' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Italie');

INSERT INTO pays (libelle) 
SELECT 'Espagne' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Espagne');

INSERT INTO pays (libelle) 
SELECT 'Pays-Bas' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Pays-Bas');

INSERT INTO pays (libelle) 
SELECT 'Belgique' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Belgique');

INSERT INTO pays (libelle) 
SELECT 'Suisse' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Suisse');

INSERT INTO pays (libelle) 
SELECT 'Autriche' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Autriche');

INSERT INTO pays (libelle) 
SELECT 'Suède' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Suède');

INSERT INTO pays (libelle) 
SELECT 'Norvège' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Norvège');

INSERT INTO pays (libelle) 
SELECT 'Danemark' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Danemark');

INSERT INTO pays (libelle) 
SELECT 'Finlande' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Finlande');

INSERT INTO pays (libelle) 
SELECT 'Pologne' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Pologne');

INSERT INTO pays (libelle) 
SELECT 'République tchèque' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'République tchèque');

INSERT INTO pays (libelle) 
SELECT 'Slovaquie' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Slovaquie');

INSERT INTO pays (libelle) 
SELECT 'Hongrie' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Hongrie');

INSERT INTO pays (libelle) 
SELECT 'Roumanie' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Roumanie');

INSERT INTO pays (libelle) 
SELECT 'Bulgarie' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Bulgarie');

INSERT INTO pays (libelle) 
SELECT 'Grèce' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Grèce');

INSERT INTO pays (libelle) 
SELECT 'Chypre' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Chypre');

INSERT INTO pays (libelle) 
SELECT 'Malte' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Malte');

INSERT INTO pays (libelle) 
SELECT 'Slovénie' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Slovénie');

INSERT INTO pays (libelle) 
SELECT 'Croatie' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Croatie');

INSERT INTO pays (libelle) 
SELECT 'Bosnie-Herzégovine' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Bosnie-Herzégovine');

INSERT INTO pays (libelle) 
SELECT 'Serbie' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Serbie');

INSERT INTO pays (libelle) 
SELECT 'Monténégro' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Monténégro');

INSERT INTO pays (libelle) 
SELECT 'Kosovo' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Kosovo');

INSERT INTO pays (libelle) 
SELECT 'Macédoine du Nord' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Macédoine du Nord');

INSERT INTO pays (libelle) 
SELECT 'Albanie' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Albanie');

INSERT INTO pays (libelle) 
SELECT 'Moldavie' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Moldavie');

INSERT INTO pays (libelle) 
SELECT 'Ukraine' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Ukraine');

INSERT INTO pays (libelle) 
SELECT 'Biélorussie' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Biélorussie');

INSERT INTO pays (libelle) 
SELECT 'Lituanie' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Lituanie');

INSERT INTO pays (libelle) 
SELECT 'Lettonie' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Lettonie');

INSERT INTO pays (libelle) 
SELECT 'Estonie' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Estonie');

INSERT INTO pays (libelle) 
SELECT 'Russie' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Russie');

INSERT INTO pays (libelle) 
SELECT 'Turquie' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Turquie');

INSERT INTO pays (libelle) 
SELECT 'Géorgie' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Géorgie');

INSERT INTO pays (libelle) 
SELECT 'Arménie' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Arménie');

INSERT INTO pays (libelle) 
SELECT 'Azerbaïdjan' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Azerbaïdjan');

INSERT INTO pays (libelle) 
SELECT 'Iran' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Iran');

INSERT INTO pays (libelle) 
SELECT 'Irak' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Irak');

INSERT INTO pays (libelle) 
SELECT 'Syrie' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Syrie');

INSERT INTO pays (libelle) 
SELECT 'Liban' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Liban');

INSERT INTO pays (libelle) 
SELECT 'Jordanie' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Jordanie');

INSERT INTO pays (libelle) 
SELECT 'Israël' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Israël');

INSERT INTO pays (libelle) 
SELECT 'Palestine' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Palestine');

INSERT INTO pays (libelle) 
SELECT 'Arabie saoudite' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Arabie saoudite');

INSERT INTO pays (libelle) 
SELECT 'Yémen' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Yémen');

INSERT INTO pays (libelle) 
SELECT 'Oman' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Oman');

INSERT INTO pays (libelle) 
SELECT 'Émirats arabes unis' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Émirats arabes unis');

INSERT INTO pays (libelle) 
SELECT 'Qatar' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Qatar');

INSERT INTO pays (libelle) 
SELECT 'Bahreïn' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Bahreïn');

INSERT INTO pays (libelle) 
SELECT 'Koweït' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Koweït');

INSERT INTO pays (libelle) 
SELECT 'Afghanistan' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Afghanistan');

INSERT INTO pays (libelle) 
SELECT 'Pakistan' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Pakistan');

INSERT INTO pays (libelle) 
SELECT 'Inde' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Inde');

INSERT INTO pays (libelle) 
SELECT 'Népal' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Népal');

INSERT INTO pays (libelle) 
SELECT 'Bhoutan' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Bhoutan');

INSERT INTO pays (libelle) 
SELECT 'Bangladesh' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Bangladesh');

INSERT INTO pays (libelle) 
SELECT 'Sri Lanka' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Sri Lanka');

INSERT INTO pays (libelle) 
SELECT 'Maldives' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Maldives');

INSERT INTO pays (libelle) 
SELECT 'Chine' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Chine');

INSERT INTO pays (libelle) 
SELECT 'Mongolie' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Mongolie');

INSERT INTO pays (libelle) 
SELECT 'Corée du Nord' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Corée du Nord');

INSERT INTO pays (libelle) 
SELECT 'Corée du Sud' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Corée du Sud');

INSERT INTO pays (libelle) 
SELECT 'Japon' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Japon');

INSERT INTO pays (libelle) 
SELECT 'Taïwan' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Taïwan');

INSERT INTO pays (libelle) 
SELECT 'Vietnam' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Vietnam');

INSERT INTO pays (libelle) 
SELECT 'Laos' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Laos');

INSERT INTO pays (libelle) 
SELECT 'Cambodge' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Cambodge');

INSERT INTO pays (libelle) 
SELECT 'Thaïlande' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Thaïlande');

INSERT INTO pays (libelle) 
SELECT 'Myanmar' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Myanmar');

INSERT INTO pays (libelle) 
SELECT 'Malaisie' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Malaisie');

INSERT INTO pays (libelle) 
SELECT 'Singapour' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Singapour');

INSERT INTO pays (libelle) 
SELECT 'Indonésie' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Indonésie');

INSERT INTO pays (libelle) 
SELECT 'Philippines' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Philippines');

INSERT INTO pays (libelle) 
SELECT 'Brunei' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Brunei');

INSERT INTO pays (libelle) 
SELECT 'Timor oriental' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Timor oriental');

INSERT INTO pays (libelle) 
SELECT 'Papouasie-Nouvelle-Guinée' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Papouasie-Nouvelle-Guinée');

INSERT INTO pays (libelle) 
SELECT 'Fidji' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Fidji');

INSERT INTO pays (libelle) 
SELECT 'Vanuatu' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Vanuatu');

INSERT INTO pays (libelle) 
SELECT 'Nouvelle-Calédonie' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Nouvelle-Calédonie');

INSERT INTO pays (libelle) 
SELECT 'Australie' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Australie');

INSERT INTO pays (libelle) 
SELECT 'Nouvelle-Zélande' WHERE NOT EXISTS (SELECT 1 FROM pays WHERE libelle = 'Nouvelle-Zélande');

-- Vérifier les données après insertion
SELECT 'Données finales:' as info;
SELECT 'Civilités:' as type, COUNT(*) as nombre FROM civilites
UNION ALL
SELECT 'Pays:' as type, COUNT(*) as nombre FROM pays;

-- Afficher quelques exemples
SELECT 'Exemples de civilités:' as info;
SELECT id, libelle FROM civilites ORDER BY id LIMIT 5;

SELECT 'Exemples de pays:' as info;
SELECT id, libelle FROM pays ORDER BY id LIMIT 10; 