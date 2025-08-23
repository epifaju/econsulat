@echo off
echo ========================================
echo   eConsulat - Systeme de Notifications
echo ========================================
echo.

echo [1/4] Verification de la configuration...
if not exist "backend\src\main\resources\schema_notifications.sql" (
    echo ❌ Fichier schema_notifications.sql manquant
    echo    Creez d'abord la table des notifications
    pause
    exit /b 1
)

if not exist "backend\src\main\java\com\econsulat\model\Notification.java" (
    echo ❌ Modele Notification.java manquant
    echo    Verifiez que tous les fichiers ont ete crees
    pause
    exit /b 1
)

echo ✅ Configuration verifiee
echo.

echo [2/4] Demarrage du backend...
cd backend
start "Backend eConsulat" cmd /k "mvn spring-boot:run"
cd ..
echo ✅ Backend demarre
echo.

echo [3/4] Attente du demarrage du backend...
timeout /t 15 /nobreak >nul
echo ✅ Backend pret
echo.

echo [4/4] Demarrage du frontend...
cd frontend
start "Frontend eConsulat" cmd /k "npm run dev"
cd ..
echo ✅ Frontend demarre
echo.

echo ========================================
echo   🎉 Systeme de notifications demarre !
echo ========================================
echo.
echo 📋 Prochaines etapes:
echo    1. Ouvrir http://localhost:5173 dans votre navigateur
echo    2. Vous connecter en tant qu'admin
echo    3. Tester la modification du statut d'une demande
echo    4. Verifier l'envoi automatique d'emails
echo    5. Consulter les notifications dans le tableau de bord
echo.
echo 🧪 Pour tester les API:
echo    Ouvrir test_notifications.html dans votre navigateur
echo.
echo 📚 Documentation:
echo    Consulter GUIDE_NOTIFICATIONS.md
echo.
echo ⚠️  Important: Configurez votre SMTP dans application.properties
echo.
pause
