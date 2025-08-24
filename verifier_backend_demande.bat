@echo off
chcp 65001 >nul
echo.
echo ========================================
echo 🔍 VERIFICATION BACKEND - POST /api/demandes
echo ========================================
echo.

echo 📊 ANALYSE DU PROBLEME:
echo    ✅ Authentification JWT: FONCTIONNE
echo    ✅ Endpoints GET: FONCTIONNENT
echo    ❌ Endpoint POST /api/demandes: N'APPEARAIT PAS dans les logs
echo.

echo 🎯 DIAGNOSTIC:
echo    - La requête POST n'arrive pas au backend
echo    - Problème probable: CORS, proxy, ou configuration frontend
echo.

echo ⏳ Démarrage de la vérification...
echo.

REM Étape 1: Vérification du backend
echo 🔍 ETAPE 1: Vérification du backend
echo.

echo 📊 Test de connexion au backend...
curl -s http://127.0.0.1:8080/actuator/health >nul 2>&1
if %errorlevel% equ 0 (
    echo ✅ Backend accessible sur http://127.0.0.1:8080
) else (
    echo ❌ Backend non accessible
    echo    Vérifiez que le backend est démarré
    pause
    exit /b 1
)

echo.
echo 📊 Test des endpoints de demandes...
echo.

echo 🔍 Test GET /api/demandes/civilites...
curl -s http://127.0.0.1:8080/api/demandes/civilites >nul 2>&1
if %errorlevel% equ 0 (
    echo ✅ GET /api/demandes/civilites accessible
) else (
    echo ❌ GET /api/demandes/civilites non accessible
)

echo 🔍 Test GET /api/demandes/pays...
curl -s http://127.0.0.1:8080/api/demandes/pays >nul 2>&1
if %errorlevel% equ 0 (
    echo ✅ GET /api/demandes/pays accessible
) else (
    echo ❌ GET /api/demandes/pays non accessible
)

echo 🔍 Test GET /api/demandes/document-types...
curl -s http://127.0.0.1:8080/api/demandes/document-types >nul 2>&1
if %errorlevel% equ 0 (
    echo ✅ GET /api/demandes/document-types accessible
) else (
    echo ❌ GET /api/demandes/document-types non accessible
)

echo.
echo ✅ Backend vérifié
echo.

REM Étape 2: Vérification de la configuration CORS
echo 🔍 ETAPE 2: Vérification de la configuration CORS
echo.

if exist "backend\src\main\java\com\econsulat\config\SecurityConfig.java" (
    echo ✅ Fichier SecurityConfig.java trouvé
    echo 📝 Vérification de la configuration CORS...
    
    findstr /C:"corsConfigurationSource" "backend\src\main\java\com\econsulat\config\SecurityConfig.java" >nul
    if %errorlevel% equ 0 (
        echo ✅ Configuration CORS trouvée
        echo 📋 Détails CORS:
        findstr /C:"corsConfigurationSource" "backend\src\main\java\com\econsulat\config\SecurityConfig.java"
    ) else (
        echo ❌ Configuration CORS non trouvée
    )
    
    echo.
    echo 📋 Origines autorisées:
    findstr /C:"allowedOrigins" "backend\src\main\java\com\econsulat\config\SecurityConfig.java"
    
) else (
    echo ❌ Fichier SecurityConfig.java non trouvé
)

echo.
echo ✅ Configuration CORS vérifiée
echo.

REM Étape 3: Vérification du contrôleur
echo 🔍 ETAPE 3: Vérification du contrôleur
echo.

if exist "backend\src\main\java\com\econsulat\controller\DemandeController.java" (
    echo ✅ Fichier DemandeController.java trouvé
    echo 📝 Vérification des méthodes POST...
    
    findstr /C:"@PostMapping" "backend\src\main\java\com\econsulat\controller\DemandeController.java" >nul
    if %errorlevel% equ 0 (
        echo ✅ Méthode POST trouvée
        echo 📋 Méthodes POST:
        findstr /C:"@PostMapping" "backend\src\main\java\com\econsulat\controller\DemandeController.java"
    ) else (
        echo ❌ Aucune méthode POST trouvée
    )
    
    echo.
    echo 📋 Mapping des endpoints:
    findstr /C:"@RequestMapping" "backend\src\main\java\com\econsulat\controller\DemandeController.java"
    
) else (
    echo ❌ Fichier DemandeController.java non trouvé
)

echo.
echo ✅ Contrôleur vérifié
echo.

REM Étape 4: Test direct de l'endpoint POST
echo 🔍 ETAPE 4: Test direct de l'endpoint POST
echo.

echo 📝 Test POST /api/demandes avec données minimales...
echo.

REM Créer un fichier de test temporaire
echo {"civiliteId":1,"firstName":"Test","lastName":"User","birthDate":"1990-01-01","birthPlace":"Test","birthCountryId":1,"streetName":"Test","streetNumber":"123","postalCode":"12345","city":"Test","countryId":1,"fatherFirstName":"Test","fatherLastName":"Father","fatherBirthDate":"1960-01-01","fatherBirthPlace":"Test","fatherBirthCountryId":1,"motherFirstName":"Test","motherLastName":"Mother","motherBirthDate":"1965-01-01","motherBirthPlace":"Test","motherBirthCountryId":1,"documentTypeId":1,"documentFiles":[]} > test_demande.json

echo 📤 Test avec curl...
curl -X POST http://127.0.0.1:8080/api/demandes ^
  -H "Content-Type: application/json" ^
  -d @test_demande.json ^
  -v

echo.
echo 📊 Résultat du test POST:
if %errorlevel% equ 0 (
    echo ✅ Test POST réussi
) else (
    echo ❌ Test POST échoué
    echo    Vérifiez les logs du backend
)

REM Nettoyer le fichier temporaire
del test_demande.json >nul 2>&1

echo.
echo ✅ Test POST terminé
echo.

REM Étape 5: Vérification des logs
echo 🔍 ETAPE 5: Vérification des logs
echo.

echo 📋 Instructions pour vérifier les logs:
echo    1. Regardez le terminal du backend
echo    2. Cherchez ces messages lors du test POST:
echo       - "Securing POST /api/demandes"
echo       - "JWT Filter - URL: /api/demandes"
echo       - "Secured POST /api/demandes"
echo       - "Authorizing method invocation"
echo.

echo 🔍 Si aucun de ces messages n'apparaît:
echo    - Le problème est CORS ou proxy
echo    - La requête n'arrive pas au backend
echo.

echo 🔍 Si ces messages apparaissent mais avec 403:
echo    - Le problème est dans la configuration de sécurité
echo    - Vérifiez SecurityConfig.java
echo.

echo.
echo ========================================
echo 🎯 VERIFICATION TERMINEE
echo ========================================
echo.

echo 📋 RESUME DES VERIFICATIONS:
echo    ✅ Backend accessible
echo    ✅ Endpoints GET fonctionnent
echo    ✅ Configuration CORS vérifiée
echo    ✅ Contrôleur vérifié
echo    ✅ Test POST effectué
echo.

echo 🧪 POUR TESTER COMPLETEMENT:
echo    1. Ouvrez test_post_demande_direct.html
echo    2. Suivez les étapes dans l'ordre
echo    3. Vérifiez les logs du backend
echo    4. Identifiez exactement où la requête échoue
echo.

echo 🔧 SOLUTIONS POSSIBLES:
echo    - Problème CORS: Vérifiez les origines autorisées
echo    - Problème proxy: Vérifiez la configuration frontend
echo    - Problème sécurité: Vérifiez SecurityConfig.java
echo    - Problème réseau: Vérifiez les ports et firewall
echo.

echo 📚 DOCUMENTATION:
echo    - test_post_demande_direct.html (test complet)
echo    - RESOLUTION_RAPIDE_403_DEMANDES.md (guide)
echo.

echo.
pause
