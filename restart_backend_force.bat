@echo off
echo ========================================
echo Redémarrage Forcé du Backend eConsulat
echo ========================================
echo.

echo 1. Arrêt forcé de tous les processus Java...
taskkill /f /im java.exe 2>nul
echo    ✅ Processus Java arrêtés

echo.
echo 2. Attente de 3 secondes pour libérer le port 8080...
timeout /t 3 /nobreak >nul

echo.
echo 3. Vérification que le port 8080 est libre...
netstat -an | findstr :8080
if %errorlevel% == 0 (
    echo    ⚠️  Le port 8080 est encore utilisé
    echo    🔄 Tentative d'arrêt supplémentaire...
    taskkill /f /im java.exe 2>nul
    timeout /t 2 /nobreak >nul
) else (
    echo    ✅ Port 8080 libre
)

echo.
echo 4. Nettoyage du cache Maven...
cd backend
call mvn clean
echo    ✅ Cache Maven nettoyé

echo.
echo 5. Démarrage du backend avec les nouvelles configurations...
echo    🔧 Les nouvelles règles de sécurité seront appliquées
echo    📝 Endpoint /api/demandes configuré avec .authenticated()
echo.
call mvn spring-boot:run

echo.
echo 6. Le backend démarre sur http://localhost:8080
echo 7. Ouvrez test_endpoint_demandes.html dans votre navigateur
echo 8. Testez l'authentification puis la création de demande
echo.
echo Appuyez sur une touche pour arrêter le backend...
pause >nul
taskkill /f /im java.exe 2>nul
