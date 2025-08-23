@echo off
echo ========================================
echo 🔄 Redémarrage Backend avec Corrections
echo ========================================
echo.

echo 🛑 Arrêt des processus Java existants...
taskkill /f /im java.exe 2>nul
timeout /t 2 /nobreak >nul

echo.
echo 🧹 Nettoyage du cache Maven...
cd backend
call mvn clean -q

echo.
echo 🔨 Compilation du projet...
call mvn compile -q

echo.
echo 🚀 Démarrage du backend...
start "Backend eConsulat" cmd /k "mvn spring-boot:run"

echo.
echo ⏳ Attente du démarrage...
timeout /t 10 /nobreak >nul

echo.
echo 🔍 Vérification du statut...
curl -s http://127.0.0.1:8080/api/demandes/document-types >nul
if %errorlevel% equ 0 (
    echo ✅ Backend démarré avec succès sur le port 8080
    echo.
    echo 📋 Endpoints disponibles:
    echo    - http://127.0.0.1:8080/api/demandes/*/status (ADMIN/AGENT)
    echo    - http://127.0.0.1:8080/api/auth/me (Test auth)
    echo.
    echo 🧪 Testez maintenant avec le fichier test_status_update_debug.html
) else (
    echo ❌ Erreur lors du démarrage du backend
    echo 🔍 Vérifiez les logs dans la console Maven
)

echo.
echo ========================================
pause
