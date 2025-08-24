# Test de l'endpoint d'inscription
$uri = "http://localhost:8080/api/auth/register"
$headers = @{
    "Content-Type" = "application/json"
}

$body = @{
    firstName = "Test"
    lastName = "User"
    email = "test@example.com"
    password = "password123"
} | ConvertTo-Json

Write-Host "Test de l'endpoint d'inscription..."
Write-Host "URL: $uri"
Write-Host "Body: $body"

try {
    $response = Invoke-RestMethod -Uri $uri -Method Post -Headers $headers -Body $body
    Write-Host "Succès: $($response | ConvertTo-Json -Depth 3)" -ForegroundColor Green
} catch {
    Write-Host "Erreur HTTP: $($_.Exception.Response.StatusCode.value__)" -ForegroundColor Red
    Write-Host "Message d'erreur: $($_.Exception.Message)" -ForegroundColor Red
    
    if ($_.Exception.Response) {
        $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
        $responseBody = $reader.ReadToEnd()
        Write-Host "Corps de la réponse: $responseBody" -ForegroundColor Red
    }
}
