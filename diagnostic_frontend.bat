@echo off
echo ========================================
echo Diagnostic Frontend eConsulat
echo ========================================

echo.
echo 1. Vérification de Node.js...
node --version
if %errorlevel% neq 0 (
    echo ERREUR: Node.js n'est pas installé ou pas dans le PATH
    pause
    exit /b 1
)

echo.
echo 2. Vérification de npm...
npm --version
if %errorlevel% neq 0 (
    echo ERREUR: npm n'est pas installé ou pas dans le PATH
    pause
    exit /b 1
)

echo.
echo 3. Vérification du dossier frontend...
if not exist "frontend" (
    echo ERREUR: Le dossier frontend n'existe pas
    pause
    exit /b 1
)

echo.
echo 4. Vérification de package.json...
if not exist "frontend\package.json" (
    echo ERREUR: package.json n'existe pas dans le dossier frontend
    pause
    exit /b 1
)

echo.
echo 5. Vérification des dépendances...
cd frontend
if not exist "node_modules" (
    echo ATTENTION: node_modules n'existe pas, installation des dépendances...
    npm install
    if %errorlevel% neq 0 (
        echo ERREUR: Impossible d'installer les dépendances
        pause
        exit /b 1
    )
)

echo.
echo 6. Vérification du port 5173...
netstat -an | findstr :5173
if %errorlevel% equ 0 (
    echo ATTENTION: Le port 5173 est déjà utilisé
    echo Arrêt des processus sur le port 5173...
    for /f "tokens=5" %%a in ('netstat -aon ^| findstr :5173') do (
        taskkill /f /pid %%a 2>nul
    )
)

echo.
echo 7. Démarrage du frontend...
echo Le frontend va démarrer sur http://localhost:5173
echo Appuyez sur Ctrl+C pour arrêter
echo.
npm run dev

pause 