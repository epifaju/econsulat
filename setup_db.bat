@echo off
echo ========================================
echo    🗄️  Configuration Base de Données
echo ========================================
echo.

echo 📊 Test de connexion PostgreSQL...
"C:\Program Files\PostgreSQL\13\bin\psql.exe" -U postgres -c "SELECT version();" >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Impossible de se connecter à PostgreSQL
    echo    Vérifiez que PostgreSQL est démarré
    pause
    exit /b 1
)
echo ✅ Connexion PostgreSQL réussie

echo.
echo 🗄️  Création de la base de données 'econsulat'...
"C:\Program Files\PostgreSQL\13\bin\createdb.exe" -U postgres econsulat 2>nul
if %errorlevel% neq 0 (
    echo    Base de données 'econsulat' existe déjà ou erreur de connexion
) else (
    echo ✅ Base de données 'econsulat' créée
)

echo.
echo 📝 Application du script SQL...
"C:\Program Files\PostgreSQL\13\bin\psql.exe" -U postgres -d econsulat -f econsulat_db.sql >nul 2>&1
if %errorlevel% neq 0 (
    echo ⚠️  Erreur lors de l'application du script SQL
) else (
    echo ✅ Script SQL appliqué avec succès
)

echo.
echo ========================================
echo    ✅ Configuration terminée!
echo ========================================
pause 