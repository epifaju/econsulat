-- Supprimer tous les utilisateurs existants
DELETE FROM users;

-- Créer l'admin avec un mot de passe simple (sera encodé par Spring)
INSERT INTO users (username, password, role) VALUES 
('admin', 'admin123', 'ADMIN');

-- Créer l'utilisateur avec un mot de passe simple (sera encodé par Spring)
INSERT INTO users (username, password, role) VALUES 
('user', 'user123', 'USER');

-- Vérifier le résultat
SELECT id, username, password, role FROM users; 