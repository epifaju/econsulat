@echo off
echo ========================================
echo Test du Rafraichissement des Demandes
echo ========================================
echo.

echo [1/4] Verification de l'application...
echo - Backend: http://localhost:8080
echo - Frontend: http://localhost:3000
echo.

echo [2/4] Test de l'API des demandes...
curl -s -o nul -w "Status: %%{http_code}\n" http://localhost:8080/api/demandes/my
if %errorlevel% neq 0 (
    echo ❌ L'API des demandes n'est pas accessible
    echo    Verifiez que le backend est demarre
    goto :end
)

echo [3/4] Test des donnees de reference...
curl -s -o nul -w "Civilités: %%{http_code}\n" http://localhost:8080/api/civilites
curl -s -o nul -w "Pays: %%{http_code}\n" http://localhost:8080/api/pays
curl -s -o nul -w "Types de documents: %%{http_code}\n" http://localhost:8080/api/document-types

echo.
echo [4/4] Instructions de test manuel:
echo.
echo 1. Ouvrez http://localhost:3000 dans votre navigateur
echo 2. Connectez-vous avec:
echo    - Email: citizen@econsulat.com
echo    - Mot de passe: citizen123
echo 3. Cliquez sur "Nouvelle demande"
echo 4. Remplissez le formulaire et soumettez
echo 5. Verifiez que la demande apparaît dans la liste
echo.
echo Ou utilisez la page de test:
echo - Ouvrez test_demandes_api.html dans votre navigateur
echo.

:end
echo.
echo Test termine.
pause 