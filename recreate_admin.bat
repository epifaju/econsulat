@echo off
echo ========================================
echo    🔄 Recréation Compte Admin
echo ========================================
echo.

echo 🔄 Suppression de l'ancien compte admin...
"C:\Program Files\PostgreSQL\13\bin\psql.exe" -U postgres -d econsulat -c "DELETE FROM users WHERE username = 'admin';"

echo.
echo 🔄 Création du nouveau compte admin avec BCrypt...
"C:\Program Files\PostgreSQL\13\bin\psql.exe" -U postgres -d econsulat -c "INSERT INTO users (username, password, role) VALUES ('admin', '\$2a\$10\$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'ADMIN');"

if %errorlevel% equ 0 (
    echo ✅ Compte admin recréé avec succès!
    echo.
    echo 📋 Informations de connexion:
    echo    Nom d'utilisateur: admin
    echo    Mot de passe: admin123
    echo    Rôle: ADMIN
) else (
    echo ❌ Erreur lors de la recréation du compte
)

echo.
echo 📊 Vérification des utilisateurs:
"C:\Program Files\PostgreSQL\13\bin\psql.exe" -U postgres -d econsulat -c "SELECT id, username, role FROM users ORDER BY id;"

echo.
echo 🔍 Test de l'endpoint d'authentification:
echo    URL: http://localhost:8080/api/auth/login
echo    Méthode: POST
echo    Content-Type: application/json
echo    Body: {"username":"admin","password":"admin123"}
echo.
pause 