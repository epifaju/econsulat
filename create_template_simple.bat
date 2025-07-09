@echo off
chcp 65001 >nul
echo ========================================
echo   🛂 Création du template Word
echo ========================================
echo.

echo 📋 Création d'un template Word simple...
echo.

REM Créer le répertoire si nécessaire
if not exist "backend\src\main\resources" mkdir "backend\src\main\resources"

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
echo ^<p^>Data de pedido: _________________^</p^> >> template_temp.html
echo ^<p^>Assinatura: _____________________^</p^> >> template_temp.html
echo ^<p^>Observações:^</p^> >> template_temp.html
echo ^<p^>_________________________________^</p^> >> template_temp.html
echo ^<p^>_________________________________^</p^> >> template_temp.html
echo ^<p^>_________________________________^</p^> >> template_temp.html
echo ^<p style="text-align: center; font-style: italic;"^>Este documento foi gerado automaticamente pelo sistema eConsulat.^</p^> >> template_temp.html
echo ^</body^>^</html^> >> template_temp.html

echo ✅ Template HTML créé: template_temp.html
echo.
echo 📋 Instructions pour créer le fichier Word:
echo 1. Ouvrez template_temp.html dans un navigateur
echo 2. Copiez tout le contenu (Ctrl+A, Ctrl+C)
echo 3. Ouvrez Microsoft Word
echo 4. Créez un nouveau document
echo 5. Collez le contenu (Ctrl+V)
echo 6. Sauvegardez au format .docx
echo 7. Renommez en "FORMULARIO DE PEDIDO DE PASSAPORTE.docx"
echo 8. Copiez dans backend/src/main/resources/
echo.
echo 🔧 Ou utilisez le script automatique:
echo    Ouvrez create_simple_template.html dans votre navigateur
echo.

pause 