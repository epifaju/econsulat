@echo off
echo ========================================
echo 🌐 Test API avec cURL
echo ========================================

echo.
echo 🔍 Test de connexion de base...
curl -s -o nul -w "Connexion: %%{http_code}\n" http://localhost:8080/api/demandes/document-types

echo.
echo 🔐 Test d'authentification...
curl -s -X POST http://localhost:8080/api/auth/login ^
  -H "Content-Type: application/json" ^
  -d "{\"email\":\"admin@econsulat.com\",\"password\":\"admin123\"}" ^
  -o temp_token.json

if exist temp_token.json (
    echo ✅ Réponse d'authentification reçue
    type temp_token.json
    echo.
    
    echo 📋 Test des données de référence...
    echo.
    
    echo - Civilités:
    curl -s -H "Authorization: Bearer $(type temp_token.json | findstr token | sed 's/.*"token":"\([^"]*\)".*/\1/')" http://localhost:8080/api/demandes/civilites
    
    echo.
    echo - Pays:
    curl -s -H "Authorization: Bearer $(type temp_token.json | findstr token | sed 's/.*"token":"\([^"]*\)".*/\1/')" http://localhost:8080/api/demandes/pays
    
    echo.
    echo - Types de documents:
    curl -s -H "Authorization: Bearer $(type temp_token.json | findstr token | sed 's/.*"token":"\([^"]*\)".*/\1/')" http://localhost:8080/api/demandes/document-types
    
    echo.
    echo 📝 Test de création de demande...
    curl -s -X POST http://localhost:8080/api/demandes ^
      -H "Content-Type: application/json" ^
      -H "Authorization: Bearer $(type temp_token.json | findstr token | sed 's/.*"token":"\([^"]*\)".*/\1/')" ^
      -d "{\"civiliteId\":1,\"firstName\":\"Test\",\"lastName\":\"Utilisateur\",\"birthDate\":\"1990-01-01\",\"birthPlace\":\"Paris\",\"birthCountryId\":1,\"streetName\":\"Rue de Test\",\"streetNumber\":\"123\",\"boxNumber\":\"\",\"postalCode\":\"75001\",\"city\":\"Paris\",\"countryId\":1,\"fatherFirstName\":\"Jean\",\"fatherLastName\":\"Utilisateur\",\"fatherBirthDate\":\"1960-01-01\",\"fatherBirthPlace\":\"Lyon\",\"fatherBirthCountryId\":1,\"motherFirstName\":\"Marie\",\"motherLastName\":\"Utilisateur\",\"motherBirthDate\":\"1965-01-01\",\"motherBirthPlace\":\"Marseille\",\"motherBirthCountryId\":1,\"documentTypeId\":1,\"documentFiles\":[]}"
    
    del temp_token.json
) else (
    echo ❌ Erreur d'authentification
)

echo.
echo ========================================
echo 📋 Résumé des Tests
echo ========================================
echo.
echo Si les tests échouent:
echo 1. Vérifiez que le backend est démarré
echo 2. Vérifiez que le port 8080 est libre
echo 3. Utilisez start_backend_with_checks.bat
echo.
pause
