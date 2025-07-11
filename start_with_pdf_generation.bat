@echo off
echo ========================================
echo   eConsulat - Demarrage avec PDF Generation
echo ========================================
echo.

echo [1/4] Verification de Java...
java -version >nul 2>&1
if errorlevel 1 (
    echo ERREUR: Java n'est pas installe ou non accessible
    pause
    exit /b 1
)
echo ✓ Java detecte

echo.
echo [2/4] Nettoyage du cache Maven...
cd backend
call mvn clean >nul 2>&1
echo ✓ Cache Maven nettoye

echo.
echo [3/4] Compilation avec nouvelles dependances...
call mvn compile
if errorlevel 1 (
    echo ERREUR: Compilation echouee
    echo Verifiez les dependances iText 7 dans pom.xml
    pause
    exit /b 1
)
echo ✓ Compilation reussie

echo.
echo [4/4] Demarrage du backend...
start "eConsulat Backend" cmd /k "mvn spring-boot:run"

echo.
echo ========================================
echo   Backend demarre sur http://localhost:8080
echo ========================================
echo.
echo Fonctionnalites disponibles:
echo - Generation de documents Word
echo - Generation de documents PDF avec filigrane
echo - Interface d'administration
echo.
echo Pour tester la generation PDF:
echo 1. Ouvrez test_pdf_generation.html
echo 2. Connectez-vous en tant qu'admin
echo 3. Testez la generation de documents
echo.
echo Appuyez sur une touche pour continuer...
pause >nul

echo.
echo [5/5] Demarrage du frontend...
cd ../frontend
start "eConsulat Frontend" cmd /k "npm run dev"

echo.
echo ========================================
echo   Frontend demarre sur http://localhost:5173
echo ========================================
echo.
echo ✅ eConsulat avec generation PDF est pret!
echo.
echo URLs:
echo - Frontend: http://localhost:5173
echo - Backend:  http://localhost:8080
echo - Test PDF: test_pdf_generation.html
echo.
echo Appuyez sur une touche pour fermer...
pause >nul 