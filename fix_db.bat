@echo off
echo ========================================
echo    🔧 Correction Base de Données
echo ========================================
echo.

echo 📝 Suppression des tables existantes et recréation...
"C:\Program Files\PostgreSQL\13\bin\psql.exe" -U postgres -d econsulat -c "DROP TABLE IF EXISTS citizens CASCADE; DROP TABLE IF EXISTS users CASCADE;" >nul 2>&1
echo ✅ Tables supprimées

echo.
echo 📝 Application du script SQL corrigé...
"C:\Program Files\PostgreSQL\13\bin\psql.exe" -U postgres -d econsulat -f econsulat_db.sql
if %errorlevel% neq 0 (
    echo ❌ Erreur lors de l'application du script SQL
    pause
    exit /b 1
)
echo ✅ Script SQL appliqué avec succès

echo.
echo ========================================
echo    ✅ Base de données corrigée!
echo ========================================
echo    Vous pouvez maintenant relancer l'application.
echo.
pause 