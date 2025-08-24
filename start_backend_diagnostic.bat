@echo off
echo ========================================
echo Demarrage Backend eConsulat - Diagnostic
echo ========================================
echo.

REM Vérifier si on est dans le bon répertoire
if not exist "backend\pom.xml" (
    echo ❌ Erreur: Ce script doit etre execute depuis la racine du projet eConsulat
    echo    Repertoire actuel: %CD%
    echo    Fichier pom.xml non trouve dans backend\
    pause
    exit /b 1
)

echo [1/4] Verification de l'environnement...
echo.

REM Vérifier Java
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Java non trouve dans le PATH
    echo    Installez Java 17+ et ajoutez-le au PATH
    pause
    exit /b 1
) else (
    echo ✅ Java trouve
    java -version 2>&1 | findstr "version"
)

REM Vérifier Maven
mvn -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Maven non trouve dans le PATH
    echo    Installez Maven et ajoutez-le au PATH
    pause
    exit /b 1
) else (
    echo ✅ Maven trouve
    mvn -version | findstr "Apache Maven"
)

echo.
echo [2/4] Verification de la base de donnees...
echo.

REM Vérifier PostgreSQL
psql -h localhost -p 5432 -U postgres -d econsulat -c "SELECT 1;" >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Impossible de se connecter a PostgreSQL
    echo    Verifiez que PostgreSQL est demarre et accessible
    echo    Mot de passe par defaut: postgres
    echo.
    echo Tentative de demarrage de PostgreSQL...
    net start postgresql-x64-15 >nul 2>&1
    if %errorlevel% equ 0 (
        echo ✅ PostgreSQL demarre
        timeout /t 3 >nul
    ) else (
        echo ❌ Impossible de demarrer PostgreSQL automatiquement
        echo    Demarrez-le manuellement depuis les services Windows
        pause
        exit /b 1
    )
) else (
    echo ✅ PostgreSQL accessible
)

echo.
echo [3/4] Nettoyage et compilation...
echo.

cd backend

REM Nettoyer le cache Maven si nécessaire
echo Nettoyage du cache Maven...
mvn clean >nul 2>&1

REM Compiler le projet
echo Compilation du projet...
mvn compile >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Erreur de compilation
    echo    Verifiez les erreurs ci-dessus
    pause
    exit /b 1
) else (
    echo ✅ Compilation reussie
)

echo.
echo [4/4] Demarrage du backend...
echo.

echo 🚀 Demarrage de Spring Boot...
echo    URL: http://127.0.0.1:8080
echo    API: http://127.0.0.1:8080/api/demandes/document-types
echo.
echo Appuyez sur Ctrl+C pour arreter le backend
echo.

REM Démarrer le backend
mvn spring-boot:run

echo.
echo Backend arrete.
pause

