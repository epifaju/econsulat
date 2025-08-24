-- Script de diagnostic pour comprendre les vrais IDs des types de documents
-- Exécuter ce script AVANT la migration

-- 1. Vérifier l'état actuel de la table document_types
SELECT 'État actuel de la table document_types:' as info;
SELECT id, libelle, description, is_active 
FROM document_types 
ORDER BY id;

-- 2. Vérifier les types de documents actuels dans les demandes
SELECT 'Types de documents dans les demandes existantes:' as info;
SELECT DISTINCT document_type, COUNT(*) as nombre_demandes
FROM demandes 
WHERE document_type IS NOT NULL
GROUP BY document_type 
ORDER BY document_type;

-- 3. Vérifier si la colonne document_type_id existe déjà
SELECT 'Colonnes de la table demandes:' as info;
SELECT column_name, data_type, is_nullable 
FROM information_schema.columns 
WHERE table_name = 'demandes' AND column_name LIKE '%document%'
ORDER BY column_name;

-- 4. Compter le nombre total de demandes
SELECT 'Nombre total de demandes:' as info;
SELECT COUNT(*) as total_demandes FROM demandes;

-- 5. Vérifier s'il y a des types de documents manquants
SELECT 'Vérification des types manquants:' as info;
SELECT 
    'PASSEPORT' as enum_type,
    (SELECT id FROM document_types WHERE libelle = 'Passeport' LIMIT 1) as id_trouve,
    (SELECT libelle FROM document_types WHERE libelle = 'Passeport' LIMIT 1) as libelle_trouve
UNION ALL
SELECT 
    'ACTE_NAISSANCE' as enum_type,
    (SELECT id FROM document_types WHERE libelle = 'Acte de naissance' LIMIT 1) as id_trouve,
    (SELECT libelle FROM document_types WHERE libelle = 'Acte de naissance' LIMIT 1) as libelle_trouve
UNION ALL
SELECT 
    'CERTIFICAT_MARIAGE' as enum_type,
    (SELECT id FROM document_types WHERE libelle = 'Certificat de mariage' LIMIT 1) as id_trouve,
    (SELECT libelle FROM document_types WHERE libelle = 'Certificat de mariage' LIMIT 1) as libelle_trouve
UNION ALL
SELECT 
    'CARTE_IDENTITE' as enum_type,
    (SELECT id FROM document_types WHERE libelle = 'Carte d''identité' LIMIT 1) as id_trouve,
    (SELECT libelle FROM document_types WHERE libelle = 'Carte d''identité' LIMIT 1) as libelle_trouve
UNION ALL
SELECT 
    'AUTRE' as enum_type,
    (SELECT id FROM document_types WHERE libelle = 'Autre' LIMIT 1) as id_trouve,
    (SELECT libelle FROM document_types WHERE libelle = 'Autre' LIMIT 1) as libelle_trouve;

-- 6. Recommandations basées sur les résultats
SELECT 'INSTRUCTIONS:' as info;
SELECT 'Exécutez d''abord ce script pour voir les vrais IDs, puis modifiez le script de migration en conséquence.' as instruction;
