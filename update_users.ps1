# Script pour mettre à jour les utilisateurs dans PostgreSQL
Write-Host "🔄 Mise à jour des utilisateurs dans la base de données..." -ForegroundColor Yellow

# Chemin vers psql (essayer différentes versions)
$psqlPaths = @(
    "C:\Program Files\PostgreSQL\13\bin\psql.exe",
    "C:\Program Files\PostgreSQL\14\bin\psql.exe", 
    "C:\Program Files\PostgreSQL\15\bin\psql.exe",
    "C:\Program Files\PostgreSQL\16\bin\psql.exe"
)

$psqlPath = $null
foreach ($path in $psqlPaths) {
    if (Test-Path $path) {
        $psqlPath = $path
        Write-Host "✅ PostgreSQL trouvé: $path" -ForegroundColor Green
        break
    }
}

if (-not $psqlPath) {
    Write-Host "❌ PostgreSQL non trouvé. Vérifiez l'installation." -ForegroundColor Red
    exit 1
}

# Exécuter le script SQL
Write-Host "📝 Exécution du script SQL..." -ForegroundColor Yellow
& $psqlPath -U postgres -d econsulat -f "..\check_users.sql"

if ($LASTEXITCODE -eq 0) {
    Write-Host "✅ Utilisateurs mis à jour avec succès!" -ForegroundColor Green
    Write-Host "🔑 Comptes disponibles:" -ForegroundColor Cyan
    Write-Host "   - Admin: admin / admin123" -ForegroundColor White
    Write-Host "   - User: user / user123" -ForegroundColor White
} else {
    Write-Host "❌ Erreur lors de la mise à jour des utilisateurs" -ForegroundColor Red
}

Write-Host "`n🚀 Le backend devrait maintenant être prêt!" -ForegroundColor Green
Write-Host "📱 Testez la connexion sur: http://localhost:5173" -ForegroundColor Cyan 