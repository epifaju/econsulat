@echo off
echo ========================================
echo Test Génération de Documents eConsulat
echo ========================================

echo.
echo 1. Vérification du backend...
curl -s http://localhost:8080/api/auth/test
if %errorlevel% neq 0 (
    echo ERREUR: Backend non accessible
    echo Démarrez le backend avec: cd backend && mvn spring-boot:run
    pause
    exit /b 1
)

echo.
echo 2. Test d'authentification admin...
curl -X POST http://localhost:8080/api/auth/login ^
  -H "Content-Type: application/json" ^
  -d "{\"email\":\"admin@econsulat.com\",\"password\":\"admin123\"}" ^
  -o temp_token.json

if %errorlevel% neq 0 (
    echo ERREUR: Impossible de se connecter
    pause
    exit /b 1
)

echo.
echo 3. Extraction du token...
for /f "tokens=2 delims=:," %%a in ('findstr "token" temp_token.json') do (
    set TOKEN=%%a
    set TOKEN=!TOKEN:"=!
    set TOKEN=!TOKEN: =!
)

echo Token extrait: !TOKEN:~0,50!...

echo.
echo 4. Test de l'endpoint de génération de documents...
curl -X POST "http://localhost:8080/api/admin/documents/generate?demandeId=1&documentTypeId=1" ^
  -H "Authorization: Bearer !TOKEN!" ^
  -H "Content-Type: application/json" ^
  -v

echo.
echo 5. Nettoyage...
del temp_token.json 2>nul

echo.
echo ========================================
echo Test terminé
echo ========================================
echo.
echo Si vous voyez une erreur 403, le problème persiste
echo Si vous voyez une réponse 200, le problème est résolu
echo.
pause 