-- Initialisation du schéma eConsulat (source de vérité Flyway)
-- Remarque : ce script est idempotent (IF NOT EXISTS / ON CONFLICT DO NOTHING)

-- 1. Table users (avec preferred_locale et rôle CITIZEN)
CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) CHECK (role IN ('USER', 'ADMIN', 'AGENT', 'CITIZEN')) NOT NULL DEFAULT 'USER',
    email_verified BOOLEAN DEFAULT FALSE,
    preferred_locale VARCHAR(5),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);

-- 2. Table citizens (optionnelle, utilisée par /api/citizens)
CREATE TABLE IF NOT EXISTS citizens (
    id SERIAL PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    birth_date DATE NOT NULL,
    birth_place VARCHAR(100) NOT NULL,
    file_path VARCHAR(500),
    pdf_path VARCHAR(500),
    document_type VARCHAR(100),
    status VARCHAR(20) DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    user_id INTEGER NOT NULL REFERENCES users(id)
);

-- 3. Table document_types (avant demandes)
CREATE TABLE IF NOT EXISTS document_types (
    id SERIAL PRIMARY KEY,
    libelle VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    template_path VARCHAR(500),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    price_cents INTEGER DEFAULT 1000,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 4. Civilités, pays, adresses
CREATE TABLE IF NOT EXISTS civilites (
    id SERIAL PRIMARY KEY,
    libelle VARCHAR(50) NOT NULL UNIQUE
);
CREATE TABLE IF NOT EXISTS pays (
    id SERIAL PRIMARY KEY,
    libelle VARCHAR(100) NOT NULL UNIQUE
);
CREATE TABLE IF NOT EXISTS adresses (
    id SERIAL PRIMARY KEY,
    street_name VARCHAR(255) NOT NULL,
    street_number VARCHAR(20) NOT NULL,
    box_number VARCHAR(20),
    postal_code VARCHAR(20) NOT NULL,
    city VARCHAR(100) NOT NULL,
    country_id INTEGER NOT NULL REFERENCES pays(id)
);

-- 5. Table demandes (avec document_type_id)
CREATE TABLE IF NOT EXISTS demandes (
    id SERIAL PRIMARY KEY,
    civilite_id INTEGER NOT NULL REFERENCES civilites(id),
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    birth_date DATE NOT NULL,
    birth_place VARCHAR(100) NOT NULL,
    birth_country_id INTEGER NOT NULL REFERENCES pays(id),
    adresse_id INTEGER NOT NULL REFERENCES adresses(id),
    father_first_name VARCHAR(100) NOT NULL,
    father_last_name VARCHAR(100) NOT NULL,
    father_birth_date DATE NOT NULL,
    father_birth_place VARCHAR(100) NOT NULL,
    father_birth_country_id INTEGER NOT NULL REFERENCES pays(id),
    mother_first_name VARCHAR(100) NOT NULL,
    mother_last_name VARCHAR(100) NOT NULL,
    mother_birth_date DATE NOT NULL,
    mother_birth_place VARCHAR(100) NOT NULL,
    mother_birth_country_id INTEGER NOT NULL REFERENCES pays(id),
    document_type_id INTEGER NOT NULL REFERENCES document_types(id),
    documents_path TEXT,
    status VARCHAR(32) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    user_id INTEGER NOT NULL REFERENCES users(id)
);
CREATE INDEX IF NOT EXISTS idx_demandes_user_id ON demandes(user_id);
CREATE INDEX IF NOT EXISTS idx_demandes_status ON demandes(status);

-- 6. Table generated_documents
CREATE TABLE IF NOT EXISTS generated_documents (
    id SERIAL PRIMARY KEY,
    demande_id INTEGER NOT NULL REFERENCES demandes(id) ON DELETE CASCADE,
    document_type_id INTEGER REFERENCES document_types(id),
    file_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    file_size BIGINT,
    status VARCHAR(50) NOT NULL DEFAULT 'GENERATED',
    created_by INTEGER NOT NULL REFERENCES users(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    downloaded_at TIMESTAMP,
    expires_at TIMESTAMP
);

-- 7. Table payments
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

-- 8. Table notifications
CREATE TABLE IF NOT EXISTS notifications (
    id_notification SERIAL PRIMARY KEY,
    id_demande BIGINT NOT NULL REFERENCES demandes(id) ON DELETE CASCADE,
    id_utilisateur BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    email_destinataire VARCHAR(255) NOT NULL,
    objet VARCHAR(500) NOT NULL,
    contenu TEXT NOT NULL,
    new_status VARCHAR(32),
    statut VARCHAR(50) NOT NULL DEFAULT 'ENVOYE',
    date_envoi TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_notifications_id_demande ON notifications(id_demande);
CREATE INDEX IF NOT EXISTS idx_notifications_id_utilisateur ON notifications(id_utilisateur);

-- 9. Données de base : civilités et pays (échantillon)
INSERT INTO civilites (libelle) VALUES ('Monsieur'), ('Madame'), ('Mademoiselle') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO pays (libelle) VALUES ('Guinée-Bissau'), ('France'), ('Portugal'), ('Sénégal'), ('Cap-Vert')
ON CONFLICT (libelle) DO NOTHING;

-- 10. Types de documents initiaux
INSERT INTO document_types (libelle, description, is_active, price_cents, created_at, updated_at)
VALUES
  ('Acte de naissance', 'Certificat de naissance officiel', TRUE, 1000, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('Certificat de mariage', 'Certificat de mariage officiel', TRUE, 1000, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('Passeport', 'Document de voyage officiel', TRUE, 1000, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('Carte d''identité', 'Carte d''identité nationale', TRUE, 1000, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('Certificat d''hébergement', 'Certificat d''hébergement', TRUE, 1000, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('Certificat de célibat', 'Certificat de célibat', TRUE, 1000, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('Certificat de résidence', 'Certificat de résidence', TRUE, 1000, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (libelle) DO NOTHING;

-- 11. Utilisateurs par défaut (BCrypt : admin123 / user123)
INSERT INTO users (first_name, last_name, email, password, role, email_verified, created_at)
VALUES ('Admin', 'System', 'admin@econsulat.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'ADMIN', TRUE, CURRENT_TIMESTAMP)
ON CONFLICT (email) DO NOTHING;
INSERT INTO users (first_name, last_name, email, password, role, email_verified, created_at)
VALUES ('User', 'Test', 'user@econsulat.com', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a', 'USER', TRUE, CURRENT_TIMESTAMP)
ON CONFLICT (email) DO NOTHING;

