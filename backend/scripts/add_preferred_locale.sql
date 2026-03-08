-- Ajoute la colonne preferred_locale à la table users (langue préférée FR/PT).
-- À exécuter une seule fois sur la base PostgreSQL si la colonne n'existe pas.

ALTER TABLE users ADD COLUMN IF NOT EXISTS preferred_locale VARCHAR(5) NULL;
