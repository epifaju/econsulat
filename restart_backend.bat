@echo off
echo 🔄 Redémarrage du backend eConsulat...

REM Arrêter tous les processus Java existants
echo 🛑 Arrêt des processus Java existants...
taskkill /f /im java.exe 2>nul
timeout /t 2 /nobreak >nul

REM Définir JAVA_HOME
set JAVA_HOME=C:\Program Files\Java\jdk-17
echo ✅ JAVA_HOME défini: %JAVA_HOME%

REM Aller dans le dossier backend
cd backend

REM Nettoyer et compiler
echo 🔨 Compilation du projet...
call mvnw.cmd clean compile
if %errorlevel% neq 0 (
    echo ❌ Erreur de compilation
    pause
    exit /b 1
)

REM Démarrer le backend
echo 🚀 Démarrage du backend...
call mvnw.cmd spring-boot:run

pause 