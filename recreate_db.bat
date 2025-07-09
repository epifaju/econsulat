@echo off
echo ========================================
echo    🔄 Recréation Base de Données
echo ========================================
echo.

echo ⚠️  ATTENTION: Ce script va supprimer la base de données 'econsulat'
echo    et la recréer avec le bon schéma.
echo.
echo    Appuyez sur une touche pour continuer ou fermez cette fenêtre pour annuler...
pause >nul

echo.
echo 🗑️  Suppression de la base de données existante...
"C:\Program Files\PostgreSQL\13\bin\dropdb.exe" -U postgres econsulat 2>nul
if %errorlevel% neq 0 (
    echo    Base de données 'econsulat' n'existait pas ou erreur de suppression
) else (
    echo ✅ Base de données 'econsulat' supprimée
)

echo.
echo 🗄️  Création de la nouvelle base de données...
"C:\Program Files\PostgreSQL\13\bin\createdb.exe" -U postgres econsulat
if %errorlevel% neq 0 (
    echo ❌ Erreur lors de la création de la base de données
    pause
    exit /b 1
)
echo ✅ Base de données 'econsulat' créée

echo.
echo 📝 Application du script SQL mis à jour...
"C:\Program Files\PostgreSQL\13\bin\psql.exe" -U postgres -d econsulat -f econsulat_db.sql
if %errorlevel% neq 0 (
    echo ❌ Erreur lors de l'application du script SQL
    pause
    exit /b 1
)
echo ✅ Script SQL appliqué avec succès

echo.
echo ========================================
echo    ✅ Base de données recréée!
echo ========================================
echo    Vous pouvez maintenant relancer l'application.
echo.
pause 