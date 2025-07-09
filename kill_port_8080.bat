@echo off
echo ========================================
echo   Arrêt des processus sur le port 8080
echo   eConsulat
echo ========================================
echo.

echo Recherche des processus utilisant le port 8080...
echo.

REM Trouver les processus utilisant le port 8080
for /f "tokens=5" %%a in ('netstat -aon ^| findstr :8080') do (
    set PID=%%a
    echo Processus trouvé avec PID: !PID!
    
    REM Obtenir le nom du processus
    for /f "tokens=2" %%b in ('tasklist /fi "PID eq !PID!" /fo table /nh') do (
        set PROCESS_NAME=%%b
        echo Nom du processus: !PROCESS_NAME!
        
        REM Demander confirmation pour arrêter
        echo.
        set /p confirm="Voulez-vous arrêter ce processus? (o/n): "
        
        if /i "!confirm!"=="o" (
            echo Arrêt du processus PID !PID!...
            taskkill /PID !PID! /F
            if !errorlevel! equ 0 (
                echo ✅ Processus arrêté avec succès
            ) else (
                echo ❌ Erreur lors de l'arrêt du processus
            )
        ) else (
            echo Processus conservé
        )
    )
)

echo.
echo Vérification que le port 8080 est libre...
netstat -aon | findstr :8080

if %errorlevel% equ 0 (
    echo ⚠️ Le port 8080 est encore utilisé
    echo.
    echo Solutions possibles:
    echo 1. Redémarrer l'ordinateur
    echo 2. Changer le port dans application.properties
    echo 3. Arrêter manuellement les processus
) else (
    echo ✅ Le port 8080 est maintenant libre
)

echo.
pause 