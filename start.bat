@echo off
echo ========================================
echo    Démarrage de eConsulat
echo ========================================

echo.
echo 1. Arrêt des processus existants sur les ports 8080 et 5173...
for /f "tokens=5" %%a in ('netstat -ano ^| findstr :8080') do taskkill /PID %%a /F >nul 2>&1
for /f "tokens=5" %%a in ('netstat -ano ^| findstr :5173') do taskkill /PID %%a /F >nul 2>&1

echo.
echo 2. Démarrage du backend...
cd backend
start "Backend" cmd /k "mvn spring-boot:run"

echo.
echo 3. Attente du démarrage du backend...
timeout /t 10 /nobreak >nul

echo.
echo 4. Démarrage du frontend...
cd ../frontend
start "Frontend" cmd /k "npm run dev"

echo.
echo ========================================
echo    Application en cours de démarrage
echo ========================================
echo Backend: http://localhost:8080
echo Frontend: http://localhost:5173
echo.
echo Appuyez sur une touche pour fermer cette fenêtre...
pause >nul 