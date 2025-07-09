@echo off
echo ========================================
echo   Test des fichiers eConsulat
echo ========================================
echo.

REM Obtenir le répertoire courant
set CURRENT_DIR=%~dp0
echo Répertoire courant: %CURRENT_DIR%
echo.

echo Vérification des fichiers nécessaires...
echo.

REM Vérifier les scripts batch
echo Scripts batch:
if exist "%CURRENT_DIR%fix_maven_backend.bat" (
    echo ✅ fix_maven_backend.bat
) else (
    echo ❌ fix_maven_backend.bat - MANQUANT
)

if exist "%CURRENT_DIR%fix_demandes_simple.bat" (
    echo ✅ fix_demandes_simple.bat
) else (
    echo ❌ fix_demandes_simple.bat - MANQUANT
)

if exist "%CURRENT_DIR%setup_demandes_simple.sql" (
    echo ✅ setup_demandes_simple.sql
) else (
    echo ❌ setup_demandes_simple.sql - MANQUANT
)

if exist "%CURRENT_DIR%test_api_demandes.html" (
    echo ✅ test_api_demandes.html
) else (
    echo ❌ test_api_demandes.html - MANQUANT
)

echo.

REM Vérifier les répertoires
echo Répertoires:
if exist "%CURRENT_DIR%backend" (
    echo ✅ backend/
    if exist "%CURRENT_DIR%backend\pom.xml" (
        echo ✅ backend/pom.xml
    ) else (
        echo ❌ backend/pom.xml - MANQUANT
    )
) else (
    echo ❌ backend/ - MANQUANT
)

if exist "%CURRENT_DIR%frontend" (
    echo ✅ frontend/
    if exist "%CURRENT_DIR%frontend\package.json" (
        echo ✅ frontend/package.json
    ) else (
        echo ❌ frontend/package.json - MANQUANT
    )
) else (
    echo ❌ frontend/ - MANQUANT
)

echo.

REM Vérifier les outils
echo Outils:
where java >nul 2>nul
if %errorlevel% equ 0 (
    echo ✅ Java
) else (
    echo ❌ Java - NON INSTALLÉ
)

where mvn >nul 2>nul
if %errorlevel% equ 0 (
    echo ✅ Maven
) else (
    echo ❌ Maven - NON INSTALLÉ
)

where node >nul 2>nul
if %errorlevel% equ 0 (
    echo ✅ Node.js
) else (
    echo ❌ Node.js - NON INSTALLÉ
)

where psql >nul 2>nul
if %errorlevel% equ 0 (
    echo ✅ PostgreSQL
) else (
    echo ❌ PostgreSQL - NON INSTALLÉ
)

echo.
echo ========================================
echo   Test terminé
echo ========================================
echo.

echo Si tous les fichiers sont présents (✅), vous pouvez utiliser:
echo - start_complete.bat (recommandé)
echo - start_with_maven_fix.bat
echo.
echo Si des fichiers sont manquants (❌), contactez l'administrateur.
echo.

pause 