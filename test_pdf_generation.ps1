# Test de génération PDF avec authentification
Write-Host "=== Test de Génération PDF ===" -ForegroundColor Green

# 1. Authentification
Write-Host "1. Authentification admin..." -ForegroundColor Yellow
$loginBody = @{
    email = "admin@econsulat.com"
    password = "admin123"
} | ConvertTo-Json

try {
    $loginResponse = Invoke-WebRequest -Uri "http://localhost:8080/api/auth/login" -Method POST -ContentType "application/json" -Body $loginBody
    $loginData = $loginResponse.Content | ConvertFrom-Json
    $token = $loginData.token
    Write-Host "✅ Authentification réussie" -ForegroundColor Green
} catch {
    Write-Host "❌ Erreur d'authentification: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# 2. Génération PDF
Write-Host "2. Génération PDF..." -ForegroundColor Yellow
$headers = @{
    "Authorization" = "Bearer $token"
    "Content-Type" = "application/json"
}

try {
    $generateResponse = Invoke-WebRequest -Uri "http://localhost:8080/api/admin/pdf-documents/generate?demandeId=1&documentTypeId=1" -Method POST -Headers $headers
    $generateData = $generateResponse.Content | ConvertFrom-Json
    Write-Host "✅ PDF généré avec succès" -ForegroundColor Green
    Write-Host "   ID: $($generateData.id)" -ForegroundColor Cyan
    Write-Host "   Fichier: $($generateData.fileName)" -ForegroundColor Cyan
    Write-Host "   Statut: $($generateData.status)" -ForegroundColor Cyan
} catch {
    Write-Host "❌ Erreur de génération: $($_.Exception.Message)" -ForegroundColor Red
    if ($_.Exception.Response) {
        $errorContent = $_.Exception.Response.GetResponseStream()
        $reader = New-Object System.IO.StreamReader($errorContent)
        $errorText = $reader.ReadToEnd()
        Write-Host "   Détails: $errorText" -ForegroundColor Red
    }
    exit 1
}

# 3. Téléchargement PDF
Write-Host "3. Téléchargement PDF..." -ForegroundColor Yellow
try {
    $downloadResponse = Invoke-WebRequest -Uri "http://localhost:8080/api/admin/pdf-documents/download/1" -Method GET -Headers $headers
    $pdfBytes = $downloadResponse.Content
    $pdfSize = $pdfBytes.Length
    Write-Host "✅ PDF téléchargé avec succès" -ForegroundColor Green
    Write-Host "   Taille: $pdfSize bytes" -ForegroundColor Cyan
    Write-Host "   Type: $($downloadResponse.Headers['Content-Type'])" -ForegroundColor Cyan
    
    # Sauvegarder le PDF
    $pdfPath = "test_document.pdf"
    [System.IO.File]::WriteAllBytes($pdfPath, $pdfBytes)
    Write-Host "   Sauvegardé dans: $pdfPath" -ForegroundColor Cyan
    
} catch {
    Write-Host "❌ Erreur de téléchargement: $($_.Exception.Message)" -ForegroundColor Red
    if ($_.Exception.Response) {
        $errorContent = $_.Exception.Response.GetResponseStream()
        $reader = New-Object System.IO.StreamReader($errorContent)
        $errorText = $reader.ReadToEnd()
        Write-Host "   Détails: $errorText" -ForegroundColor Red
    }
    exit 1
}

Write-Host "=== Test terminé avec succès ===" -ForegroundColor Green 