@echo off
echo ========================================
echo   Correction du projet Maven backend
echo   eConsulat
echo ========================================
echo.

REM Vérifier si Maven est installé
where mvn >nul 2>nul
if %errorlevel% neq 0 (
    echo ❌ ERREUR: Maven n'est pas installé ou pas dans le PATH
    echo Veuillez installer Maven et réessayer
    pause
    exit /b 1
)

echo ✅ Maven est installé
echo.

REM Aller dans le répertoire backend
cd backend

echo ========================================
echo   Etape 1: Nettoyage du projet
echo ========================================
echo.

echo Nettoyage du projet Maven...
mvn clean

if %errorlevel% neq 0 (
    echo ❌ ERREUR lors du nettoyage
    pause
    exit /b 1
)

echo ✅ Nettoyage réussi
echo.

echo ========================================
echo   Etape 2: Téléchargement des dépendances
echo ========================================
echo.

echo Téléchargement des dépendances Maven...
mvn dependency:resolve

if %errorlevel% neq 0 (
    echo ❌ ERREUR lors du téléchargement des dépendances
    pause
    exit /b 1
)

echo ✅ Dépendances téléchargées
echo.

echo ========================================
echo   Etape 3: Compilation du projet
echo ========================================
echo.

echo Compilation du projet...
mvn compile

if %errorlevel% neq 0 (
    echo ❌ ERREUR lors de la compilation
    pause
    exit /b 1
)

echo ✅ Compilation réussie
echo.

echo ========================================
echo   Etape 4: Test du plugin Spring Boot
echo ========================================
echo.

echo Test du plugin Spring Boot...
mvn spring-boot:help

if %errorlevel% neq 0 (
    echo ❌ ERREUR: Le plugin Spring Boot n'est pas disponible
    echo.
    echo Solutions possibles:
    echo 1. Vérifiez votre connexion internet
    echo 2. Vérifiez que le fichier pom.xml est correct
    echo 3. Essayez de supprimer le dossier .m2/repository
    pause
    exit /b 1
)

echo ✅ Plugin Spring Boot disponible
echo.

echo ========================================
echo   ✅ Projet Maven corrigé avec succès!
echo ========================================
echo.
echo Le backend est maintenant prêt à être démarré.
echo.
echo Pour démarrer le backend:
echo mvn spring-boot:run
echo.
echo Ou utilisez le script de démarrage complet:
echo start_final_demandes.bat
echo.

cd ..

pause 