-- Script de diagnostic de la base de données pour la création de demandes
-- Exécuter ce script pour vérifier que toutes les tables et données nécessaires existent

-- 1. Vérifier la structure des tables principales
SELECT '=== STRUCTURE DES TABLES ===' as info;

-- Table des civilités
SELECT 'Civilités' as table_name, COUNT(*) as count FROM civilites;

-- Table des pays
SELECT 'Pays' as table_name, COUNT(*) as count FROM pays;

-- Table des types de documents
SELECT 'Types de documents' as table_name, COUNT(*) as count FROM document_types;

-- Table des utilisateurs
SELECT 'Utilisateurs' as table_name, COUNT(*) as count FROM users;

-- Table des demandes
SELECT 'Demandes' as table_name, COUNT(*) as count FROM demandes;

-- Table des adresses
SELECT 'Adresses' as table_name, COUNT(*) as count FROM adresses;

-- 2. Vérifier les données de référence
SELECT '=== DONNÉES DE RÉFÉRENCE ===' as info;

-- Civilités disponibles
SELECT 'Civilités:' as info;
SELECT id, libelle FROM civilites ORDER BY id;

-- Pays disponibles (limiter à 10 pour la lisibilité)
SELECT 'Pays (10 premiers):' as info;
SELECT id, nom FROM pays ORDER BY id LIMIT 10;

-- Types de documents disponibles
SELECT 'Types de documents:' as info;
SELECT id, libelle, is_active FROM document_types ORDER BY id;

-- 3. Vérifier les utilisateurs avec leurs rôles
SELECT '=== UTILISATEURS ===' as info;
SELECT id, email, role, email_verified FROM users ORDER BY id;

-- 4. Vérifier la structure des demandes existantes
SELECT '=== STRUCTURE DES DEMANDES ===' as info;
SELECT 
    d.id,
    d.first_name,
    d.last_name,
    d.status,
    d.created_at,
    c.libelle as civilite,
    p.nom as pays_naissance,
    dt.libelle as type_document,
    u.email as utilisateur
FROM demandes d
LEFT JOIN civilites c ON d.civilite_id = c.id
LEFT JOIN pays p ON d.birth_country_id = p.id
LEFT JOIN document_types dt ON d.document_type_id = dt.id
LEFT JOIN users u ON d.user_id = u.id
ORDER BY d.id DESC
LIMIT 5;

-- 5. Vérifier les contraintes de clés étrangères
SELECT '=== VÉRIFICATION DES CLÉS ÉTRANGÈRES ===' as info;

-- Vérifier que les civilités référencées existent
SELECT 'Civilités manquantes:' as info;
SELECT DISTINCT d.civilite_id 
FROM demandes d 
LEFT JOIN civilites c ON d.civilite_id = c.id 
WHERE c.id IS NULL AND d.civilite_id IS NOT NULL;

-- Vérifier que les pays référencés existent
SELECT 'Pays de naissance manquants:' as info;
SELECT DISTINCT d.birth_country_id 
FROM demandes d 
LEFT JOIN pays p ON d.birth_country_id = p.id 
WHERE p.id IS NULL AND d.birth_country_id IS NOT NULL;

-- Vérifier que les types de documents référencés existent
SELECT 'Types de documents manquants:' as info;
SELECT DISTINCT d.document_type_id 
FROM demandes d 
LEFT JOIN document_types dt ON d.document_type_id = dt.id 
WHERE dt.id IS NULL AND d.document_type_id IS NOT NULL;

-- 6. Vérifier les permissions des utilisateurs
SELECT '=== PERMISSIONS UTILISATEURS ===' as info;
SELECT 
    u.email,
    u.role,
    u.email_verified,
    CASE 
        WHEN u.role IN ('ADMIN', 'AGENT', 'USER') THEN '✅ Peut créer des demandes'
        ELSE '❌ Rôle insuffisant'
    END as permission_demandes
FROM users u
ORDER BY u.role, u.email;
