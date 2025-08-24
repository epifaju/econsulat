@echo off
echo ========================================
echo 🔍 Test de Connectivité Simple
echo ========================================

echo.
echo 🌐 Test de connexion au backend...
echo.

echo 1. Test de ping vers localhost:8080
ping -n 1 127.0.0.1 >nul 2>&1
if %errorlevel% equ 0 (
    echo ✅ Ping vers localhost réussi
) else (
    echo ❌ Ping vers localhost échoué
)

echo.
echo 2. Test de port 8080 ouvert
netstat -an | findstr ":8080" >nul 2>&1
if %errorlevel% equ 0 (
    echo ✅ Port 8080 est ouvert
) else (
    echo ❌ Port 8080 n'est pas ouvert
)

echo.
echo 3. Test de processus Java
tasklist /fi "imagename eq java.exe" 2>nul | findstr "java.exe" >nul 2>&1
if %errorlevel% equ 0 (
    echo ✅ Processus Java en cours d'exécution
    tasklist /fi "imagename eq java.exe" | findstr "java.exe"
) else (
    echo ❌ Aucun processus Java en cours d'exécution
)

echo.
echo 4. Test de connexion HTTP simple
curl -s -o nul -w "Code de statut: %%{http_code}\n" http://localhost:8080/api/demandes/document-types 2>nul
if %errorlevel% equ 0 (
    echo ✅ Connexion HTTP réussie
) else (
    echo ❌ Connexion HTTP échouée
)

echo.
echo ========================================
echo 📋 Résumé des Tests
echo ========================================
echo.
echo Si tous les tests sont ✅, le backend fonctionne
echo Si des tests ❌, il faut redémarrer le backend
echo.
echo Pour redémarrer: restart_with_cors_fix.bat
echo.
pause
