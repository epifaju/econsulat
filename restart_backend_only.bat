@echo off
echo ========================================
echo Redémarrage Backend eConsulat
echo ========================================

echo.
echo 1. Arrêt des processus Java...
taskkill /f /im java.exe 2>nul
timeout /t 3 /nobreak >nul

echo.
echo 2. Compilation du backend...
mvn clean compile
if %errorlevel% neq 0 (
    echo ERREUR: Compilation échouée
    pause
    exit /b 1
)

echo.
echo 3. Démarrage du backend...
echo Le backend va démarrer sur http://localhost:8080
echo Appuyez sur Ctrl+C pour arrêter
echo.
mvn spring-boot:run

pause 