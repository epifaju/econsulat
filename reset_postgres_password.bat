@echo off
echo ========================================
echo    🔐 Réinitialisation Mot de Passe PostgreSQL
echo ========================================
echo.

echo ⚠️  ATTENTION: Ce script va changer le mot de passe de l'utilisateur 'postgres'
echo    Le nouveau mot de passe sera: postgres
echo.
echo    Appuyez sur une touche pour continuer ou fermez cette fenêtre pour annuler...
pause >nul

echo.
echo 🔄 Changement du mot de passe...
"C:\Program Files\PostgreSQL\13\bin\psql.exe" -U postgres -c "ALTER USER postgres PASSWORD 'postgres';" 2>nul

if %errorlevel% neq 0 (
    echo ❌ Impossible de changer le mot de passe
    echo    Vous devez peut-être vous connecter en tant qu'administrateur
    echo    ou utiliser pgAdmin pour changer le mot de passe
) else (
    echo ✅ Mot de passe changé avec succès
    echo    Nouveau mot de passe: postgres
)

echo.
echo ========================================
echo    ✅ Opération terminée!
echo ========================================
pause 