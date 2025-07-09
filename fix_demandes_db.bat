@echo off
echo ========================================
echo   Correction de la base de donnees
echo   des demandes eConsulat
echo ========================================
echo.

REM Vérifier si PostgreSQL est installé
where psql >nul 2>nul
if %errorlevel% neq 0 (
    echo ERREUR: PostgreSQL n'est pas installé ou pas dans le PATH
    echo Veuillez installer PostgreSQL et réessayer
    pause
    exit /b 1
)

echo Vérification de la connexion à PostgreSQL...
psql -U postgres -c "SELECT version();" >nul 2>nul
if %errorlevel% neq 0 (
    echo ERREUR: Impossible de se connecter à PostgreSQL
    echo Vérifiez que PostgreSQL est démarré et que l'utilisateur 'postgres' existe
    pause
    exit /b 1
)

echo.
echo Connexion à la base de données 'econsulat'...
psql -U postgres -d econsulat -f check_demandes_db.sql

if %errorlevel% equ 0 (
    echo.
    echo ========================================
    echo   Base de donnees corrigee avec succes!
    echo ========================================
    echo.
    echo Les tables et donnees de reference ont ete creees:
    echo - Table civilites avec 3 valeurs (Monsieur, Madame, Mademoiselle)
    echo - Table pays avec 100+ pays du monde
    echo - Tables adresses et demandes pretes a recevoir des donnees
    echo.
    echo Vous pouvez maintenant redemarrer l'application
    echo et tester le formulaire de nouvelle demande.
) else (
    echo.
    echo ========================================
    echo   ERREUR lors de la correction
    echo ========================================
    echo.
    echo Vérifiez les messages d'erreur ci-dessus
    echo et assurez-vous que:
    echo - PostgreSQL est démarré
    echo - La base 'econsulat' existe
    echo - L'utilisateur 'postgres' a les droits nécessaires
)

echo.
pause 