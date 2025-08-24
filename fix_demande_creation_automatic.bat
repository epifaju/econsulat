@echo off
echo ========================================
echo CORRECTION AUTOMATIQUE DE LA BASE DE DONNEES
echo ========================================
echo.

echo [1/3] Connexion a la base de donnees...
echo.

echo [2/3] Execution du script de correction...
psql -h localhost -U postgres -d econsulat -f fix_demande_creation_automatic.sql

if %ERRORLEVEL% EQU 0 (
    echo.
    echo [3/3] ✅ CORRECTION TERMINEE AVEC SUCCES !
    echo.
    echo La base de donnees a ete corrigee automatiquement.
    echo Vous pouvez maintenant redemarrer l'application.
    echo.
    pause
) else (
    echo.
    echo [3/3] ❌ ERREUR LORS DE LA CORRECTION !
    echo.
    echo Erreur lors de l'execution du script SQL.
    echo Verifiez que PostgreSQL est demarre et accessible.
    echo.
    pause
)
