-- Script de migration CORRIGÉ pour passer de l'enum DocumentType à la relation JPA
-- Basé sur les vrais IDs de la base de données

-- 1. Vérifier l'état actuel
SELECT 'État actuel avant migration:' as info;
SELECT id, libelle FROM document_types ORDER BY id;

-- 2. D'abord, ajouter le type "Carte d'identité" manquant
INSERT INTO document_types (libelle, description, template_path, is_active, created_at, updated_at) 
VALUES ('Carte d''identité', 'Carte d''identité nationale', 'templates/carte_identite_template.docx', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (libelle) DO NOTHING;

-- 3. Récupérer l'ID du type "Carte d'identité" (nouvellement créé ou existant)
SELECT 'ID du type Carte d''identité:' as info;
SELECT id, libelle FROM document_types WHERE libelle = 'Carte d''identité';

-- 4. Créer la nouvelle colonne document_type_id
ALTER TABLE demandes ADD COLUMN IF NOT EXISTS document_type_id INTEGER;

-- 5. Mapper les anciennes valeurs enum vers les VRAIS IDs de la base
UPDATE demandes 
SET document_type_id = CASE 
    WHEN document_type = 'PASSEPORT' THEN 2                -- ID réel : 2
    WHEN document_type = 'ACTE_NAISSANCE' THEN 3           -- ID réel : 3
    WHEN document_type = 'CERTIFICAT_MARIAGE' THEN 4       -- ID réel : 4
    WHEN document_type = 'CARTE_IDENTITE' THEN (
        SELECT id FROM document_types WHERE libelle = 'Carte d''identité' LIMIT 1
    )                                                       -- ID nouvellement créé
    WHEN document_type = 'AUTRE' THEN 10                   -- ID réel : 10
    ELSE 2 -- Fallback vers Passeport (ID 2)
END;

-- 6. Vérifier le mapping
SELECT 'Vérification du mapping:' as info;
SELECT 
    d.document_type as ancien_type,
    d.document_type_id as nouveau_id,
    dt.libelle as nouveau_libelle,
    COUNT(*) as nombre_demandes
FROM demandes d
LEFT JOIN document_types dt ON d.document_type_id = dt.id
GROUP BY d.document_type, d.document_type_id, dt.libelle
ORDER BY d.document_type;

-- 7. Vérifier qu'aucune demande n'a un document_type_id NULL
SELECT 'Vérification des IDs NULL:' as info;
SELECT COUNT(*) as demandes_avec_id_null
FROM demandes 
WHERE document_type_id IS NULL;

-- 8. Vérifier que tous les IDs existent dans document_types
SELECT 'Vérification de l''existence des IDs:' as info;
SELECT DISTINCT 
    d.document_type_id,
    dt.libelle,
    CASE WHEN dt.id IS NOT NULL THEN '✅ Existe' ELSE '❌ N''existe pas' END as statut
FROM demandes d
LEFT JOIN document_types dt ON d.document_type_id = dt.id
ORDER BY d.document_type_id;

-- 9. Si tout est OK, ajouter la contrainte de clé étrangère
-- ATTENTION : Ne pas exécuter cette partie si il y a des erreurs ci-dessus !
SELECT 'PRÊT pour la contrainte FK ? Vérifiez les résultats ci-dessus avant de continuer.' as warning;

-- Décommentez les lignes suivantes SEULEMENT si tous les tests ci-dessus sont OK :
-- ALTER TABLE demandes 
-- ADD CONSTRAINT fk_demandes_document_type 
-- FOREIGN KEY (document_type_id) REFERENCES document_types(id);

-- 10. Rendre la colonne NOT NULL
-- ALTER TABLE demandes ALTER COLUMN document_type_id SET NOT NULL;

-- 11. Optionnel : Supprimer l'ancienne colonne document_type (à faire plus tard)
-- ALTER TABLE demandes DROP COLUMN document_type;

-- 12. Vérification finale
SELECT 'Mapping final des types de documents:' as info;
SELECT 
    'PASSEPORT' as enum_type,
    2 as expected_id,
    (SELECT libelle FROM document_types WHERE id = 2) as actual_libelle
UNION ALL
SELECT 
    'ACTE_NAISSANCE' as enum_type,
    3 as expected_id,
    (SELECT libelle FROM document_types WHERE id = 3) as actual_libelle
UNION ALL
SELECT 
    'CERTIFICAT_MARIAGE' as enum_type,
    4 as expected_id,
    (SELECT libelle FROM document_types WHERE id = 4) as actual_libelle
UNION ALL
SELECT 
    'CARTE_IDENTITE' as enum_type,
    (SELECT id FROM document_types WHERE libelle = 'Carte d''identité' LIMIT 1) as expected_id,
    (SELECT libelle FROM document_types WHERE libelle = 'Carte d''identité' LIMIT 1) as actual_libelle
UNION ALL
SELECT 
    'AUTRE' as enum_type,
    10 as expected_id,
    (SELECT libelle FROM document_types WHERE id = 10) as actual_libelle
ORDER BY expected_id;

SELECT 'Migration préparée ! Vérifiez tous les résultats avant d''ajouter les contraintes.' as result;
