@echo off
echo ========================================
echo Configuration de la base de donnees eConsulat
echo ========================================

REM Vérifier si PostgreSQL est installé
where psql >nul 2>nul
if %errorlevel% neq 0 (
    echo ERREUR: PostgreSQL n'est pas installé ou n'est pas dans le PATH
    echo Veuillez installer PostgreSQL et réessayer
    pause
    exit /b 1
)

REM Variables de configuration
set DB_NAME=econsulat
set DB_USER=postgres
set DB_PASSWORD=postgres
set DB_HOST=localhost
set DB_PORT=5432

echo.
echo Connexion à la base de données...
echo Nom de la base: %DB_NAME%
echo Utilisateur: %DB_USER%
echo Hôte: %DB_HOST%
echo Port: %DB_PORT%
echo.

REM Exécuter le script SQL
echo Exécution du script de création des tables...
psql -h %DB_HOST% -p %DB_PORT% -U %DB_USER% -d %DB_NAME% -f backend\src\main\resources\schema_demandes.sql

if %errorlevel% equ 0 (
    echo.
    echo ========================================
    echo SUCCES: Base de données configurée
    echo ========================================
    echo Les tables suivantes ont été créées:
    echo - civilites
    echo - pays  
    echo - adresses
    echo - demandes
    echo.
    echo Les données de référence ont été insérées:
    echo - 3 civilités (Monsieur, Madame, Mademoiselle)
    echo - 100+ pays
    echo.
) else (
    echo.
    echo ========================================
    echo ERREUR: Échec de la configuration
    echo ========================================
    echo Vérifiez que:
    echo - PostgreSQL est démarré
    echo - Les identifiants sont corrects
    echo - La base de données existe
    echo.
)

pause 