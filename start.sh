#!/bin/bash

echo "========================================"
echo "   🛂 eConsulat - Démarrage"
echo "========================================"
echo

echo "📊 Vérification des prérequis..."
echo

# Vérifier Java
if ! command -v java &> /dev/null; then
    echo "❌ Java n'est pas installé ou n'est pas dans le PATH"
    echo "   Veuillez installer Java 17+ et redémarrer"
    exit 1
fi
echo "✅ Java détecté"

# Vérifier Node.js
if ! command -v node &> /dev/null; then
    echo "❌ Node.js n'est pas installé ou n'est pas dans le PATH"
    echo "   Veuillez installer Node.js 18+ et redémarrer"
    exit 1
fi
echo "✅ Node.js détecté"

# Vérifier PostgreSQL
if ! command -v psql &> /dev/null; then
    echo "❌ PostgreSQL n'est pas installé ou n'est pas dans le PATH"
    echo "   Veuillez installer PostgreSQL et redémarrer"
    exit 1
fi
echo "✅ PostgreSQL détecté"

echo
echo "🗄️  Initialisation de la base de données..."
echo "   Création de la base 'econsulat' si elle n'existe pas..."
createdb -U postgres econsulat 2>/dev/null || echo "   Base de données 'econsulat' existe déjà ou erreur de connexion"

echo "   Application du script SQL..."
psql -U postgres -d econsulat -f econsulat_db.sql >/dev/null 2>&1
echo "✅ Base de données initialisée"

echo
echo "🚀 Démarrage du backend Spring Boot..."
echo "   Port: http://localhost:8080"
cd backend && mvn spring-boot:run &
BACKEND_PID=$!

echo
echo "⏳ Attente du démarrage du backend..."
sleep 10

echo
echo "🎨 Démarrage du frontend React..."
echo "   Port: http://localhost:5173"
cd ../frontend && npm install && npm run dev &
FRONTEND_PID=$!

echo
echo "========================================"
echo "   ✅ eConsulat démarré avec succès!"
echo "========================================"
echo
echo "📱 Frontend: http://localhost:5173"
echo "🔧 Backend:  http://localhost:8080"
echo
echo "👥 Comptes de test:"
echo "   Admin:  admin / admin123"
echo "   Citoyen: user / user123"
echo
echo "Appuyez sur Ctrl+C pour arrêter l'application..."

# Fonction de nettoyage
cleanup() {
    echo
    echo "🛑 Arrêt de l'application..."
    kill $BACKEND_PID 2>/dev/null
    kill $FRONTEND_PID 2>/dev/null
    exit 0
}

# Capturer Ctrl+C
trap cleanup SIGINT

# Attendre indéfiniment
wait 