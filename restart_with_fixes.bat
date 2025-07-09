@echo off
echo ========================================
echo Redémarrage eConsulat avec corrections
echo ========================================

echo.
echo 1. Arrêt des processus existants...
taskkill /f /im java.exe 2>nul
taskkill /f /im node.exe 2>nul
timeout /t 3 /nobreak >nul

echo.
echo 2. Vérification de la base de données...
setup_admin_interface.bat
if %errorlevel% neq 0 (
    echo ERREUR: Configuration de la base de données échouée
    pause
    exit /b 1
)

echo.
echo 3. Compilation du backend...
cd backend
mvn clean compile
if %errorlevel% neq 0 (
    echo ERREUR: Compilation échouée
    pause
    exit /b 1
)

echo.
echo 4. Démarrage du backend...
start "Backend eConsulat" cmd /k "mvn spring-boot:run"

echo.
echo 5. Attente du démarrage du backend...
timeout /t 15 /nobreak >nul

echo.
echo 6. Démarrage du frontend...
cd ..\frontend
start "Frontend eConsulat" cmd /k "npm run dev"

echo.
echo 7. Attente du démarrage du frontend...
timeout /t 10 /nobreak >nul

echo.
echo ========================================
echo Application redémarrée avec corrections !
echo ========================================
echo.
echo URLs d'accès :
echo - Frontend: http://localhost:5173
echo - Backend: http://localhost:8080
echo - Test Auth: test_admin_auth.html
echo.
echo Comptes de test :
echo - Admin: admin@econsulat.com / admin123
echo - Agent: agent@econsulat.com / agent123
echo.
echo Pour tester l'authentification admin :
echo 1. Ouvrez test_admin_auth.html dans votre navigateur
echo 2. Connectez-vous avec admin@econsulat.com / admin123
echo 3. Testez la génération de document
echo.
pause 