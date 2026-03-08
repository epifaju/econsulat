-- Colonne pour réaffichage traduit du message de notification (statut au moment de l'envoi).
ALTER TABLE notifications ADD COLUMN IF NOT EXISTS new_status VARCHAR(32) NULL;
