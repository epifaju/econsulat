@echo off
echo ========================================
echo   Demarrage propre eConsulat
echo   avec arrêt des processus existants
echo ========================================
echo.

REM Obtenir le répertoire courant
set CURRENT_DIR=%~dp0
echo Répertoire courant: %CURRENT_DIR%
echo.

REM Vérifier si Java est installé
where java >nul 2>nul
if %errorlevel% neq 0 (
    echo ❌ ERREUR: Java n'est pas installé ou pas dans le PATH
    echo Veuillez installer Java 17+ et réessayer
    pause
    exit /b 1
)

REM Vérifier si Maven est installé
where mvn >nul 2>nul
if %errorlevel% neq 0 (
    echo ❌ ERREUR: Maven n'est pas installé ou pas dans le PATH
    echo Veuillez installer Maven et réessayer
    pause
    exit /b 1
)

REM Vérifier si Node.js est installé
where node >nul 2>nul
if %errorlevel% neq 0 (
    echo ❌ ERREUR: Node.js n'est pas installé ou pas dans le PATH
    echo Veuillez installer Node.js et réessayer
    pause
    exit /b 1
)

echo ✅ Vérification des prérequis...
echo - Java: OK
echo - Maven: OK
echo - Node.js: OK
echo.

REM Arrêter les processus existants
echo ========================================
echo   Etape 1: Arrêt des processus existants
echo ========================================
echo.

call "%CURRENT_DIR%stop_java_processes.bat"

if %errorlevel% neq 0 (
    echo ⚠️ ATTENTION: Impossible d'arrêter tous les processus
    echo Le démarrage peut échouer si le port 8080 est utilisé
    echo.
    set /p continue="Voulez-vous continuer? (o/n): "
    if /i not "!continue!"=="o" (
        echo Arrêt du processus
        pause
        exit /b 1
    )
)

REM Corriger le projet Maven
echo ========================================
echo   Etape 2: Correction du projet Maven
echo ========================================
echo.

call "%CURRENT_DIR%fix_maven_backend.bat"

if %errorlevel% neq 0 (
    echo ❌ ERREUR: Impossible de corriger le projet Maven
    echo Arrêt du processus
    pause
    exit /b 1
)

REM Corriger la base de données
echo ========================================
echo   Etape 3: Correction de la base de donnees
echo ========================================
echo.

call "%CURRENT_DIR%fix_demandes_simple.bat"

if %errorlevel% neq 0 (
    echo ❌ ERREUR: Impossible de corriger la base de données
    echo Arrêt du processus
    pause
    exit /b 1
)

echo.
echo ========================================
echo   Etape 4: Demarrage du backend
echo ========================================
echo.

REM Vérifier que le port 8080 est libre
netstat -aon | findstr :8080 >nul 2>&1
if %errorlevel% equ 0 (
    echo ❌ ERREUR: Le port 8080 est encore utilisé
    echo Veuillez redémarrer l'ordinateur ou arrêter manuellement les processus
    pause
    exit /b 1
)

echo ✅ Le port 8080 est libre
echo Démarrage du backend...

REM Démarrer le backend en arrière-plan
cd "%CURRENT_DIR%backend"
start "Backend eConsulat" cmd /k "mvn spring-boot:run"
cd "%CURRENT_DIR%"

REM Attendre que le backend démarre
echo Attente du démarrage du backend...
timeout /t 25 /nobreak >nul

echo.
echo ========================================
echo   Etape 5: Demarrage du frontend
echo ========================================
echo.

REM Démarrer le frontend en arrière-plan
cd "%CURRENT_DIR%frontend"
start "Frontend eConsulat" cmd /k "npm run dev"
cd "%CURRENT_DIR%"

echo.
echo ========================================
echo   ✅ Application demarree avec succes!
echo ========================================
echo.
echo URLs d'acces:
echo - Frontend: http://localhost:5173
echo - Backend:  http://localhost:8080
echo - Test API: test_api_demandes.html
echo.
echo Comptes de test:
echo - Admin: admin@econsulat.com / admin123
echo - User:  user@econsulat.com / user123
echo - Citizen: citizen@econsulat.com / citizen123
echo.
echo Pour tester les demandes:
echo 1. Connectez-vous avec un compte 'user'
echo 2. Cliquez sur "Nouvelle demande"
echo 3. Remplissez le formulaire en 6 etapes
echo 4. Soumettez la demande
echo.
echo Pour verifier que tout fonctionne:
echo 1. Ouvrez test_api_demandes.html dans votre navigateur
echo 2. Cliquez sur "Tester tous les endpoints"
echo 3. Vérifiez que toutes les listes sont remplies
echo.
echo Pour arreter l'application:
echo - Fermez les fenetres de commande ouvertes
echo - Ou utilisez Ctrl+C dans chaque fenetre
echo - Ou exécutez stop_java_processes.bat
echo.
pause 