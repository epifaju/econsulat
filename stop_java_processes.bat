@echo off
echo ========================================
echo   Arrêt des processus Java
echo   eConsulat
echo ========================================
echo.

echo Recherche des processus Java...
echo.

REM Trouver et arrêter tous les processus Java
for /f "tokens=2" %%a in ('tasklist /fi "IMAGENAME eq java.exe" /fo table /nh') do (
    set PID=%%a
    if not "!PID!"=="PID" (
        echo Arrêt du processus Java PID: !PID!
        taskkill /PID !PID! /F >nul 2>&1
        if !errorlevel! equ 0 (
            echo ✅ Processus Java arrêté
        ) else (
            echo ℹ️ Processus Java déjà arrêté ou inexistant
        )
    )
)

echo.
echo Vérification que le port 8080 est libre...
netstat -aon | findstr :8080 >nul 2>&1

if %errorlevel% equ 0 (
    echo ⚠️ Le port 8080 est encore utilisé
    echo.
    echo Arrêt forcé des processus sur le port 8080...
    
    REM Arrêter tous les processus sur le port 8080
    for /f "tokens=5" %%a in ('netstat -aon ^| findstr :8080') do (
        set PID=%%a
        if not "!PID!"=="" (
            echo Arrêt du processus PID !PID! sur le port 8080...
            taskkill /PID !PID! /F >nul 2>&1
        )
    )
    
    echo.
    echo Vérification finale...
    netstat -aon | findstr :8080 >nul 2>&1
    if %errorlevel% equ 0 (
        echo ❌ Le port 8080 est toujours utilisé
        echo Essayez de redémarrer l'ordinateur
    ) else (
        echo ✅ Le port 8080 est maintenant libre
    )
) else (
    echo ✅ Le port 8080 est libre
)

echo.
echo ========================================
echo   Arrêt terminé
echo ========================================
echo.
echo Vous pouvez maintenant redémarrer l'application.
echo.
pause 