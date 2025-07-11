-- Nettoyer les documents générés pour forcer la régénération
DELETE FROM generated_documents WHERE id > 0;

-- Réinitialiser la séquence d'ID (si nécessaire)
-- ALTER SEQUENCE generated_documents_id_seq RESTART WITH 1; 