@echo off
echo ========================================
echo   Vérification et Démarrage Backend
echo ========================================
echo.

echo [1/6] Vérification de Java...
java -version >nul 2>&1
if errorlevel 1 (
    echo ❌ Java n'est pas installé ou non accessible
    echo Installez Java 17 ou plus récent
    pause
    exit /b 1
)
echo ✅ Java détecté

echo.
echo [2/6] Vérification de Maven...
mvn -version >nul 2>&1
if errorlevel 1 (
    echo ❌ Maven n'est pas installé ou non accessible
    echo Installez Apache Maven
    pause
    exit /b 1
)
echo ✅ Maven détecté

echo.
echo [3/6] Vérification du port 8080...
netstat -an | findstr :8080 >nul 2>&1
if not errorlevel 1 (
    echo ⚠️ Le port 8080 est déjà utilisé
    echo Voulez-vous arrêter le processus existant? (O/N)
    set /p choice=
    if /i "%choice%"=="O" (
        echo Arrêt des processus sur le port 8080...
        for /f "tokens=5" %%a in ('netstat -aon ^| findstr :8080') do (
            taskkill /f /pid %%a >nul 2>&1
        )
        echo ✅ Processus arrêtés
    ) else (
        echo ❌ Impossible de continuer, port 8080 occupé
        pause
        exit /b 1
    )
) else (
    echo ✅ Port 8080 libre
)

echo.
echo [4/6] Vérification de la base de données...
echo Vérifiez que PostgreSQL est démarré et accessible
echo Appuyez sur une touche pour continuer...
pause >nul

echo.
echo [5/6] Compilation du projet...
cd backend
call mvn clean compile
if errorlevel 1 (
    echo ❌ Erreur de compilation
    echo Vérifiez les erreurs ci-dessus
    pause
    exit /b 1
)
echo ✅ Compilation réussie

echo.
echo [6/6] Démarrage du backend...
echo Le backend va démarrer sur http://localhost:8080
echo Appuyez sur Ctrl+C pour arrêter
echo.
start "eConsulat Backend" cmd /k "mvn spring-boot:run"

echo.
echo ========================================
echo   Backend en cours de démarrage...
echo ========================================
echo.
echo Attendez quelques secondes que le serveur démarre
echo Puis ouvrez test_backend_connection.html pour tester
echo.
echo URLs utiles:
echo - Health check: http://localhost:8080/actuator/health
echo - API docs: http://localhost:8080/swagger-ui.html
echo - Test connexion: test_backend_connection.html
echo.
echo Appuyez sur une touche pour continuer...
pause >nul 