@echo off
chcp 65001 >nul
echo ========================================
echo   🛂 Correction du template Word
echo ========================================
echo.

echo 📋 Ce script vous aide à corriger le template Word corrompu.
echo.

echo 🚨 Problème identifié:
echo    Le fichier "FORMULARIO DE PEDIDO DE PASSAPORTE.docx" est corrompu.
echo    Erreur: "Cannot read the array length because "<local3>" is null"
echo.

echo ✅ Solutions disponibles:
echo    1. Créer un nouveau template manuellement
echo    2. Utiliser le template de secours
echo    3. Vérifier et corriger le fichier existant
echo.

set /p choice="Choisissez une option (1-3): "

if "%choice%"=="1" goto :manual
if "%choice%"=="2" goto :backup
if "%choice%"=="3" goto :check
goto :invalid

:manual
echo.
echo 📝 Solution 1: Créer un nouveau template manuellement
echo.
echo Instructions:
echo 1. Ouvrez Microsoft Word
echo 2. Créez un nouveau document
echo 3. Ajoutez le contenu suivant:
echo.
echo    FORMULARIO DE PEDIDO DE PASSAPORTE
echo.
echo    Nome: {{Prénom}}
echo    Apelido: {{Nom de famille}}
echo    Data de nascimento: {{Date de naissance}}
echo    Local de nascimento: {{Local de nascimento}}
echo.
echo 4. Sauvegardez au format .docx
echo 5. Renommez en "FORMULARIO DE PEDIDO DE PASSAPORTE.docx"
echo 6. Copiez dans backend/src/main/resources/
echo.
pause
goto :end

:backup
echo.
echo 💾 Solution 2: Utiliser le template de secours
echo.
echo Création d'un template de secours...
echo.

set "template_path=backend\src\main\resources\FORMULARIO DE PEDIDO DE PASSAPORTE.docx"

if exist "%template_path%" (
    echo Sauvegarde de l'ancien fichier...
    move "%template_path%" "%template_path%.backup"
    echo ✅ Ancien fichier sauvegardé
)

echo Création du nouveau template...
echo.

REM Créer un fichier HTML simple qui peut être converti en Word
echo ^<!DOCTYPE html^> > template_temp.html
echo ^<html^> >> template_temp.html
echo ^<head^>^<title^>Template Passeport^</title^>^<meta charset="UTF-8"^>^</head^> >> template_temp.html
echo ^<body^> >> template_temp.html
echo ^<h1^>FORMULARIO DE PEDIDO DE PASSAPORTE^</h1^> >> template_temp.html
echo ^<table border="1" cellpadding="5"^> >> template_temp.html
echo ^<tr^>^<td^>Nome:^</td^>^<td^>{{Prénom}}^</td^>^</tr^> >> template_temp.html
echo ^<tr^>^<td^>Apelido:^</td^>^<td^>{{Nom de famille}}^</td^>^</tr^> >> template_temp.html
echo ^<tr^>^<td^>Data de nascimento:^</td^>^<td^>{{Date de naissance}}^</td^>^</tr^> >> template_temp.html
echo ^<tr^>^<td^>Local de nascimento:^</td^>^<td^>{{Local de nascimento}}^</td^>^</tr^> >> template_temp.html
echo ^</table^> >> template_temp.html
echo ^</body^>^</html^> >> template_temp.html

echo ✅ Template HTML créé: template_temp.html
echo.
echo Instructions pour convertir en Word:
echo 1. Ouvrez template_temp.html dans un navigateur
echo 2. Copiez tout le contenu
echo 3. Créez un nouveau document Word
echo 4. Collez le contenu
echo 5. Sauvegardez au format .docx
echo 6. Renommez en "FORMULARIO DE PEDIDO DE PASSAPORTE.docx"
echo 7. Copiez dans backend/src/main/resources/
echo.
pause
goto :end

:check
echo.
echo 🔍 Solution 3: Vérifier le fichier existant
echo.

set "template_path=backend\src\main\resources\FORMULARIO DE PEDIDO DE PASSAPORTE.docx"

if exist "%template_path%" (
    echo 📄 Fichier trouvé: %template_path%
    echo.
    echo Vérification du format...
    
    REM Vérifier si c'est un fichier .docx valide
    powershell -Command "try { $bytes = [System.IO.File]::ReadAllBytes('%template_path%'); if ($bytes.Length -ge 2 -and $bytes[0] -eq 0x50 -and $bytes[1] -eq 0x4B) { Write-Host '✅ Format .docx valide détecté' } else { Write-Host '❌ Format non valide - fichier corrompu' } } catch { Write-Host '❌ Erreur lors de la lecture du fichier' }"
    
    echo.
    echo Taille du fichier:
    dir "%template_path%" | findstr "FORMULARIO"
    
    echo.
    echo Recommandation: Le fichier semble corrompu.
    echo Utilisez la Solution 1 ou 2 pour créer un nouveau template.
    
) else (
    echo ❌ Fichier non trouvé: %template_path%
    echo Utilisez la Solution 1 ou 2 pour créer un nouveau template.
)

echo.
pause
goto :end

:invalid
echo.
echo ❌ Option invalide. Veuillez choisir 1, 2 ou 3.
echo.
pause
goto :end

:end
echo.
echo 🔧 Prochaines étapes après correction:
echo 1. Arrêtez l'application (Ctrl+C)
echo 2. Copiez le nouveau template dans backend/src/main/resources/
echo 3. Redémarrez avec start.sh
echo 4. Testez la génération de document
echo.
echo ✅ Correction terminée!
pause 