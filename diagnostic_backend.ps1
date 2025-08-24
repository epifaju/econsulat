# Diagnostic Backend eConsulat - PowerShell
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Diagnostic Backend eConsulat" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# 1. Vérifier les processus Java
Write-Host "[1/5] Vérification des processus Java..." -ForegroundColor Yellow
$javaProcesses = Get-Process -Name "java" -ErrorAction SilentlyContinue
if ($javaProcesses) {
    Write-Host "✅ Processus Java trouvés:" -ForegroundColor Green
    $javaProcesses | ForEach-Object {
        Write-Host "   PID: $($_.Id), CPU: $([math]::Round($_.CPU, 2))s, Mémoire: $([math]::Round($_.WorkingSet64/1MB, 2)) MB" -ForegroundColor Gray
    }
} else {
    Write-Host "❌ Aucun processus Java trouvé" -ForegroundColor Red
    Write-Host "   Le backend Spring Boot n'est probablement pas démarré" -ForegroundColor Red
}

Write-Host ""

# 2. Vérifier les ports utilisés
Write-Host "[2/5] Vérification des ports..." -ForegroundColor Yellow
$port8080 = Get-NetTCPConnection -LocalPort 8080 -ErrorAction SilentlyContinue
if ($port8080) {
    Write-Host "✅ Port 8080 utilisé par:" -ForegroundColor Green
    Write-Host "   PID: $($port8080.OwningProcess), État: $($port8080.State)" -ForegroundColor Gray
} else {
    Write-Host "❌ Port 8080 non utilisé" -ForegroundColor Red
    Write-Host "   Le backend n'écoute pas sur le port 8080" -ForegroundColor Red
}

Write-Host ""

# 3. Test de connexion au backend
Write-Host "[3/5] Test de connexion au backend..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "http://127.0.0.1:8080/api/demandes/document-types" -Method GET -TimeoutSec 10 -ErrorAction Stop
    Write-Host "✅ Backend accessible!" -ForegroundColor Green
    Write-Host "   Status: $($response.StatusCode)" -ForegroundColor Gray
    Write-Host "   Réponse: $($response.Content.Length) caractères" -ForegroundColor Gray
} catch {
    Write-Host "❌ Backend non accessible" -ForegroundColor Red
    Write-Host "   Erreur: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""

# 4. Vérifier la base de données
Write-Host "[4/5] Vérification de la base de données..." -ForegroundColor Yellow
try {
    # Vérifier si PostgreSQL est installé et accessible
    $pgProcesses = Get-Process -Name "postgres" -ErrorAction SilentlyContinue
    if ($pgProcesses) {
        Write-Host "✅ Processus PostgreSQL trouvés:" -ForegroundColor Green
        $pgProcesses | ForEach-Object {
            Write-Host "   PID: $($_.Id), Mémoire: $([math]::Round($_.WorkingSet64/1MB, 2)) MB" -ForegroundColor Gray
        }
        
        # Tester la connexion à la base
        Write-Host "   Test de connexion à la base..." -ForegroundColor Gray
        $env:PGPASSWORD = "postgres"
        $testQuery = psql -h localhost -p 5432 -U postgres -d econsulat -t -c "SELECT 1 as test;" 2>&1
        if ($LASTEXITCODE -eq 0) {
            Write-Host "   ✅ Connexion à la base réussie" -ForegroundColor Green
        } else {
            Write-Host "   ❌ Connexion à la base échouée" -ForegroundColor Red
        }
    } else {
        Write-Host "❌ Aucun processus PostgreSQL trouvé" -ForegroundColor Red
    }
} catch {
    Write-Host "❌ Erreur lors de la vérification de la base" -ForegroundColor Red
    Write-Host "   $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""

# 5. Recommandations
Write-Host "[5/5] Recommandations..." -ForegroundColor Yellow
if (-not $javaProcesses) {
    Write-Host "🚀 Démarrer le backend:" -ForegroundColor Cyan
    Write-Host "   cd backend" -ForegroundColor White
    Write-Host "   mvn spring-boot:run" -ForegroundColor White
    Write-Host ""
}

if (-not $port8080) {
    Write-Host "🔌 Vérifier la configuration du port dans application.properties" -ForegroundColor Cyan
    Write-Host ""
}

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Diagnostic terminé" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

# Attendre l'entrée utilisateur
Write-Host ""
Write-Host "Appuyez sur une touche pour continuer..." -ForegroundColor Yellow
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")

