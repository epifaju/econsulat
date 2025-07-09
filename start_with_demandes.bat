@echo off
echo ========================================
echo Démarrage d'eConsulat avec fonctionnalité demandes
echo ========================================

echo.
echo 1. Configuration de la base de données...
call setup_demandes_db.bat

if %errorlevel% neq 0 (
    echo ERREUR: Échec de la configuration de la base de données
    pause
    exit /b 1
)

echo.
echo 2. Démarrage du backend Spring Boot...
start "Backend eConsulat" cmd /k "cd backend && mvn spring-boot:run"

echo Attente du démarrage du backend...
timeout /t 10 /nobreak >nul

echo.
echo 3. Démarrage du frontend React...
start "Frontend eConsulat" cmd /k "cd frontend && npm run dev"

echo.
echo ========================================
echo Application démarrée avec succès !
echo ========================================
echo.
echo Backend: http://localhost:8080
echo Frontend: http://localhost:5173
echo.
echo Comptes de test:
echo - Admin: admin@econsulat.com / admin123
echo - User: user@econsulat.com / user123
echo.
echo Nouvelle fonctionnalité disponible:
echo - Bouton "Nouvelle demande" dans le dashboard utilisateur
echo - Formulaire multistep complet
echo - Gestion des demandes avec statuts
echo.
echo Appuyez sur une touche pour fermer cette fenêtre...
pause >nul 