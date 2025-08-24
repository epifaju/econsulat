-- Script pour identifier et corriger les doublons dans document_types
-- Exécuter AVANT de continuer la migration

-- 1. Identifier les doublons
SELECT 'IDENTIFICATION DES DOUBLONS:' as info;
SELECT 
    libelle,
    COUNT(*) as nombre_occurrences,
    array_agg(id ORDER BY id) as ids_dupliques
FROM document_types 
GROUP BY libelle 
HAVING COUNT(*) > 1
ORDER BY libelle;

-- 2. Voir tous les enregistrements avec leurs IDs
SELECT 'TOUS LES ENREGISTREMENTS:' as info;
SELECT 
    id,
    libelle,
    description,
    is_active,
    created_at
FROM document_types 
ORDER BY libelle, id;

-- 3. Identifier les demandes qui utilisent chaque ID
SELECT 'UTILISATION DES IDs PAR LES DEMANDES:' as info;
SELECT 
    dt.id,
    dt.libelle,
    COUNT(d.id) as nombre_demandes,
    CASE WHEN COUNT(d.id) > 0 THEN '⚠️ UTILISÉ' ELSE '🔒 LIBRE' END as statut
FROM document_types dt
LEFT JOIN demandes d ON dt.id = d.document_type_id
GROUP BY dt.id, dt.libelle
ORDER BY dt.id;

-- 4. Recommandations de nettoyage
SELECT 'RECOMMANDATIONS:' as info;
SELECT 
    'Si "Carte d''identité" a 2 IDs et qu''un seul est utilisé, supprimer l''autre.' as action1,
    'Si les 2 sont utilisés, consolider vers un seul ID.' as action2,
    'Vérifier qu''aucune demande n''utilise l''ID à supprimer.' as action3;

-- 5. Script de nettoyage (à exécuter APRÈS vérification)
SELECT 'SCRIPT DE NETTOYAGE (à exécuter après vérification):' as info;
SELECT 
    '-- Supprimer le doublon de "Carte d''identité" (ID le plus récent)' as cleanup1,
    '-- DELETE FROM document_types WHERE id = (SELECT MAX(id) FROM document_types WHERE libelle = ''Carte d''identité'');' as cleanup2,
    '-- Vérifier qu''il ne reste qu''un seul enregistrement par libellé' as cleanup3;
