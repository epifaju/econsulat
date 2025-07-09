-- Script pour créer un nouveau compte admin
-- Le mot de passe sera encodé avec BCrypt

-- Option 1: Créer un admin avec le mot de passe 'admin123' (déjà encodé)
INSERT INTO users (username, password, role) 
VALUES ('monadmin', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'ADMIN')
ON CONFLICT (username) DO NOTHING;

-- Option 2: Créer un admin avec un mot de passe personnalisé
-- Remplacez 'votre_mot_de_passe' par le mot de passe souhaité
-- INSERT INTO users (username, password, role) 
-- VALUES ('votre_admin', 'votre_mot_de_passe_encodé', 'ADMIN')
-- ON CONFLICT (username) DO NOTHING;

-- Vérifier les utilisateurs existants
SELECT id, username, role, created_at FROM users ORDER BY id; 