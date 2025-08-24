# Test API Direct eConsulat - PowerShell
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Test API Direct eConsulat" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Configuration
$baseUrl = "http://127.0.0.1:8080"
$testUser = "user@test.com"
$testPassword = "password123"

# 1. Test de connexion au backend
Write-Host "[1/4] Test de connexion au backend..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "$baseUrl/api/demandes/document-types" -Method GET -TimeoutSec 10 -ErrorAction Stop
    Write-Host "✅ Backend accessible!" -ForegroundColor Green
    Write-Host "   Status: $($response.StatusCode)" -ForegroundColor Gray
    Write-Host "   Réponse: $($response.Content.Length) caractères" -ForegroundColor Gray
    
    # Afficher le contenu de la réponse
    try {
        $jsonResponse = $response.Content | ConvertFrom-Json
        Write-Host "   Types de documents: $($jsonResponse.Count)" -ForegroundColor Gray
    } catch {
        Write-Host "   Contenu brut: $($response.Content.Substring(0, [Math]::Min(100, $response.Content.Length)))..." -ForegroundColor Gray
    }
} catch {
    Write-Host "❌ Backend non accessible" -ForegroundColor Red
    Write-Host "   Erreur: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host ""
    Write-Host "💡 Solutions:" -ForegroundColor Cyan
    Write-Host "   1. Demarrer le backend: start_backend_diagnostic.bat" -ForegroundColor White
    Write-Host "   2. Verifier que le port 8080 est libre" -ForegroundColor White
    Write-Host "   3. Verifier les logs du backend" -ForegroundColor White
    pause
    exit
}

Write-Host ""

# 2. Test d'authentification
Write-Host "[2/4] Test d'authentification..." -ForegroundColor Yellow
try {
    $loginData = @{
        email = $testUser
        password = $testPassword
    } | ConvertTo-Json
    
    $headers = @{
        "Content-Type" = "application/json"
    }
    
    Write-Host "   Tentative de connexion avec: $testUser" -ForegroundColor Gray
    
    $loginResponse = Invoke-WebRequest -Uri "$baseUrl/api/auth/login" -Method POST -Body $loginData -Headers $headers -TimeoutSec 10 -ErrorAction Stop
    
    if ($loginResponse.StatusCode -eq 200) {
        $loginResult = $loginResponse.Content | ConvertFrom-Json
        $token = $loginResult.token
        
        if ($token) {
            Write-Host "✅ Authentification réussie!" -ForegroundColor Green
            Write-Host "   Token reçu: $($token.Substring(0, [Math]::Min(50, $token.Length)))..." -ForegroundColor Gray
            Write-Host "   Rôle: $($loginResult.role)" -ForegroundColor Gray
        } else {
            Write-Host "❌ Token manquant dans la réponse" -ForegroundColor Red
            Write-Host "   Réponse complète: $($loginResponse.Content)" -ForegroundColor Gray
            pause
            exit
        }
    } else {
        Write-Host "❌ Échec de l'authentification" -ForegroundColor Red
        Write-Host "   Status: $($loginResponse.StatusCode)" -ForegroundColor Gray
        Write-Host "   Réponse: $($loginResponse.Content)" -ForegroundColor Gray
        pause
        exit
    }
} catch {
    Write-Host "❌ Erreur lors de l'authentification" -ForegroundColor Red
    Write-Host "   Erreur: $($_.Exception.Message)" -ForegroundColor Red
    
    if ($_.Exception.Message -like "*401*") {
        Write-Host "💡 L'utilisateur $testUser n'existe pas ou le mot de passe est incorrect" -ForegroundColor Cyan
        Write-Host "   Créez un utilisateur avec: diagnostic_auth_403.sql" -ForegroundColor White
    }
    pause
    exit
}

Write-Host ""

# 3. Test de création de demande
Write-Host "[3/4] Test de création de demande..." -ForegroundColor Yellow
try {
    $demandeData = @{
        civiliteId = 1
        firstName = "Test"
        lastName = "Utilisateur"
        birthDate = "1990-01-01"
        birthPlace = "Paris"
        birthCountryId = 1
        streetName = "Rue de Test"
        streetNumber = "123"
        boxNumber = ""
        postalCode = "75001"
        city = "Paris"
        countryId = 1
        fatherFirstName = "Jean"
        fatherLastName = "Utilisateur"
        fatherBirthDate = "1960-01-01"
        fatherBirthPlace = "Lyon"
        fatherBirthCountryId = 1
        motherFirstName = "Marie"
        motherLastName = "Utilisateur"
        motherBirthDate = "1965-01-01"
        motherBirthPlace = "Marseille"
        motherBirthCountryId = 1
        documentTypeId = 1
        documentFiles = @()
    } | ConvertTo-Json
    
    $authHeaders = @{
        "Content-Type" = "application/json"
        "Authorization" = "Bearer $token"
    }
    
    Write-Host "   Envoi de la demande..." -ForegroundColor Gray
    
    $demandeResponse = Invoke-WebRequest -Uri "$baseUrl/api/demandes" -Method POST -Body $demandeData -Headers $authHeaders -TimeoutSec 10 -ErrorAction Stop
    
    if ($demandeResponse.StatusCode -eq 200) {
        Write-Host "✅ Demande créée avec succès!" -ForegroundColor Green
        $demandeResult = $demandeResponse.Content | ConvertFrom-Json
        Write-Host "   ID de la demande: $($demandeResult.id)" -ForegroundColor Gray
        Write-Host "   Statut: $($demandeResult.status)" -ForegroundColor Gray
    } else {
        Write-Host "❌ Échec de la création de demande" -ForegroundColor Red
        Write-Host "   Status: $($demandeResponse.StatusCode)" -ForegroundColor Gray
        Write-Host "   Réponse: $($demandeResponse.Content)" -ForegroundColor Gray
    }
} catch {
    Write-Host "❌ Erreur lors de la création de demande" -ForegroundColor Red
    Write-Host "   Erreur: $($_.Exception.Message)" -ForegroundColor Red
    
    if ($_.Exception.Message -like "*403*") {
        Write-Host "💡 Erreur 403 - Problème d'autorisation" -ForegroundColor Cyan
        Write-Host "   Vérifiez:" -ForegroundColor White
        Write-Host "   1. Le rôle de l'utilisateur dans la base" -ForegroundColor White
        Write-Host "   2. La configuration de sécurité Spring" -ForegroundColor White
        Write-Host "   3. Les logs du backend" -ForegroundColor White
    }
}

Write-Host ""

# 4. Test des autres endpoints
Write-Host "[4/4] Test des autres endpoints..." -ForegroundColor Yellow

$endpoints = @(
    @{ Name = "Mes demandes"; Url = "/api/demandes/my"; Method = "GET" }
    @{ Name = "Civilités"; Url = "/api/demandes/civilites"; Method = "GET" }
    @{ Name = "Pays"; Url = "/api/demandes/pays"; Method = "GET" }
)

foreach ($endpoint in $endpoints) {
    try {
        $response = Invoke-WebRequest -Uri "$baseUrl$($endpoint.Url)" -Method $endpoint.Method -Headers $authHeaders -TimeoutSec 10 -ErrorAction Stop
        
        if ($response.StatusCode -eq 200) {
            Write-Host "✅ $($endpoint.Name): OK" -ForegroundColor Green
        } else {
            Write-Host "❌ $($endpoint.Name): $($response.StatusCode)" -ForegroundColor Red
        }
    } catch {
        Write-Host "❌ $($endpoint.Name): Erreur - $($_.Exception.Message)" -ForegroundColor Red
    }
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Test terminé" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

# Résumé des résultats
Write-Host ""
Write-Host "📊 Résumé:" -ForegroundColor Yellow
if ($token) {
    Write-Host "✅ Authentification: Réussie" -ForegroundColor Green
} else {
    Write-Host "❌ Authentification: Échouée" -ForegroundColor Red
}

if ($demandeResponse -and $demandeResponse.StatusCode -eq 200) {
    Write-Host "✅ Création de demande: Réussie" -ForegroundColor Green
} else {
    Write-Host "❌ Création de demande: Échouée" -ForegroundColor Red
}

Write-Host ""
Write-Host "Appuyez sur une touche pour continuer..." -ForegroundColor Yellow
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")

