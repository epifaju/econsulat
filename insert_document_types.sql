-- Script pour insérer les types de documents manquants
-- Exécuter ce script pour corriger l'erreur de génération PDF

-- 1. Insérer d'abord les nouveaux types de documents
INSERT INTO document_types (libelle, description, template_path, is_active, created_at, updated_at) VALUES 
('Passeport', 'Document de voyage officiel', 'templates/passeport_template.docx', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Acte de naissance', 'Certificat de naissance officiel', 'templates/acte_naissance_template.docx', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Certificat de mariage', 'Certificat de mariage officiel', 'templates/certificat_mariage_template.docx', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Certificat de coutume', 'Certificat de coutume officiel', 'templates/certificat_coutume_template.docx', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Carte d''identité', 'Carte d''identité nationale', 'templates/carte_identite_template.docx', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Certificat d''hébergement', 'Certificat d''hébergement', 'templates/certificat_hebergement_template.docx', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Certificat de célibat', 'Certificat de célibat', 'templates/certificat_celibat_template.docx', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Certificat de résidence', 'Certificat de résidence', 'templates/certificat_residence_template.docx', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 2. Mettre à jour les références existantes dans generated_documents
-- Remplacer les références au type "Certificat de Coutume" par "Passeport"
UPDATE generated_documents 
SET document_type_id = (SELECT id FROM document_types WHERE libelle = 'Passeport' LIMIT 1)
WHERE document_type_id = 1; -- ID du "Certificat de Coutume"

-- 3. Maintenant on peut supprimer l'ancien type
DELETE FROM document_types WHERE libelle = 'Certificat de Coutume';

-- 4. Vérifier les types de documents insérés
SELECT 'Types de documents après insertion:' as info;
SELECT id, libelle, description, is_active FROM document_types ORDER BY libelle;

-- 5. Vérifier que les références sont correctes
SELECT 'Vérification des références:' as info;
SELECT gd.id, gd.file_name, dt.libelle as document_type
FROM generated_documents gd
JOIN document_types dt ON gd.document_type_id = dt.id
ORDER BY gd.id;
