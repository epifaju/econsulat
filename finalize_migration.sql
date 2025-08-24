-- Script pour finaliser la migration : ajouter les contraintes
-- Exécuter SEULEMENT après avoir vérifié que la migration s'est bien passée

-- 1. Vérification finale avant d'ajouter les contraintes
SELECT 'Vérification finale avant contraintes:' as info;
SELECT 
    d.document_type_id,
    dt.libelle,
    COUNT(*) as nombre_demandes,
    CASE WHEN dt.id IS NOT NULL THEN '✅ OK' ELSE '❌ PROBLÈME' END as statut
FROM demandes d
LEFT JOIN document_types dt ON d.document_type_id = dt.id
GROUP BY d.document_type_id, dt.libelle, dt.id
ORDER BY d.document_type_id;

-- 2. Vérifier qu'il n'y a pas d'IDs orphelins
SELECT 'Vérification des IDs orphelins:' as info;
SELECT COUNT(*) as ids_orphelins
FROM demandes d
LEFT JOIN document_types dt ON d.document_type_id = dt.id
WHERE dt.id IS NULL;

-- 3. Si tout est OK (0 orphelins), ajouter la contrainte FK
ALTER TABLE demandes 
ADD CONSTRAINT fk_demandes_document_type 
FOREIGN KEY (document_type_id) REFERENCES document_types(id);

-- 4. Rendre la colonne NOT NULL
ALTER TABLE demandes ALTER COLUMN document_type_id SET NOT NULL;

-- 5. Vérification finale
SELECT 'Migration terminée avec succès !' as result;
SELECT 'Contraintes ajoutées:' as info;
SELECT 
    tc.constraint_name,
    tc.table_name,
    tc.constraint_type
FROM information_schema.table_constraints tc
WHERE tc.table_name = 'demandes' 
AND tc.constraint_name LIKE '%document%';

-- 6. Test final : vérifier qu'on peut joindre les tables
SELECT 'Test de jointure final:' as info;
SELECT 
    d.id as demande_id,
    d.first_name,
    d.last_name,
    dt.id as type_id,
    dt.libelle as type_libelle
FROM demandes d
JOIN document_types dt ON d.document_type_id = dt.id
ORDER BY d.id
LIMIT 5;

SELECT 'ᅟᅠ MIGRATION COMPLÈTE ! La table document_types est maintenant utilisée.' as success;
