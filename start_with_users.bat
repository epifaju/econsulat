@echo off
echo 🚀 Démarrage d'eConsulat avec gestion des utilisateurs...
echo.

echo 📊 Vérification de la base de données...
echo.

REM Vérifier si PostgreSQL est installé
where psql >nul 2>nul
if %errorlevel% neq 0 (
    echo ❌ PostgreSQL n'est pas détecté dans le PATH
    echo Veuillez installer PostgreSQL ou l'ajouter au PATH
    pause
    exit /b 1
)

echo ✅ PostgreSQL détecté

REM Mettre à jour la table users
echo 🔄 Mise à jour de la table users...
psql -h localhost -U postgres -d econsulat_db -f update_users_table.sql
if %errorlevel% neq 0 (
    echo ❌ Erreur lors de la mise à jour de la table users
    echo Vérifiez que la base de données est accessible
    pause
    exit /b 1
)

echo ✅ Table users mise à jour avec succès

echo.
echo 🔧 Configuration requise pour les emails:
echo.
echo 1. Configurez votre service SMTP dans backend/src/main/resources/application.properties
echo 2. Consultez EMAIL_CONFIG.md pour les instructions détaillées
echo.
echo ⚠️  IMPORTANT: Sans configuration email, les nouveaux utilisateurs ne pourront pas se connecter
echo.

echo 🛑 Arrêt des processus Java existants...
taskkill /f /im java.exe >nul 2>nul
timeout /t 2 >nul

echo 🏠 Définition de JAVA_HOME...
set JAVA_HOME=C:\Program Files\Java\jdk-17
echo ✅ JAVA_HOME défini: %JAVA_HOME%

echo 🔨 Compilation du projet...
cd backend
call mvnw.cmd clean compile
if %errorlevel% neq 0 (
    echo ❌ Erreur de compilation
    pause
    exit /b 1
)

echo 🚀 Démarrage du backend...
start "eConsulat Backend" cmd /k "call mvnw.cmd spring-boot:run"

echo ⏳ Attente du démarrage du backend...
timeout /t 10 >nul

echo 🌐 Démarrage du frontend...
cd ..\frontend
start "eConsulat Frontend" cmd /k "npm run dev"

echo.
echo ✅ eConsulat démarré avec succès!
echo 🌐 Frontend: http://localhost:5173
echo 🔧 Backend: http://localhost:8080
echo.
echo 📧 Comptes de test:
echo    Admin: admin@econsulat.com / admin123
echo    User: user@econsulat.com / user123
echo.
echo 👥 Nouveaux utilisateurs:
echo    - Seuls les admins peuvent créer des utilisateurs
echo    - Les nouveaux utilisateurs reçoivent un email de validation
echo    - Ils doivent cliquer sur le lien dans l'email pour activer leur compte
echo.
pause 