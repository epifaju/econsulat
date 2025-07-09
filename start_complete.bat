@echo off
echo ========================================
echo Démarrage complet eConsulat
echo ========================================

echo.
echo 1. Vérification de la base de données...
setup_admin_interface.bat
if %errorlevel% neq 0 (
    echo ERREUR: Configuration de la base de données échouée
    pause
    exit /b 1
)

echo.
echo 2. Démarrage du backend...
echo Le backend va démarrer sur http://localhost:8080
echo.
cd backend
start "Backend eConsulat" cmd /k "mvn spring-boot:run"

echo.
echo 3. Attente du démarrage du backend...
timeout /t 10 /nobreak >nul

echo.
echo 4. Démarrage du frontend...
echo Le frontend va démarrer sur http://localhost:5173
echo.
cd ..\frontend
start "Frontend eConsulat" cmd /k "npm run dev"

echo.
echo 5. Attente du démarrage du frontend...
timeout /t 15 /nobreak >nul

echo.
echo ========================================
echo Application eConsulat démarrée !
echo ========================================
echo.
echo URLs d'accès :
echo - Frontend: http://localhost:5173
echo - Backend: http://localhost:8080
echo.
echo Comptes de test :
echo - Admin: admin@econsulat.com / admin123
echo - Agent: agent@econsulat.com / agent123
echo - User: user@econsulat.com / user123
echo - Citizen: citizen@econsulat.com / citizen123
echo.
echo Pour arrêter l'application :
echo - Fermez les fenêtres de commande ouvertes
echo - Ou utilisez Ctrl+C dans chaque terminal
echo.
echo Appuyez sur une touche pour ouvrir le navigateur...
pause >nul

start http://localhost:5173

echo.
echo Navigation ouverte dans le navigateur par défaut
echo Connectez-vous avec admin@econsulat.com / admin123
echo.
pause 