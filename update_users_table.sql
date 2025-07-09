-- Script de mise à jour de la table users pour eConsulat
-- Ce script doit être exécuté sur la base de données existante

-- Sauvegarder les données existantes
CREATE TEMP TABLE users_backup AS SELECT * FROM users;

-- Supprimer la table users existante
DROP TABLE IF EXISTS users CASCADE;

-- Créer la nouvelle table users avec tous les champs requis
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) CHECK (role IN ('USER', 'ADMIN', 'AGENT')) NOT NULL DEFAULT 'USER',
    email_verified BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Créer un index sur l'email pour optimiser les recherches
CREATE INDEX idx_users_email ON users(email);

-- Insérer un utilisateur admin par défaut (mot de passe: admin123)
INSERT INTO users (first_name, last_name, email, password, role, email_verified, created_at) 
VALUES ('Admin', 'System', 'admin@econsulat.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'ADMIN', true, CURRENT_TIMESTAMP);

-- Insérer un utilisateur test (mot de passe: user123)
INSERT INTO users (first_name, last_name, email, password, role, email_verified, created_at) 
VALUES ('User', 'Test', 'user@econsulat.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'USER', true, CURRENT_TIMESTAMP);

-- Nettoyer la table temporaire
DROP TABLE users_backup; 