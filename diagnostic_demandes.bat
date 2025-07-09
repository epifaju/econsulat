@echo off
echo ========================================
echo   Diagnostic de la base de donnees
echo   des demandes eConsulat
echo ========================================
echo.

REM Vérifier si PostgreSQL est installé
where psql >nul 2>nul
if %errorlevel% neq 0 (
    echo ERREUR: PostgreSQL n'est pas installé ou pas dans le PATH
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
    echo.
    echo Solutions possibles:
    echo 1. Démarrer le service PostgreSQL
    echo 2. Vérifier que l'utilisateur 'postgres' existe
    echo 3. Vérifier le mot de passe
    pause
    exit /b 1
)

echo ✅ Connexion à PostgreSQL réussie
echo.

REM Vérifier si la base de données existe
echo Test de connexion à la base 'econsulat'...
psql -U postgres -d econsulat -c "SELECT 1;" >nul 2>nul
if %errorlevel% neq 0 (
    echo ❌ ERREUR: La base de données 'econsulat' n'existe pas
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

REM Vérifier les tables existantes
echo Vérification des tables...
psql -U postgres -d econsulat -c "SELECT table_name FROM information_schema.tables WHERE table_schema = 'public' AND table_name IN ('civilites', 'pays', 'adresses', 'demandes') ORDER BY table_name;"

echo.
echo Vérification des données dans civilites...
psql -U postgres -d econsulat -c "SELECT COUNT(*) as nombre_civilites FROM civilites;"

echo.
echo Vérification des données dans pays...
psql -U postgres -d econsulat -c "SELECT COUNT(*) as nombre_pays FROM pays;"

echo.
echo ========================================
echo   Test des endpoints API
echo ========================================
echo.

REM Attendre que l'utilisateur démarre le backend
echo Pour tester les endpoints API:
echo 1. Démarrez le backend Spring Boot
echo 2. Ouvrez test_api_demandes.html dans votre navigateur
echo 3. Cliquez sur "Tester tous les endpoints"
echo.

echo ========================================
echo   Diagnostic terminé
echo ========================================
echo.
pause 