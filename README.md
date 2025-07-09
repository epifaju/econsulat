# 🛂 eConsulat - Système de gestion administrative consulaire

Application web permettant de dématérialiser et gérer les demandes administratives consulaires (actes de naissance, passeports, certificats, etc.) entre les citoyens et les agents d'un consulat.

## 🏗️ Architecture

- **Backend** : Spring Boot 3 + Spring Security + JWT
- **Frontend** : React.js + Vite + Bootstrap
- **Base de données** : PostgreSQL
- **PDF** : iText/OpenPDF pour la génération de documents

## 🚀 Installation et lancement

### Prérequis

- **Java 17+** : [Télécharger OpenJDK](https://adoptium.net/)
- **Node.js 18+** : [Télécharger Node.js](https://nodejs.org/)
- **PostgreSQL 12+** : [Télécharger PostgreSQL](https://www.postgresql.org/download/)
- **Maven** : [Télécharger Maven](https://maven.apache.org/download.cgi)

### 🚀 Démarrage rapide

#### Windows

```bash
# Double-cliquer sur le fichier start.bat
# Ou exécuter en ligne de commande :
start.bat
```

#### Linux/Mac

```bash
# Rendre le script exécutable
chmod +x start.sh

# Exécuter le script
./start.sh
```

### 🔧 Installation manuelle

#### 1. Base de données

```bash
# Créer la base de données
createdb -U postgres econsulat

# Initialiser avec le script SQL
psql -U postgres -d econsulat -f econsulat_db.sql
```

#### 2. Backend Spring Boot

```bash
cd backend
mvn clean install
mvn spring-boot:run
```

Le backend sera accessible sur `http://localhost:8080`

#### 3. Frontend React

```bash
cd frontend
npm install
npm run dev
```

Le frontend sera accessible sur `http://localhost:5173`

## 👥 Utilisateurs par défaut

- **Admin** : `admin` / `admin123`
- **Citoyen** : `user` / `user123`

## 🔑 Fonctionnalités

### Portail Citoyen (ROLE: USER)

- Formulaire de demande d'acte
- Upload de documents justificatifs
- Suivi des demandes

### Portail Admin (ROLE: ADMIN)

- Dashboard de toutes les demandes
- Traitement des demandes
- Génération automatique de PDF
- Gestion des documents

## 📁 Structure du projet

```
eConsulat/
├── backend/                 # Application Spring Boot
│   ├── src/
│   ├── pom.xml
│   └── application.properties
├── frontend/               # Application React
│   ├── src/
│   ├── package.json
│   └── vite.config.js
├── econsulat_db.sql        # Script d'initialisation DB
├── econsulat-prd.md        # Spécifications du projet
└── README.md
```

## 🔧 Configuration

### Backend

- Port : 8080
- Base de données : `econsulat`
- Uploads : `/uploads` (dossier local)

### Frontend

- Port : 5173
- API Backend : `http://localhost:8080/api`

## 📝 API Endpoints

### Authentification

- `POST /api/auth/login` - Connexion
- `POST /api/auth/register` - Inscription

### Citoyens

- `POST /api/citizens` - Créer une demande
- `GET /api/citizens` - Lister les demandes (admin)
- `GET /api/citizens/{id}` - Détails d'une demande
- `PUT /api/citizens/{id}` - Traiter une demande (admin)
- `DELETE /api/citizens/{id}` - Supprimer une demande (admin)

### Documents

- `POST /api/documents/upload` - Upload de fichier
- `GET /api/documents/{filename}` - Télécharger un fichier
- `POST /api/documents/generate-pdf` - Générer PDF

## 🛡️ Sécurité

- Authentification JWT
- Rôles ADMIN/USER
- Validation des fichiers uploadés
- CORS configuré pour le développement

## 📄 Génération PDF

Les PDF sont générés automatiquement lors du traitement des demandes par les admins, utilisant les informations du citoyen et les documents justificatifs.
