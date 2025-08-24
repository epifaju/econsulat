@echo off
echo ========================================
echo Test de correction de l'erreur 403
echo ========================================
echo.

echo 1. Arrêt du backend s'il est en cours...
taskkill /f /im java.exe 2>nul
timeout /t 2 /nobreak >nul

echo 2. Démarrage du backend avec les corrections...
cd backend
call mvn spring-boot:run
echo.

echo 3. Le backend démarre sur http://localhost:8080
echo 4. Ouvrez diagnostic_403_complet.html dans votre navigateur
echo 5. Testez l'authentification puis la création de demande
echo.
echo Appuyez sur une touche pour arrêter le backend...
pause >nul
taskkill /f /im java.exe 2>nul
