@echo off
echo ========================================
echo 🔧 Redémarrage du Backend avec Correction CORS
echo ========================================

echo.
echo 🛑 Arrêt des processus Java existants...
taskkill /f /im java.exe 2>nul
timeout /t 2 /nobreak >nul

echo.
echo 🧹 Nettoyage du cache Maven...
call clean_maven_cache.bat

echo.
echo 🚀 Démarrage du backend avec correction CORS...
cd backend
call mvn spring-boot:run

echo.
echo ✅ Backend redémarré avec la correction CORS
echo 🌐 Configuration CORS mise à jour pour autoriser localhost:5173 et 127.0.0.1:5173
echo.
echo 📋 Pour tester la création de demande :
echo 1. Ouvrir test_creation_demande_diagnostic.html dans le navigateur
echo 2. Tester la connexion au backend
echo 3. S'authentifier avec admin@econsulat.com / admin123
echo 4. Tester la création de demande
echo.
pause
