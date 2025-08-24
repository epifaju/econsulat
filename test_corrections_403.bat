@echo off
echo ========================================
echo 🔧 Test des Corrections Erreur 403
echo ========================================
echo.

echo 📋 Résumé des corrections implémentées:
echo ✅ GlobalExceptionHandler.java - Gestion centralisée des exceptions
echo ✅ DemandeService.java - Validation préalable des données
echo ✅ DemandeController.java - Gestion d'erreurs améliorée
echo ✅ NewDemandeForm.jsx - Validation frontend + gestion d'erreurs
echo.

echo 🚀 Démarrage du backend Spring Boot...
echo.

cd backend
start "Backend eConsulat" cmd /k "mvn spring-boot:run"

echo.
echo ⏳ Attendre que le backend démarre (environ 30 secondes)...
echo.

timeout /t 30 /nobreak >nul

echo.
echo 🌐 Ouverture de la page de test...
echo.

start "" "test_corrections_403.html"

echo.
echo ✅ Tests prêts!
echo.
echo 📝 Instructions:
echo 1. Attendre que le backend soit complètement démarré
echo 2. Utiliser la page de test pour valider les corrections
echo 3. Vérifier que les erreurs 403 sont remplacées par des codes 400
echo.
echo 🔍 Fichiers modifiés:
echo - backend/src/main/java/com/econsulat/exception/GlobalExceptionHandler.java
echo - backend/src/main/java/com/econsulat/service/DemandeService.java
echo - backend/src/main/java/com/econsulat/controller/DemandeController.java
echo - frontend/src/components/NewDemandeForm.jsx
echo.

pause
