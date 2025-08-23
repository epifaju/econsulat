-- Script de création de la table notifications
-- Table pour stocker l'historique des emails de notification envoyés

CREATE TABLE IF NOT EXISTS notifications (
    id_notification SERIAL PRIMARY KEY,
    id_demande BIGINT NOT NULL,
    id_utilisateur BIGINT NOT NULL,
    email_destinataire VARCHAR(255) NOT NULL,
    objet VARCHAR(500) NOT NULL,
    contenu TEXT NOT NULL,
    statut VARCHAR(50) NOT NULL DEFAULT 'ENVOYE',
    date_envoi TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    -- Contraintes de clés étrangères
    CONSTRAINT fk_notification_demande FOREIGN KEY (id_demande) REFERENCES demandes(id) ON DELETE CASCADE,
    CONSTRAINT fk_notification_utilisateur FOREIGN KEY (id_utilisateur) REFERENCES users(id) ON DELETE CASCADE,
    
    -- Contraintes de validation
    CONSTRAINT chk_statut CHECK (statut IN ('ENVOYE', 'ECHEC', 'EN_COURS'))
);

-- Index pour améliorer les performances des requêtes
CREATE INDEX IF NOT EXISTS idx_notifications_utilisateur ON notifications(id_utilisateur);
CREATE INDEX IF NOT EXISTS idx_notifications_demande ON notifications(id_demande);
CREATE INDEX IF NOT EXISTS idx_notifications_date_envoi ON notifications(date_envoi);

-- Commentaires sur la table
COMMENT ON TABLE notifications IS 'Table de stockage des notifications par email envoyées aux utilisateurs';
COMMENT ON COLUMN notifications.id_notification IS 'Identifiant unique de la notification';
COMMENT ON COLUMN notifications.id_demande IS 'Référence vers la demande concernée';
COMMENT ON COLUMN notifications.id_utilisateur IS 'Référence vers l''utilisateur destinataire';
COMMENT ON COLUMN notifications.email_destinataire IS 'Adresse email du destinataire';
COMMENT ON COLUMN notifications.objet IS 'Objet de l''email';
COMMENT ON COLUMN notifications.contenu IS 'Contenu complet de l''email';
COMMENT ON COLUMN notifications.statut IS 'Statut de l''envoi (ENVOYE, ECHEC, EN_COURS)';
COMMENT ON COLUMN notifications.date_envoi IS 'Date et heure de l''envoi';
