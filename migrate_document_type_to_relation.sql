-- Script de migration pour passer de l'enum DocumentType à la relation JPA
-- Exécuter ce script pour corriger la structure de la base de données

-- 1. Vérifier l'état actuel
SELECT 'État actuel de la table demandes:' as info;
SELECT column_name, data_type, is_nullable 
FROM information_schema.columns 
WHERE table_name = 'demandes' AND column_name LIKE '%document%';

-- 2. Vérifier les types de documents existants
SELECT 'Types de documents dans document_types:' as info;
SELECT id, libelle, description FROM document_types ORDER BY id;

-- 3. Vérifier les valeurs actuelles dans la colonne document_type
SELECT 'Valeurs actuelles dans document_type:' as info;
SELECT DISTINCT document_type, COUNT(*) as nombre
FROM demandes 
GROUP BY document_type 
ORDER BY document_type;

-- 4. Créer la nouvelle colonne document_type_id
ALTER TABLE demandes ADD COLUMN IF NOT EXISTS document_type_id INTEGER;

-- 5. Mapper les anciennes valeurs enum vers les nouveaux IDs
UPDATE demandes 
SET document_type_id = CASE 
    WHEN document_type = 'PASSEPORT' THEN 1
    WHEN document_type = 'ACTE_NAISSANCE' THEN 2
    WHEN document_type = 'CERTIFICAT_MARIAGE' THEN 3
    WHEN document_type = 'CARTE_IDENTITE' THEN 5
    WHEN document_type = 'AUTRE' THEN 6
    ELSE 1 -- Fallback vers Passeport
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

-- 7. Ajouter la contrainte de clé étrangère
ALTER TABLE demandes 
ADD CONSTRAINT fk_demandes_document_type 
FOREIGN KEY (document_type_id) REFERENCES document_types(id);

-- 8. Rendre la colonne NOT NULL
ALTER TABLE demandes ALTER COLUMN document_type_id SET NOT NULL;

-- 9. Supprimer l'ancienne colonne document_type (optionnel - commenter si vous voulez garder l'historique)
-- ALTER TABLE demandes DROP COLUMN document_type;

-- 10. Vérification finale
SELECT 'Vérification finale:' as info;
SELECT 
    'Colonnes demandes' as table_info,
    column_name, 
    data_type, 
    is_nullable
FROM information_schema.columns 
WHERE table_name = 'demandes' AND column_name LIKE '%document%'
UNION ALL
SELECT 
    'Contraintes FK' as table_info,
    tc.constraint_name as column_name,
    'FOREIGN KEY' as data_type,
    'YES' as is_nullable
FROM information_schema.table_constraints tc
WHERE tc.table_name = 'demandes' AND tc.constraint_type = 'FOREIGN KEY'
AND tc.constraint_name LIKE '%document%';

-- 11. Test de la relation
SELECT 'Test de la relation:' as info;
SELECT 
    d.id as demande_id,
    d.first_name,
    d.last_name,
    dt.id as type_id,
    dt.libelle as type_libelle
FROM demandes d
JOIN document_types dt ON d.document_type_id = dt.id
ORDER BY d.id
LIMIT 10;

-- 12. Message de confirmation
SELECT 'Migration terminée avec succès!' as result;
SELECT 'La table demandes utilise maintenant la relation JPA avec document_types.' as instruction;
