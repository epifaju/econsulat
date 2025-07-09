@echo off
echo ========================================
echo Test Frontend eConsulat
echo ========================================

echo.
echo 1. Vérification de Node.js...
node --version
if %errorlevel% neq 0 (
    echo ERREUR: Node.js n'est pas installé
    pause
    exit /b 1
)

echo.
echo 2. Vérification du dossier frontend...
if not exist "frontend" (
    echo ERREUR: Dossier frontend introuvable
    pause
    exit /b 1
)

echo.
echo 3. Vérification de package.json...
if not exist "frontend\package.json" (
    echo ERREUR: package.json introuvable
    pause
    exit /b 1
)

echo.
echo 4. Vérification des dépendances...
cd frontend
if not exist "node_modules" (
    echo Installation des dépendances...
    npm install
    if %errorlevel% neq 0 (
        echo ERREUR: Impossible d'installer les dépendances
        pause
        exit /b 1
    )
)

echo.
echo 5. Vérification du port 5173...
netstat -an | findstr :5173
if %errorlevel% equ 0 (
    echo ATTENTION: Le port 5173 est déjà utilisé
    echo Arrêt des processus...
    for /f "tokens=5" %%a in ('netstat -aon ^| findstr :5173') do (
        taskkill /f /pid %%a 2>nul
    )
    timeout /t 2 /nobreak >nul
)

echo.
echo 6. Test de démarrage du frontend...
echo Démarrage en mode test (5 secondes)...
start /b npm run dev

echo.
echo 7. Attente du démarrage...
timeout /t 5 /nobreak >nul

echo.
echo 8. Test de connexion...
curl -s http://localhost:5173 >nul
if %errorlevel% equ 0 (
    echo ✅ Frontend accessible sur http://localhost:5173
) else (
    echo ❌ Frontend non accessible
)

echo.
echo 9. Arrêt du processus de test...
taskkill /f /im node.exe 2>nul

echo.
echo ========================================
echo Test terminé
echo ========================================
echo.
echo Si le test a réussi, vous pouvez démarrer le frontend avec :
echo cd frontend
echo npm run dev
echo.
pause 