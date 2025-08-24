-- Script pour vérifier et corriger le mapping des types de documents
-- Exécuter ce script pour résoudre le problème d'affichage incorrect

-- 1. Vérifier l'état actuel de la table document_types
SELECT 'État actuel de la table document_types:' as info;
SELECT id, libelle, description, is_active 
FROM document_types 
ORDER BY id;

-- 2. Vérifier les demandes et leurs types de documents
SELECT 'Types de documents dans les demandes:' as info;
SELECT DISTINCT d.document_type, dt.libelle as type_affichage
FROM demandes d
LEFT JOIN document_types dt ON dt.id = (
    CASE 
        WHEN d.document_type = 'PASSEPORT' THEN 1
        WHEN d.document_type = 'ACTE_NAISSANCE' THEN 2
        WHEN d.document_type = 'CERTIFICAT_MARIAGE' THEN 3
        WHEN d.document_type = 'CARTE_IDENTITE' THEN 5
        WHEN d.document_type = 'AUTRE' THEN 6
        ELSE 1
    END
)
ORDER BY d.document_type;

-- 3. Vérifier s'il y a des incohérences
SELECT 'Vérification des incohérences:' as info;
SELECT 
    d.id as demande_id,
    d.document_type as enum_type,
    dt.id as type_id,
    dt.libelle as type_libelle,
    CASE 
        WHEN d.document_type = 'ACTE_NAISSANCE' AND dt.id = 2 THEN '✅ Correct'
        WHEN d.document_type = 'CARTE_IDENTITE' AND dt.id = 5 THEN '✅ Correct'
        WHEN d.document_type = 'PASSEPORT' AND dt.id = 1 THEN '✅ Correct'
        WHEN d.document_type = 'CERTIFICAT_MARIAGE' AND dt.id = 3 THEN '✅ Correct'
        WHEN d.document_type = 'AUTRE' AND dt.id = 6 THEN '✅ Correct'
        ELSE '❌ Incorrect'
    END as statut
FROM demandes d
LEFT JOIN document_types dt ON dt.id = (
    CASE 
        WHEN d.document_type = 'PASSEPORT' THEN 1
        WHEN d.document_type = 'ACTE_NAISSANCE' THEN 2
        WHEN d.document_type = 'CERTIFICAT_MARIAGE' THEN 3
        WHEN d.document_type = 'CARTE_IDENTITE' THEN 5
        WHEN d.document_type = 'AUTRE' THEN 6
        ELSE 1
    END
)
ORDER BY d.document_type, dt.id;

-- 4. Si nécessaire, corriger les types de documents manquants
-- Insérer les types manquants s'ils n'existent pas
INSERT INTO document_types (libelle, description, template_path, is_active, created_at, updated_at) 
SELECT 
    'Autre',
    'Autre type de document',
    'templates/autre_template.docx',
    true,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
WHERE NOT EXISTS (
    SELECT 1 FROM document_types WHERE libelle = 'Autre'
);

-- 5. Vérifier le mapping final
SELECT 'Mapping final des types de documents:' as info;
SELECT 
    'PASSEPORT' as enum_type,
    1 as expected_id,
    (SELECT libelle FROM document_types WHERE id = 1) as actual_libelle
UNION ALL
SELECT 
    'ACTE_NAISSANCE' as enum_type,
    2 as expected_id,
    (SELECT libelle FROM document_types WHERE id = 2) as actual_libelle
UNION ALL
SELECT 
    'CERTIFICAT_MARIAGE' as enum_type,
    3 as expected_id,
    (SELECT libelle FROM document_types WHERE id = 3) as actual_libelle
UNION ALL
SELECT 
    'CARTE_IDENTITE' as enum_type,
    5 as expected_id,
    (SELECT libelle FROM document_types WHERE id = 5) as actual_libelle
UNION ALL
SELECT 
    'AUTRE' as enum_type,
    6 as expected_id,
    (SELECT libelle FROM document_types WHERE id = 6) as actual_libelle
ORDER BY expected_id;

-- 6. Message de confirmation
SELECT 'Script de correction terminé!' as result;
SELECT 'Vérifiez que tous les types de documents sont correctement mappés.' as instruction;
