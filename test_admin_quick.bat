@echo off
echo ========================================
echo Test rapide de l'interface admin eConsulat
echo ========================================

echo.
echo 1. Vérification de la base de données...
psql -U postgres -d econsulat -c "SELECT COUNT(*) as total_demandes FROM demandes;" 2>nul
if %errorlevel% neq 0 (
    echo ERREUR: Impossible de se connecter à la base de données
    echo Vérifiez que PostgreSQL est démarré et que la base econsulat existe
    pause
    exit /b 1
)

echo.
echo 2. Vérification des tables admin...
psql -U postgres -d econsulat -c "SELECT COUNT(*) as total_types FROM document_types;" 2>nul
psql -U postgres -d econsulat -c "SELECT COUNT(*) as total_users FROM users WHERE role = 'ADMIN';" 2>nul

echo.
echo 3. Vérification des comptes de test...
echo Comptes disponibles :
echo - Admin: admin@econsulat.com / admin123
echo - Agent: agent@econsulat.com / agent123
echo - User: user@econsulat.com / user123
echo - Citizen: citizen@econsulat.com / citizen123

echo.
echo 4. Instructions de test :
echo.
echo Pour tester l'interface admin :
echo 1. Assurez-vous que le backend est démarré (mvn spring-boot:run)
echo 2. Assurez-vous que le frontend est démarré (npm run dev)
echo 3. Ouvrez http://localhost:5173
echo 4. Connectez-vous avec admin@econsulat.com / admin123
echo 5. Vous devriez voir l'interface admin avec 4 onglets
echo.
echo Pour tester les API directement :
echo - Ouvrez test_admin_interface.html dans votre navigateur
echo - Connectez-vous et testez les différentes fonctionnalités
echo.
echo ========================================
echo Test terminé !
echo ========================================
pause 