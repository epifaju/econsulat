-- Langue préférée pour les e-mails et l'interface (fr, pt). Null = français par défaut.
ALTER TABLE users ADD COLUMN IF NOT EXISTS preferred_locale VARCHAR(5) NULL;
