@echo off
echo ========================================
echo   Demarrage eConsulat avec correction
echo   Maven et base de donnees
echo ========================================
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

REM Corriger le projet Maven
echo ========================================
echo   Etape 1: Correction du projet Maven
echo ========================================
echo.

call fix_maven_backend.bat

if %errorlevel% neq 0 (
    echo ❌ ERREUR: Impossible de corriger le projet Maven
    echo Arrêt du processus
    pause
    exit /b 1
)

REM Corriger la base de données
echo ========================================
echo   Etape 2: Correction de la base de donnees
echo ========================================
echo.

call "%~dp0fix_demandes_simple.bat"

if %errorlevel% neq 0 (
    echo ❌ ERREUR: Impossible de corriger la base de données
    echo Arrêt du processus
    pause
    exit /b 1
)

echo.
echo ========================================
echo   Etape 3: Demarrage du backend
echo ========================================
echo.

REM Démarrer le backend en arrière-plan
cd backend
start "Backend eConsulat" cmd /k "mvn spring-boot:run"
cd ..

REM Attendre que le backend démarre
echo Attente du démarrage du backend...
timeout /t 20 /nobreak >nul

echo.
echo ========================================
echo   Etape 4: Demarrage du frontend
echo ========================================
echo.

REM Démarrer le frontend en arrière-plan
cd frontend
start "Frontend eConsulat" cmd /k "npm run dev"
cd ..

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
echo.
pause 