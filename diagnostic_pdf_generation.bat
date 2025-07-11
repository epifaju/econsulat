@echo off
echo ========================================
echo   Diagnostic Generation PDF - eConsulat
echo ========================================
echo.

echo [1/5] Verification des dependances Maven...
cd backend
call mvn dependency:tree | findstr itext
if errorlevel 1 (
    echo ❌ Dependances iText non trouvees
    echo Verifiez le fichier pom.xml
) else (
    echo ✅ Dependances iText detectees
)

echo.
echo [2/5] Compilation du projet...
call mvn clean compile
if errorlevel 1 (
    echo ❌ Erreur de compilation
    echo Verifiez les erreurs ci-dessus
    pause
    exit /b 1
) else (
    echo ✅ Compilation reussie
)

echo.
echo [3/5] Verification des fichiers de template...
if exist "src\main\resources\templates\template_content.txt" (
    echo ✅ Template content trouve
) else (
    echo ❌ Template content manquant
)

if exist "src\main\resources\templates\default_template.docx" (
    echo ✅ Template Word par defaut trouve
) else (
    echo ⚠️ Template Word par defaut manquant (sera genere automatiquement)
)

echo.
echo [4/5] Creation du dossier documents...
if not exist "documents\generated" (
    mkdir "documents\generated"
    echo ✅ Dossier documents/generated cree
) else (
    echo ✅ Dossier documents/generated existe
)

echo.
echo [5/5] Test de connexion a la base de donnees...
echo Verifiez que PostgreSQL est demarre et accessible
echo.

echo ========================================
echo   Instructions de test
echo ========================================
echo.
echo 1. Demarrez le backend:
echo    cd backend && mvn spring-boot:run
echo.
echo 2. Dans un autre terminal, demarrez le frontend:
echo    cd frontend && npm run dev
echo.
echo 3. Ouvrez test_pdf_generation.html dans votre navigateur
echo.
echo 4. Connectez-vous en tant qu'admin et testez la generation PDF
echo.
echo 5. Verifiez les logs du backend pour les erreurs detaillees
echo.

echo Appuyez sur une touche pour continuer...
pause >nul 