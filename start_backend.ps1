# Démarrage Backend eConsulat - PowerShell
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Démarrage Backend eConsulat" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Vérifier si on est dans le bon répertoire
if (-not (Test-Path "backend\pom.xml")) {
    Write-Host "❌ Erreur: Ce script doit être exécuté depuis la racine du projet eConsulat" -ForegroundColor Red
    Write-Host "   Répertoire actuel: $(Get-Location)" -ForegroundColor Red
    Write-Host "   Fichier pom.xml non trouvé dans backend\" -ForegroundColor Red
    Read-Host "Appuyez sur Entrée pour continuer"
    exit 1
}

Write-Host "[1/4] Vérification de l'environnement..." -ForegroundColor Yellow
Write-Host ""

# Vérifier Java
try {
    $javaVersion = java -version 2>&1
    Write-Host "✅ Java trouvé" -ForegroundColor Green
    $javaVersion | Select-String "version" | ForEach-Object { Write-Host "   $_" -ForegroundColor Gray }
} catch {
    Write-Host "❌ Java non trouvé dans le PATH" -ForegroundColor Red
    Write-Host "   Installez Java 17+ et ajoutez-le au PATH" -ForegroundColor Red
    Read-Host "Appuyez sur Entrée pour continuer"
    exit 1
}

# Vérifier Maven
try {
    $mavenVersion = mvn -version 2>&1
    Write-Host "✅ Maven trouvé" -ForegroundColor Green
    $mavenVersion | Select-String "Apache Maven" | ForEach-Object { Write-Host "   $_" -ForegroundColor Gray }
} catch {
    Write-Host "❌ Maven non trouvé dans le PATH" -ForegroundColor Red
    Write-Host "   Installez Maven et ajoutez-le au PATH" -ForegroundColor Red
    Read-Host "Appuyez sur Entrée pour continuer"
    exit 1
}

Write-Host ""
Write-Host "[2/4] Vérification de la base de données..." -ForegroundColor Yellow
Write-Host ""

# Vérifier PostgreSQL
try {
    $pgProcesses = Get-Process -Name "postgres" -ErrorAction SilentlyContinue
    if ($pgProcesses) {
        Write-Host "✅ Processus PostgreSQL trouvés" -ForegroundColor Green
    } else {
        Write-Host "⚠️ Aucun processus PostgreSQL trouvé" -ForegroundColor Yellow
        Write-Host "   Tentative de démarrage de PostgreSQL..." -ForegroundColor Yellow
        
        try {
            Start-Process -FilePath "net" -ArgumentList "start", "postgresql-x64-15" -Verb RunAs -Wait
            Start-Sleep -Seconds 3
            Write-Host "✅ PostgreSQL démarré" -ForegroundColor Green
        } catch {
            Write-Host "❌ Impossible de démarrer PostgreSQL automatiquement" -ForegroundColor Red
            Write-Host "   Démarrez-le manuellement depuis les services Windows" -ForegroundColor Red
        }
    }
} catch {
    Write-Host "⚠️ Erreur lors de la vérification de PostgreSQL" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "[3/4] Nettoyage et compilation..." -ForegroundColor Yellow
Write-Host ""

# Aller dans le répertoire backend
Set-Location backend

# Nettoyer le cache Maven
Write-Host "Nettoyage du cache Maven..." -ForegroundColor Gray
try {
    mvn clean | Out-Null
    Write-Host "✅ Nettoyage réussi" -ForegroundColor Green
} catch {
    Write-Host "⚠️ Erreur lors du nettoyage" -ForegroundColor Yellow
}

# Compiler le projet
Write-Host "Compilation du projet..." -ForegroundColor Gray
try {
    mvn compile | Out-Null
    Write-Host "✅ Compilation réussie" -ForegroundColor Green
} catch {
    Write-Host "❌ Erreur de compilation" -ForegroundColor Red
    Write-Host "   Vérifiez les erreurs ci-dessus" -ForegroundColor Red
    Read-Host "Appuyez sur Entrée pour continuer"
    exit 1
}

Write-Host ""
Write-Host "[4/4] Démarrage du backend..." -ForegroundColor Yellow
Write-Host ""

Write-Host "🚀 Démarrage de Spring Boot..." -ForegroundColor Cyan
Write-Host "   URL: http://127.0.0.1:8080" -ForegroundColor White
Write-Host "   API: http://127.0.0.1:8080/api/demandes/document-types" -ForegroundColor White
Write-Host ""
Write-Host "Appuyez sur Ctrl+C pour arrêter le backend" -ForegroundColor Yellow
Write-Host ""

# Démarrer le backend
try {
    mvn spring-boot:run
} catch {
    Write-Host "❌ Erreur lors du démarrage du backend" -ForegroundColor Red
    Write-Host "   $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""
Write-Host "Backend arrêté." -ForegroundColor Yellow
Read-Host "Appuyez sur Entrée pour continuer"

