-- Vérifier les utilisateurs existants
SELECT id, username, password, role FROM users;

-- Supprimer les utilisateurs existants
DELETE FROM users WHERE username IN ('admin', 'user');

-- Créer l'admin avec mot de passe BCrypt pour "admin123"
INSERT INTO users (username, password, role) VALUES 
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'ADMIN');

-- Créer l'utilisateur avec mot de passe BCrypt pour "user123"
INSERT INTO users (username, password, role) VALUES 
('user', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a', 'USER');

-- Vérifier le résultat
SELECT id, username, password, role FROM users; 