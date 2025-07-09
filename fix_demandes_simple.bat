@echo off
echo ========================================
echo   Correction simplifiee de la base
echo   de donnees des demandes eConsulat
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

echo ✅ PostgreSQL est installé
echo.

REM Vérifier la connexion à PostgreSQL
echo Test de connexion à PostgreSQL...
psql -U postgres -c "SELECT version();" >nul 2>nul
if %errorlevel% neq 0 (
    echo ❌ ERREUR: Impossible de se connecter à PostgreSQL
    echo Vérifiez que PostgreSQL est démarré et que l'utilisateur 'postgres' existe
    pause
    exit /b 1
)

echo ✅ Connexion à PostgreSQL réussie
echo.

REM Vérifier si la base de données existe
echo Test de connexion à la base 'econsulat'...
psql -U postgres -d econsulat -c "SELECT 1;" >nul 2>nul
if %errorlevel% neq 0 (
    echo ❌ La base de données 'econsulat' n'existe pas
    echo.
    echo Création de la base de données...
    createdb -U postgres econsulat
    if %errorlevel% neq 0 (
        echo ❌ ERREUR: Impossible de créer la base de données
        pause
        exit /b 1
    )
    echo ✅ Base de données 'econsulat' créée
) else (
    echo ✅ Base de données 'econsulat' existe
)
echo.

REM Exécuter le script SQL simplifié
echo Exécution du script de création des tables et données...
psql -U postgres -d econsulat -f setup_demandes_simple.sql

if %errorlevel% equ 0 (
    echo.
    echo ========================================
    echo   ✅ Base de donnees corrigee avec succes!
    echo ========================================
    echo.
    echo Les tables et donnees de reference ont ete creees:
    echo - Table civilites avec 3 valeurs (Monsieur, Madame, Mademoiselle)
    echo - Table pays avec 100+ pays du monde
    echo - Tables adresses et demandes pretes a recevoir des donnees
    echo.
    echo Vous pouvez maintenant redemarrer l'application
    echo et tester le formulaire de nouvelle demande.
    echo.
    echo Pour tester:
    echo 1. Demarrez le backend: cd backend && mvn spring-boot:run
    echo 2. Demarrez le frontend: cd frontend && npm run dev
    echo 3. Ouvrez test_api_demandes.html pour verifier les API
) else (
    echo.
    echo ========================================
    echo   ❌ ERREUR lors de la correction
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