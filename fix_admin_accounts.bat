@echo off
echo ========================================
echo    🔧 Correction des Comptes Admin
echo ========================================
echo.

echo 🔄 Suppression des anciens comptes...
"C:\Program Files\PostgreSQL\13\bin\psql.exe" -U postgres -d econsulat -c "DELETE FROM users WHERE username IN ('admin', 'user');"

echo.
echo 🔄 Création des nouveaux comptes avec BCrypt...
"C:\Program Files\PostgreSQL\13\bin\psql.exe" -U postgres -d econsulat -c "INSERT INTO users (username, password, role) VALUES ('admin', '\$2a\$10\$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'ADMIN');"

"C:\Program Files\PostgreSQL\13\bin\psql.exe" -U postgres -d econsulat -c "INSERT INTO users (username, password, role) VALUES ('user', '\$2a\$10\$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'USER');"

echo.
echo ✅ Comptes corrigés!
echo.
echo 📋 Comptes disponibles:
echo    Admin: admin / admin123
echo    User:  user / user123
echo.
echo 📊 Vérification des utilisateurs:
"C:\Program Files\PostgreSQL\13\bin\psql.exe" -U postgres -d econsulat -c "SELECT id, username, role FROM users ORDER BY id;"

echo.
pause 