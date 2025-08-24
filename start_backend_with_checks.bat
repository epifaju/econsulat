@echo off
echo ========================================
echo 🔧 Démarrage Backend avec Vérifications
echo ========================================

echo.
echo 🔍 Vérification de l'état du système...
echo.

REM Vérifier si le port 8080 est déjà utilisé
echo 1. Vérification du port 8080...
netstat -an | findstr ":8080" >nul 2>&1
if %errorlevel% equ 0 (
    echo ❌ Le port 8080 est déjà utilisé
    echo.
    echo 📋 Processus utilisant le port 8080:
    netstat -ano | findstr ":8080"
    echo.
    echo 🛑 Arrêt des processus Java existants...
    taskkill /f /im java.exe 2>nul
    timeout /t 3 /nobreak >nul
    echo ✅ Processus Java arrêtés
) else (
    echo ✅ Port 8080 libre
)

echo.
echo 2. Vérification de Java...
java -version >nul 2>&1
if %errorlevel% equ 0 (
    echo ✅ Java installé
    java -version 2>&1 | findstr "version"
) else (
    echo ❌ Java non installé ou non accessible
    echo Veuillez installer Java 17+ et redémarrer
    pause
    exit /b 1
)

echo.
echo 3. Vérification de Maven...
mvn -version >nul 2>&1
if %errorlevel% equ 0 (
    echo ✅ Maven installé
    mvn -version 2>&1 | findstr "Apache Maven"
) else (
    echo ❌ Maven non installé ou non accessible
    echo Veuillez installer Maven et redémarrer
    pause
    exit /b 1
)

echo.
echo 4. Vérification du dossier backend...
if exist "backend" (
    echo ✅ Dossier backend trouvé
    cd backend
) else (
    echo ❌ Dossier backend non trouvé
    echo Veuillez exécuter ce script depuis la racine du projet
    pause
    exit /b 1
)

echo.
echo 5. Vérification du fichier pom.xml...
if exist "pom.xml" (
    echo ✅ Fichier pom.xml trouvé
) else (
    echo ❌ Fichier pom.xml non trouvé
    echo Veuillez vérifier la structure du projet
    pause
    exit /b 1
)

echo.
echo 6. Nettoyage du cache Maven...
call mvn clean >nul 2>&1
if %errorlevel% equ 0 (
    echo ✅ Cache Maven nettoyé
) else (
    echo ⚠️ Erreur lors du nettoyage Maven
)

echo.
echo ========================================
echo 🚀 Démarrage du Backend
echo ========================================
echo.
echo 📋 Configuration actuelle:
echo - Port: 8080
echo - CORS: Corrigé pour autoriser localhost:5173
echo - Base de données: PostgreSQL
echo.
echo 🔄 Démarrage en cours...
echo.

REM Démarrer le backend
call mvn spring-boot:run

echo.
echo ✅ Backend démarré avec succès
echo.
echo 📋 Pour tester:
echo 1. Ouvrir test_connectivite_simple.bat (test basique)
echo 2. Ouvrir PowerShell et exécuter: .\test_api_powershell.ps1
echo 3. Vérifier http://localhost:8080 dans le navigateur
echo.
pause
