@echo off
echo ========================================
echo Diagnostic Erreur 403 - eConsulat
echo ========================================
echo.

echo [1/4] Verification de la base de donnees...
echo.

echo -- Structure de la table users --
psql -h localhost -p 5432 -U postgres -d econsulat -c "\d users" 2>nul
if %errorlevel% neq 0 (
    echo ❌ Impossible de se connecter a la base de donnees
    echo    Verifiez que PostgreSQL est demarre et accessible
    pause
    exit /b 1
)

echo.
echo -- Contenu de la table users --
psql -h localhost -p 5432 -U postgres -d econsulat -c "SELECT id, email, role, enabled FROM users ORDER BY id;" 2>nul

echo.
echo [2/4] Verification des roles et permissions...
echo.

echo -- Roles disponibles --
psql -h localhost -p 5432 -U postgres -d econsulat -c "SELECT DISTINCT role FROM users;" 2>nul

echo.
echo [3/4] Test de connexion au backend...
echo.

echo -- Test endpoint public --
curl -s -o nul -w "Status: %%{http_code}\n" http://127.0.0.1:8080/api/demandes/document-types
if %errorlevel% neq 0 (
    echo ❌ Backend non accessible
    echo    Verifiez que le backend Spring Boot est demarre sur le port 8080
) else (
    echo ✅ Backend accessible
)

echo.
echo [4/4] Creation d'un utilisateur de test si necessaire...
echo.

echo -- Verification si user@test.com existe --
psql -h localhost -p 5432 -U postgres -d econsulat -c "SELECT COUNT(*) as count FROM users WHERE email = 'user@test.com';" 2>nul

echo.
echo ========================================
echo Diagnostic termine
echo ========================================
echo.
echo Prochaines etapes:
echo 1. Ouvrir test_auth_diagnostic.html dans votre navigateur
echo 2. Tester la connexion au backend
echo 3. Tester l'authentification avec un utilisateur existant
echo 4. Analyser les logs du backend pour identifier le probleme
echo.
echo Si aucun utilisateur n'existe, créez-en un avec:
echo   create_admin.bat
echo.
pause

