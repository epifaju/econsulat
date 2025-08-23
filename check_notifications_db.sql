-- Script de diagnostic de la table notifications
-- Exécuter ce script pour vérifier l'état de la base de données

-- 1. Vérifier l'existence de la table notifications
SELECT 
    table_name,
    table_type
FROM information_schema.tables 
WHERE table_name = 'notifications';

-- 2. Vérifier la structure de la table notifications
SELECT 
    column_name,
    data_type,
    is_nullable,
    column_default
FROM information_schema.columns 
WHERE table_name = 'notifications'
ORDER BY ordinal_position;

-- 3. Vérifier les contraintes de clés étrangères
SELECT 
    tc.constraint_name,
    tc.table_name,
    kcu.column_name,
    ccu.table_name AS foreign_table_name,
    ccu.column_name AS foreign_column_name
FROM information_schema.table_constraints AS tc
JOIN information_schema.key_column_usage AS kcu
    ON tc.constraint_name = kcu.constraint_name
JOIN information_schema.constraint_column_usage AS ccu
    ON ccu.constraint_name = tc.constraint_name
WHERE tc.constraint_type = 'FOREIGN KEY' 
    AND tc.table_name = 'notifications';

-- 4. Vérifier les index sur la table notifications
SELECT 
    indexname,
    indexdef
FROM pg_indexes 
WHERE tablename = 'notifications';

-- 5. Vérifier les données existantes
SELECT 
    COUNT(*) as total_notifications,
    COUNT(CASE WHEN id_demande IS NOT NULL THEN 1 END) as notifications_with_demande,
    COUNT(CASE WHEN id_utilisateur IS NOT NULL THEN 1 END) as notifications_with_user
FROM notifications;

-- 6. Vérifier les relations avec la table demandes
SELECT 
    'demandes' as table_name,
    COUNT(*) as total_rows
FROM demandes
UNION ALL
SELECT 
    'notifications' as table_name,
    COUNT(*) as total_rows
FROM notifications;

-- 7. Vérifier les permissions sur la table notifications
SELECT 
    grantee,
    privilege_type,
    is_grantable
FROM information_schema.role_table_grants 
WHERE table_name = 'notifications';

-- 8. Test de jointure entre notifications et demandes
SELECT 
    n.id_notification,
    n.id_demande,
    d.id as demande_id,
    n.objet,
    n.date_envoi
FROM notifications n
LEFT JOIN demandes d ON n.id_demande = d.id
LIMIT 10;

-- 9. Vérifier les erreurs de contraintes potentielles
SELECT 
    n.id_notification,
    n.id_demande,
    CASE 
        WHEN d.id IS NULL THEN '❌ Demande inexistante'
        ELSE '✅ Demande existante'
    END as status_demande
FROM notifications n
LEFT JOIN demandes d ON n.id_demande = d.id
WHERE d.id IS NULL;

-- 10. Nettoyer les notifications orphelines (décommenter si nécessaire)
-- DELETE FROM notifications 
-- WHERE id_demande NOT IN (SELECT id FROM demandes);
