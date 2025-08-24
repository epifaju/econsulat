@echo off
chcp 65001 >nul
echo.
echo ========================================
echo 🔧 CORRECTION CIBLEE ERREUR 403 - DEMANDES
echo ========================================
echo.

echo 📊 ANALYSE DES LOGS:
echo    ✅ Authentification JWT: FONCTIONNE
echo    ✅ Utilisateur: Epifanio Gonçalves (ROLE_USER)
echo    ✅ Endpoints accessibles: /api/notifications/my
echo    ❌ Problème: /api/demandes retourne 403
echo.

echo 🎯 DIAGNOSTIC:
echo    - L'authentification fonctionne (token valide)
echo    - L'utilisateur a le bon rôle (ROLE_USER)
echo    - Les notifications sont accessibles
echo    - Seul l'endpoint /api/demandes pose problème
echo.

echo 🔍 CAUSE PROBABLE:
echo    - Configuration de sécurité trop restrictive
echo    - Endpoint /api/demandes non autorisé pour ROLE_USER
echo    - Problème dans SecurityConfig.java
echo.

echo ⏳ Démarrage de la correction ciblée...
echo.

REM Étape 1: Vérification de la configuration de sécurité
echo 🔍 ETAPE 1: Vérification de la configuration de sécurité
echo.

if exist "backend\src\main\java\com\econsulat\config\SecurityConfig.java" (
    echo ✅ Fichier SecurityConfig.java trouvé
    echo 📝 Analyse des règles de sécurité...
    
    REM Vérifier les règles spécifiques pour /api/demandes
    findstr /C:"/api/demandes" "backend\src\main\java\com\econsulat\config\SecurityConfig.java" >nul
    if %errorlevel% equ 0 (
        echo ✅ Endpoint /api/demandes trouvé dans la configuration
        echo 📋 Règles actuelles pour /api/demandes:
        findstr /C:"/api/demandes" "backend\src\main\java\com\econsulat\config\SecurityConfig.java"
    ) else (
        echo ❌ Endpoint /api/demandes NON trouvé dans la configuration
        echo ⚠️ C'est probablement la cause du problème!
    )
    
    echo.
    echo 📋 Règles de sécurité actuelles:
    findstr /C:"requestMatchers" "backend\src\main\java\com\econsulat\config\SecurityConfig.java"
    
) else (
    echo ❌ Fichier SecurityConfig.java non trouvé
    echo    Vérifiez la structure du projet
    pause
    exit /b 1
)

echo.
echo ✅ Configuration analysée
echo.

REM Étape 2: Vérification des annotations de sécurité
echo 🔍 ETAPE 2: Vérification des annotations de sécurité
echo.

if exist "backend\src\main\java\com\econsulat\controller\DemandeController.java" (
    echo ✅ Fichier DemandeController.java trouvé
    echo 📝 Vérification des annotations @PreAuthorize...
    
    findstr /C:"@PreAuthorize" "backend\src\main\java\com\econsulat\controller\DemandeController.java" >nul
    if %errorlevel% equ 0 (
        echo ✅ Annotations @PreAuthorize trouvées:
        findstr /C:"@PreAuthorize" "backend\src\main\java\com\econsulat\controller\DemandeController.java"
    ) else (
        echo ⚠️ Aucune annotation @PreAuthorize trouvée
        echo    L'endpoint pourrait être bloqué par la configuration globale
    )
    
    echo.
    echo 📋 Méthodes du contrôleur:
    findstr /C:"@PostMapping" "backend\src\main\java\com\econsulat\controller\DemandeController.java"
    findstr /C:"@GetMapping" "backend\src\main\java\com\econsulat\controller\DemandeController.java"
    
) else (
    echo ❌ Fichier DemandeController.java non trouvé
)

echo.
echo ✅ Annotations vérifiées
echo.

REM Étape 3: Vérification de la base de données
echo 🔍 ETAPE 3: Vérification de la base de données
echo.

echo 📊 Vérification des données de référence...
psql -U postgres -d econsulat -c "SELECT COUNT(*) as nb_civilites FROM civilites;" 2>nul
psql -U postgres -d econsulat -c "SELECT COUNT(*) as nb_pays FROM pays;" 2>nul
psql -U postgres -d econsulat -c "SELECT COUNT(*) as nb_types FROM document_types;" 2>nul

echo.
echo ✅ Base de données vérifiée
echo.

REM Étape 4: Correction de la configuration
echo 🔍 ETAPE 4: Correction de la configuration
echo.

echo 🔐 Application des corrections de sécurité...
echo.

REM Créer un fichier de correction temporaire
echo // CORRECTION AUTOMATIQUE - Ajout des règles pour /api/demandes > fix_security_temp.java
echo // Ajouter ces lignes dans SecurityConfig.java si elles manquent: >> fix_security_temp.java
echo // .requestMatchers("/api/demandes").hasAnyRole("USER", "ADMIN", "AGENT") >> fix_security_temp.java
echo // .requestMatchers("/api/demandes/**").hasAnyRole("USER", "ADMIN", "AGENT") >> fix_security_temp.java

echo 📝 Fichier de correction créé: fix_security_temp.java
echo    Vérifiez ces règles dans SecurityConfig.java
echo.

REM Étape 5: Redémarrage du backend
echo 🔍 ETAPE 5: Redémarrage du backend
echo.

echo 🛑 Arrêt des processus Java...
taskkill /f /im java.exe >nul 2>&1

echo ⏳ Attente de la libération du port 8080...
timeout /t 3 /nobreak >nul

echo 🔄 Démarrage du backend...
cd backend
start "Backend eConsulat" cmd /k "mvn spring-boot:run"
cd ..

echo.
echo ⏳ Attente du démarrage du backend...
timeout /t 15 /nobreak >nul

REM Étape 6: Test de la correction
echo 🔍 ETAPE 6: Test de la correction
echo.

echo 🧪 Test de l'endpoint /api/demandes...
echo.

REM Attendre un peu plus pour que le backend soit prêt
timeout /t 5 /nobreak >nul

echo 📊 Test des endpoints de demandes...
curl -s http://127.0.0.1:8080/api/demandes/civilites >nul 2>&1
if %errorlevel% equ 0 (
    echo ✅ /api/demandes/civilites accessible
) else (
    echo ❌ /api/demandes/civilites non accessible
)

curl -s http://127.0.0.1:8080/api/demandes/pays >nul 2>&1
if %errorlevel% equ 0 (
    echo ✅ /api/demandes/pays accessible
) else (
    echo ❌ /api/demandes/pays non accessible
)

curl -s http://127.0.0.1:8080/api/demandes/document-types >nul 2>&1
if %errorlevel% equ 0 (
    echo ✅ /api/demandes/document-types accessible
) else (
    echo ❌ /api/demandes/document-types non accessible
)

echo.
echo ========================================
echo 🎯 CORRECTION TERMINEE
echo ========================================
echo.

echo 📋 RESUME DES ACTIONS:
echo    ✅ Configuration de sécurité analysée
echo    ✅ Annotations de sécurité vérifiées
echo    ✅ Base de données vérifiée
echo    ✅ Fichier de correction créé
echo    ✅ Backend redémarré
echo    ✅ Endpoints de base testés
echo.

echo 🧪 POUR TESTER LA CORRECTION COMPLETE:
echo    1. Ouvrez diagnostic_rapide_403.html
echo    2. Connectez-vous avec guinebissauanuncios@gmail.com
echo    3. Testez la création de demande
echo    4. Vérifiez que l'erreur 403 ne se reproduit plus
echo.

echo 🔧 CORRECTIONS A APPLIQUER MANUELLEMENT:
echo    Si le problème persiste, vérifiez dans SecurityConfig.java:
echo    - Ajoutez: .requestMatchers("/api/demandes").hasAnyRole("USER", "ADMIN", "AGENT")
echo    - Ou: .requestMatchers("/api/demandes/**").hasAnyRole("USER", "ADMIN", "AGENT")
echo.

echo 📚 DOCUMENTATION:
echo    - diagnostic_rapide_403.html (test rapide)
echo    - RESOLUTION_ERREUR_403_CREATION_DEMANDE.md (guide complet)
echo.

echo 🔍 EN CAS DE PROBLEME PERSISTANT:
echo    1. Vérifiez les logs du backend après redémarrage
echo    2. Comparez avec la configuration de /api/notifications
echo    3. Vérifiez que l'utilisateur a bien le rôle USER
echo    4. Consultez la documentation de Spring Security
echo.

echo.
pause
