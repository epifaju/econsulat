# Test de l'API Backend avec PowerShell
# Exécuter ce script dans PowerShell

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "🔍 Test de l'API Backend" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Test 1: Connexion de base
Write-Host "1. 🌐 Test de connexion de base..." -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "http://localhost:8080/api/demandes/document-types" -Method Get -TimeoutSec 10
    Write-Host "✅ Connexion réussie - Types de documents chargés: $($response.Count)" -ForegroundColor Green
} catch {
    Write-Host "❌ Erreur de connexion: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""

# Test 2: Authentification
Write-Host "2. 🔐 Test d'authentification..." -ForegroundColor Yellow
try {
    $loginData = @{
        email = "admin@econsulat.com"
        password = "admin123"
    } | ConvertTo-Json

    $headers = @{
        "Content-Type" = "application/json"
    }

    $response = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" -Method Post -Body $loginData -Headers $headers -TimeoutSec 10
    
    if ($response.token) {
        Write-Host "✅ Authentification réussie - Token reçu" -ForegroundColor Green
        $token = $response.token
        
        # Test 3: Données de référence avec token
        Write-Host ""
        Write-Host "3. 📋 Test des données de référence..." -ForegroundColor Yellow
        
        $authHeaders = @{
            "Authorization" = "Bearer $token"
            "Content-Type" = "application/json"
        }
        
        try {
            $civilites = Invoke-RestMethod -Uri "http://localhost:8080/api/demandes/civilites" -Method Get -Headers $authHeaders -TimeoutSec 10
            Write-Host "✅ Civilités chargées: $($civilites.Count)" -ForegroundColor Green
        } catch {
            Write-Host "❌ Erreur civilités: $($_.Exception.Message)" -ForegroundColor Red
        }
        
        try {
            $pays = Invoke-RestMethod -Uri "http://localhost:8080/api/demandes/pays" -Method Get -Headers $authHeaders -TimeoutSec 10
            Write-Host "✅ Pays chargés: $($pays.Count)" -ForegroundColor Green
        } catch {
            Write-Host "❌ Erreur pays: $($_.Exception.Message)" -ForegroundColor Red
        }
        
        try {
            $types = Invoke-RestMethod -Uri "http://localhost:8080/api/demandes/document-types" -Method Get -Headers $authHeaders -TimeoutSec 10
            Write-Host "✅ Types de documents chargés: $($types.Count)" -ForegroundColor Green
        } catch {
            Write-Host "❌ Erreur types: $($_.Exception.Message)" -ForegroundColor Red
        }
        
        # Test 4: Création de demande
        Write-Host ""
        Write-Host "4. 📝 Test de création de demande..." -ForegroundColor Yellow
        
        $testDemande = @{
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
        
        try {
            $response = Invoke-RestMethod -Uri "http://localhost:8080/api/demandes" -Method Post -Body $testDemande -Headers $authHeaders -TimeoutSec 10
            Write-Host "✅ Demande créée avec succès! ID: $($response.id)" -ForegroundColor Green
        } catch {
            Write-Host "❌ Erreur création demande: $($_.Exception.Message)" -ForegroundColor Red
        }
        
    } else {
        Write-Host "❌ Pas de token reçu" -ForegroundColor Red
    }
    
} catch {
    Write-Host "❌ Erreur d'authentification: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "📋 Résumé des Tests" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Si des tests échouent, vérifiez que:" -ForegroundColor Yellow
Write-Host "1. Le backend est démarré sur le port 8080" -ForegroundColor White
Write-Host "2. La base de données est accessible" -ForegroundColor White
Write-Host "3. Les données de référence existent (civilités, pays, types)" -ForegroundColor White
Write-Host ""
Write-Host "Pour redémarrer le backend: restart_with_cors_fix.bat" -ForegroundColor Green
Write-Host ""

Read-Host "Appuyez sur Entrée pour continuer..."
