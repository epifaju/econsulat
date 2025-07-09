@echo off
echo ========================================
echo Configuration de l'interface admin eConsulat
echo ========================================

REM Vérifier si PostgreSQL est en cours d'exécution
echo Vérification de PostgreSQL...
pg_isready -U postgres >nul 2>&1
if %errorlevel% neq 0 (
    echo ERREUR: PostgreSQL n'est pas en cours d'exécution
    echo Veuillez démarrer PostgreSQL et réessayer
    pause
    exit /b 1
)

REM Exécuter le script SQL pour créer les tables admin
echo Création des tables pour l'interface admin...
psql -U postgres -d econsulat -f setup_admin_interface.sql

if %errorlevel% neq 0 (
    echo ERREUR: Impossible d'exécuter le script SQL
    pause
    exit /b 1
)

echo.
echo ========================================
echo Interface admin configurée avec succès !
echo ========================================
echo.
echo Comptes de test créés :
echo - Admin: admin@econsulat.com / admin123
echo - Agent: agent@econsulat.com / agent123
echo.
echo Types de documents créés :
echo - Passeport
echo - Acte de naissance
echo - Certificat de mariage
echo - Carte d'identité
echo - Certificat d'hébergement
echo - Certificat de célibat
echo - Certificat de résidence
echo.
echo Pour démarrer l'application :
echo 1. Ouvrez un terminal dans le dossier backend
echo 2. Exécutez: mvn spring-boot:run
echo 3. Ouvrez un autre terminal dans le dossier frontend
echo 4. Exécutez: npm run dev
echo.
echo L'interface admin sera accessible à :
echo http://localhost:5173 (connectez-vous avec admin@econsulat.com)
echo.
pause 