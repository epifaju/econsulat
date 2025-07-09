@echo off
echo ========================================
echo    🔧 Correction Mot de Passe Admin
echo ========================================
echo.

echo 🔄 Mise à jour du mot de passe admin...
"C:\Program Files\PostgreSQL\13\bin\psql.exe" -U postgres -d econsulat -c "UPDATE users SET password = '\$2a\$10\$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi' WHERE username = 'admin';"

if %errorlevel% equ 0 (
    echo ✅ Mot de passe admin corrigé avec succès!
    echo.
    echo 📋 Informations de connexion admin:
    echo    Nom d'utilisateur: admin
    echo    Mot de passe: admin123
    echo    Rôle: ADMIN
) else (
    echo ❌ Erreur lors de la correction du mot de passe
)

echo.
echo 📊 Vérification des utilisateurs:
"C:\Program Files\PostgreSQL\13\bin\psql.exe" -U postgres -d econsulat -c "SELECT id, username, role FROM users ORDER BY id;"

echo.
pause 