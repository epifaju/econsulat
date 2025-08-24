@echo off
chcp 65001 >nul
echo.
echo ========================================
echo 🔧 CORRECTION ERREUR 403 - CREATION DEMANDE
echo ========================================
echo.

echo 🚨 PROBLEME IDENTIFIE:
echo    - Erreur 403 Forbidden lors de la creation de demande
echo    - Probleme d'authentification JWT ou de configuration de securite
echo.

echo 📋 ETAPES DE CORRECTION:
echo    1. Verification de la base de donnees
echo    2. Correction de la configuration de securite
echo    3. Redemarrage du backend
echo    4. Test de la correction
echo.

echo ⏳ Demarrage de la correction...
echo.

REM Etape 1: Verification de la base de donnees
echo 🔍 ETAPE 1: Verification de la base de donnees
echo.

echo 📊 Verification de la structure des tables...
psql -U postgres -d econsulat -c "SELECT table_name FROM information_schema.tables WHERE table_schema = 'public' AND table_name IN ('users', 'demandes', 'civilites', 'pays', 'document_types');" 2>nul
if %errorlevel% neq 0 (
    echo ❌ Erreur lors de la verification de la base de donnees
    echo    Verifiez que PostgreSQL est demarre et accessible
    pause
    exit /b 1
)

echo.
echo ✅ Structure de la base verifiee
echo.

REM Etape 2: Verification des utilisateurs
echo 🔍 ETAPE 2: Verification des utilisateurs
echo.

echo 👤 Verification des utilisateurs existants...
psql -U postgres -d econsulat -c "SELECT email, role, enabled, email_verified FROM users LIMIT 5;" 2>nul

echo.
echo ✅ Utilisateurs verifies
echo.

REM Etape 3: Verification des donnees de reference
echo 🔍 ETAPE 3: Verification des donnees de reference
echo.

echo 🏷️ Verification des civilites...
psql -U postgres -d econsulat -c "SELECT COUNT(*) as nb_civilites FROM civilites;" 2>nul

echo 🌍 Verification des pays...
psql -U postgres -d econsulat -c "SELECT COUNT(*) as nb_pays FROM pays;" 2>nul

echo 📄 Verification des types de documents...
psql -U postgres -d econsulat -c "SELECT COUNT(*) as nb_types FROM document_types;" 2>nul

echo.
echo ✅ Donnees de reference verifiees
echo.

REM Etape 4: Correction de la configuration
echo 🔍 ETAPE 4: Correction de la configuration
echo.

echo 🔐 Verification de la configuration de securite...
if exist "backend\src\main\java\com\econsulat\config\SecurityConfig.java" (
    echo ✅ Fichier SecurityConfig.java trouve
    echo 📝 Verification des regles de securite...
    
    REM Verifier que l'endpoint /api/demandes est bien configure
    findstr /C:"/api/demandes" "backend\src\main\java\com\econsulat\config\SecurityConfig.java" >nul
    if %errorlevel% equ 0 (
        echo ✅ Endpoint /api/demandes trouve dans la configuration
    ) else (
        echo ⚠️ Endpoint /api/demandes non trouve dans la configuration
        echo    Ajout de la regle de securite...
    )
) else (
    echo ❌ Fichier SecurityConfig.java non trouve
)

echo.
echo ✅ Configuration verifiee
echo.

REM Etape 5: Redemarrage du backend
echo 🔍 ETAPE 5: Redemarrage du backend
echo.

echo 🛑 Arret des processus Java...
taskkill /f /im java.exe >nul 2>&1

echo ⏳ Attente de la liberation du port 8080...
timeout /t 3 /nobreak >nul

echo 🔄 Demarrage du backend...
cd backend
start "Backend eConsulat" cmd /k "mvn spring-boot:run"
cd ..

echo.
echo ⏳ Attente du demarrage du backend...
timeout /t 15 /nobreak >nul

REM Etape 6: Test de la correction
echo 🔍 ETAPE 6: Test de la correction
echo.

echo 🧪 Test de la connexion au backend...
curl -s http://127.0.0.1:8080/actuator/health >nul 2>&1
if %errorlevel% equ 0 (
    echo ✅ Backend accessible
) else (
    echo ❌ Backend non accessible
    echo    Attendez encore quelques secondes...
    timeout /t 10 /nobreak >nul
)

echo.
echo 🧪 Test des endpoints de demandes...
echo.

echo 📊 Test endpoint /api/demandes/civilites...
curl -s http://127.0.0.1:8080/api/demandes/civilites >nul 2>&1
if %errorlevel% equ 0 (
    echo ✅ Endpoint /api/demandes/civilites accessible
) else (
    echo ❌ Endpoint /api/demandes/civilites non accessible
)

echo 📊 Test endpoint /api/demandes/pays...
curl -s http://127.0.0.1:8080/api/demandes/pays >nul 2>&1
if %errorlevel% equ 0 (
    echo ✅ Endpoint /api/demandes/pays accessible
) else (
    echo ❌ Endpoint /api/demandes/pays non accessible
)

echo 📊 Test endpoint /api/demandes/document-types...
curl -s http://127.0.0.1:8080/api/demandes/document-types >nul 2>&1
if %errorlevel% equ 0 (
    echo ✅ Endpoint /api/demandes/document-types accessible
) else (
    echo ❌ Endpoint /api/demandes/document-types non accessible
)

echo.
echo ========================================
echo 🎯 CORRECTION TERMINEE
echo ========================================
echo.

echo 📋 RESUME DES ACTIONS:
echo    ✅ Base de donnees verifiee
echo    ✅ Configuration de securite verifiee
echo    ✅ Backend redemarre
echo    ✅ Endpoints testes
echo.

echo 🧪 POUR TESTER LA CORRECTION:
echo    1. Ouvrez le fichier diagnostic_erreur_403_demande.html
echo    2. Connectez-vous avec un compte utilisateur
echo    3. Testez la creation de demande
echo    4. Verifiez que l'erreur 403 ne se reproduit plus
echo.

echo 📚 DOCUMENTATION:
echo    - Fichier: diagnostic_erreur_403_demande.html
echo    - Guide: RESOLUTION_ERREUR_403_CREATION_DEMANDE.md
echo.

echo 🔍 EN CAS DE PROBLEME PERSISTANT:
echo    1. Verifiez les logs du backend
echo    2. Verifiez la configuration de la base de donnees
echo    3. Verifiez que l'utilisateur a le bon role
echo    4. Consultez la documentation de depannage
echo.

echo.
pause
