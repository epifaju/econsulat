@echo off
echo Démarrage du backend eConsulat...
echo.

cd backend

echo Nettoyage et compilation...
call mvn clean compile

if %ERRORLEVEL% NEQ 0 (
    echo Erreur lors de la compilation
    pause
    exit /b 1
)

echo.
echo Démarrage du serveur Spring Boot...
echo Le serveur sera accessible sur http://localhost:8080
echo Appuyez sur Ctrl+C pour arrêter le serveur
echo.

call mvn spring-boot:run

pause 