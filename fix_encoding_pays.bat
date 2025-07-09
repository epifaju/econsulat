@echo off
echo ========================================
echo Correction de l'Encodage des Pays
echo ========================================
echo.

echo [1/4] Arret de l'application backend...
taskkill /f /im java.exe 2>nul
timeout /t 3 /nobreak >nul

echo [2/4] Correction de l'encodage dans la base de donnees...
echo Executing fix_encoding_pays.sql...
psql -h localhost -U postgres -d econsulat -f fix_encoding_pays.sql

if %errorlevel% neq 0 (
    echo ❌ Erreur lors de la correction de l'encodage
    echo Verifiez que PostgreSQL est demarre et accessible
    pause
    exit /b 1
)

echo [3/4] Redemarrage de l'application...
cd backend
start /B mvn spring-boot:run
cd ..

echo [4/4] Attente du demarrage de l'application...
timeout /t 10 /nobreak >nul

echo.
echo ✅ Correction de l'encodage terminee!
echo.
echo Pour tester:
echo 1. Ouvrez http://localhost:3000
echo 2. Connectez-vous et allez dans "Nouvelle demande"
echo 3. Verifiez que les pays s'affichent correctement
echo.
echo Ou utilisez la page de test:
echo - Ouvrez test_demandes_api.html
echo - Cliquez sur "Charger les donnees de reference"
echo - Verifiez les pays dans le dropdown
echo.

pause 