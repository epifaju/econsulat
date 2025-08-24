# Test API Simple eConsulat - PowerShell
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Test API Simple eConsulat" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Configuration
$baseUrl = "http://127.0.0.1:8080"

# 1. Test de connexion au backend
Write-Host "[1/3] Test de connexion au backend..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "$baseUrl/api/demandes/document-types" -Method GET -TimeoutSec 10 -ErrorAction Stop
    Write-Host "✅ Backend accessible!" -ForegroundColor Green
    Write-Host "   Status: $($response.StatusCode)" -ForegroundColor Gray
    Write-Host "   Réponse: $($response.Content.Length) caractères" -ForegroundColor Gray
} catch {
    Write-Host "❌ Backend non accessible" -ForegroundColor Red
    Write-Host "   Erreur: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host ""
    Write-Host "💡 Solutions:" -ForegroundColor Cyan
    Write-Host "   1. Démarrer le backend: .\start_backend.ps1" -ForegroundColor White
    Write-Host "   2. Vérifier que le port 8080 est libre" -ForegroundColor White
    Write-Host "   3. Vérifier les logs du backend" -ForegroundColor White
    Read-Host "Appuyez sur Entrée pour continuer"
    exit
}

Write-Host ""

# 2. Test d'authentification
Write-Host "[2/3] Test d'authentification..." -ForegroundColor Yellow
try {
    $loginData = @{
        email = "user@test.com"
        password = "password123"
    } | ConvertTo-Json
    
    $headers = @{
        "Content-Type" = "application/json"
    }
    
    Write-Host "   Tentative de connexion avec: user@test.com" -ForegroundColor Gray
    
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
            Read-Host "Appuyez sur Entrée pour continuer"
            exit
        }
    } else {
        Write-Host "❌ Échec de l'authentification" -ForegroundColor Red
        Write-Host "   Status: $($loginResponse.StatusCode)" -ForegroundColor Gray
        Write-Host "   Réponse: $($loginResponse.Content)" -ForegroundColor Gray
        Read-Host "Appuyez sur Entrée pour continuer"
        exit
    }
} catch {
    Write-Host "❌ Erreur lors de l'authentification" -ForegroundColor Red
    Write-Host "   Erreur: $($_.Exception.Message)" -ForegroundColor Red
    
    if ($_.Exception.Message -like "*401*") {
        Write-Host "💡 L'utilisateur user@test.com n'existe pas ou le mot de passe est incorrect" -ForegroundColor Cyan
        Write-Host "   Créez un utilisateur avec: diagnostic_auth_403.sql" -ForegroundColor White
    }
    Read-Host "Appuyez sur Entrée pour continuer"
    exit
}

Write-Host ""

# 3. Test de création de demande
Write-Host "[3/3] Test de création de demande..." -ForegroundColor Yellow
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
Read-Host "Appuyez sur Entrée pour continuer"

