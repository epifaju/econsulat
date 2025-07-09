@echo off
echo ========================================
echo   Nettoyage complet du cache Maven
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

echo ATTENTION: Ce script va supprimer le cache Maven local.
echo Cela peut prendre du temps lors de la prochaine compilation.
echo.
set /p confirm="Voulez-vous continuer? (o/n): "

if /i not "%confirm%"=="o" (
    echo Opération annulée.
    pause
    exit /b 0
)

echo.
echo ========================================
echo   Etape 1: Nettoyage du cache Maven
echo ========================================
echo.

echo Suppression du cache Maven local...
if exist "%USERPROFILE%\.m2\repository" (
    rmdir /s /q "%USERPROFILE%\.m2\repository"
    echo ✅ Cache Maven supprimé
) else (
    echo ℹ️ Cache Maven déjà vide
)

echo.
echo ========================================
echo   Etape 2: Nettoyage du projet
echo ========================================
echo.

REM Aller dans le répertoire backend
cd backend

echo Nettoyage du projet...
mvn clean

if %errorlevel% neq 0 (
    echo ❌ ERREUR lors du nettoyage
    cd ..
    pause
    exit /b 1
)

echo ✅ Projet nettoyé
echo.

echo ========================================
echo   Etape 3: Téléchargement des dépendances
echo ========================================
echo.

echo Téléchargement des dépendances Maven...
echo (Cela peut prendre plusieurs minutes...)
mvn dependency:resolve

if %errorlevel% neq 0 (
    echo ❌ ERREUR lors du téléchargement des dépendances
    cd ..
    pause
    exit /b 1
)

echo ✅ Dépendances téléchargées
echo.

echo ========================================
echo   Etape 4: Test de compilation
echo ========================================
echo.

echo Test de compilation...
mvn compile

if %errorlevel% neq 0 (
    echo ❌ ERREUR lors de la compilation
    cd ..
    pause
    exit /b 1
)

echo ✅ Compilation réussie
echo.

echo ========================================
echo   Etape 5: Test du plugin Spring Boot
echo ========================================
echo.

echo Test du plugin Spring Boot...
mvn spring-boot:help

if %errorlevel% neq 0 (
    echo ❌ ERREUR: Le plugin Spring Boot n'est toujours pas disponible
    echo.
    echo Solutions possibles:
    echo 1. Vérifiez votre connexion internet
    echo 2. Vérifiez que le fichier pom.xml est correct
    echo 3. Essayez de redémarrer votre ordinateur
    cd ..
    pause
    exit /b 1
)

echo ✅ Plugin Spring Boot disponible
echo.

echo ========================================
echo   ✅ Cache Maven nettoyé avec succès!
echo ========================================
echo.
echo Le projet Maven est maintenant complètement nettoyé et reconstruit.
echo.
echo Pour démarrer l'application:
echo start_with_maven_fix.bat
echo.

cd ..

pause 