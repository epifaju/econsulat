-- Script pour créer les tables nécessaires à l'interface administrateur eConsulat

-- 1. Table des types de documents
CREATE TABLE IF NOT EXISTS document_types (
    id SERIAL PRIMARY KEY,
    libelle VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    template_path VARCHAR(255),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Table des documents générés
CREATE TABLE IF NOT EXISTS generated_documents (
    id SERIAL PRIMARY KEY,
    demande_id INTEGER NOT NULL REFERENCES demandes(id) ON DELETE CASCADE,
    document_type_id INTEGER NOT NULL REFERENCES document_types(id),
    file_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    file_size BIGINT,
    status VARCHAR(20) NOT NULL DEFAULT 'GENERATED',
    created_by INTEGER NOT NULL REFERENCES users(id),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    downloaded_at TIMESTAMP,
    expires_at TIMESTAMP
);

-- 3. Insérer les types de documents par défaut
INSERT INTO document_types (libelle, description, template_path) VALUES 
('Passeport', 'Document de voyage officiel', 'templates/passeport_template.docx') ON CONFLICT (libelle) DO NOTHING,
('Acte de naissance', 'Certificat de naissance officiel', 'templates/acte_naissance_template.docx') ON CONFLICT (libelle) DO NOTHING,
('Certificat de mariage', 'Certificat de mariage officiel', 'templates/certificat_mariage_template.docx') ON CONFLICT (libelle) DO NOTHING,
('Carte d''identité', 'Carte d''identité nationale', 'templates/carte_identite_template.docx') ON CONFLICT (libelle) DO NOTHING,
('Certificat d''hébergement', 'Certificat d''hébergement', 'templates/certificat_hebergement_template.docx') ON CONFLICT (libelle) DO NOTHING,
('Certificat de célibat', 'Certificat de célibat', 'templates/certificat_celibat_template.docx') ON CONFLICT (libelle) DO NOTHING,
('Certificat de résidence', 'Certificat de résidence', 'templates/certificat_residence_template.docx') ON CONFLICT (libelle) DO NOTHING;

-- 4. Créer un utilisateur agent pour les tests
INSERT INTO users (first_name, last_name, email, password, role, email_verified, created_at) 
VALUES (
    'Agent',
    'Test',
    'agent@econsulat.com',
    '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', -- password: agent123
    'AGENT',
    true,
    CURRENT_TIMESTAMP
) ON CONFLICT (email) DO NOTHING;

-- 5. Créer les index pour optimiser les performances
CREATE INDEX IF NOT EXISTS idx_generated_documents_demande_id ON generated_documents(demande_id);
CREATE INDEX IF NOT EXISTS idx_generated_documents_document_type_id ON generated_documents(document_type_id);
CREATE INDEX IF NOT EXISTS idx_generated_documents_created_by ON generated_documents(created_by);
CREATE INDEX IF NOT EXISTS idx_generated_documents_status ON generated_documents(status);
CREATE INDEX IF NOT EXISTS idx_generated_documents_created_at ON generated_documents(created_at);

CREATE INDEX IF NOT EXISTS idx_document_types_is_active ON document_types(is_active);
CREATE INDEX IF NOT EXISTS idx_document_types_libelle ON document_types(libelle);

-- 6. Vérifier les données insérées
SELECT 'Types de documents créés:' as info;
SELECT id, libelle, description, is_active FROM document_types ORDER BY libelle;

SELECT 'Utilisateurs dans le système:' as info;
SELECT id, first_name, last_name, email, role, email_verified FROM users ORDER BY role, email;

SELECT 'Tables créées avec succès!' as result; 