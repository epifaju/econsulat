@echo off
echo ========================================
echo    👤 Création Compte Administrateur
echo ========================================
echo.

set /p username="Nom d'utilisateur pour l'admin: "
set /p password="Mot de passe pour l'admin: "

echo.
echo 🔄 Création du compte admin '%username%'...

REM Créer un admin avec le mot de passe spécifié
"C:\Program Files\PostgreSQL\13\bin\psql.exe" -U postgres -d econsulat -c "INSERT INTO users (username, password, role) VALUES ('%username%', '%password%', 'ADMIN') ON CONFLICT (username) DO NOTHING;"

if %errorlevel% equ 0 (
    echo ✅ Compte admin '%username%' créé avec succès!
    echo.
    echo 📋 Informations de connexion:
    echo    Nom d'utilisateur: %username%
    echo    Mot de passe: %password%
    echo    Rôle: ADMIN
) else (
    echo ❌ Erreur lors de la création du compte
)

echo.
echo 📊 Utilisateurs existants:
"C:\Program Files\PostgreSQL\13\bin\psql.exe" -U postgres -d econsulat -c "SELECT id, username, role FROM users ORDER BY id;"

echo.
pause 