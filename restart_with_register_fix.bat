@echo off
echo ========================================
echo 🔧 Redémarrage Backend avec Corrections
echo ========================================
echo.

echo 🛑 Arrêt des processus Java en cours...
taskkill /f /im java.exe >nul 2>&1
timeout /t 2 >nul

echo.
echo 🧹 Nettoyage du cache Maven...
cd backend
call mvn clean >nul 2>&1

echo.
echo 🔄 Compilation et démarrage du backend...
echo ⏳ Patientez pendant la compilation...
call mvn spring-boot:run

echo.
echo ✅ Backend redémarré avec les corrections !
echo 📋 Testez maintenant l'inscription avec :
echo    - test_register_diagnostic.html
echo    - test_register_fixed.html
echo.
echo 🌐 URL de test : http://localhost:8080/api/auth/register
echo.
pause
