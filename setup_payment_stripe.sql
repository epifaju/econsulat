-- Exécuter ce script une fois pour activer le paiement Stripe (option B).
-- Table des paiements
CREATE TABLE IF NOT EXISTS payments (
    id SERIAL PRIMARY KEY,
    demande_id INTEGER NOT NULL UNIQUE REFERENCES demandes(id) ON DELETE CASCADE,
    stripe_session_id VARCHAR(255) NOT NULL UNIQUE,
    amount_cents INTEGER NOT NULL,
    currency VARCHAR(3) NOT NULL DEFAULT 'eur',
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    paid_at TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_payments_demande_id ON payments(demande_id);
CREATE INDEX IF NOT EXISTS idx_payments_stripe_session_id ON payments(stripe_session_id);
CREATE INDEX IF NOT EXISTS idx_payments_status ON payments(status);

-- Prix par type de document (centimes). 1000 = 10,00 EUR.
ALTER TABLE document_types ADD COLUMN IF NOT EXISTS price_cents INTEGER DEFAULT 1000;
