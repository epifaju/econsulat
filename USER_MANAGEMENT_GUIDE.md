# Guide de Gestion des Utilisateurs - eConsulat

## 🎯 Vue d'ensemble

Le système de gestion des utilisateurs d'eConsulat permet aux administrateurs de créer et gérer les comptes utilisateurs avec validation d'email obligatoire.

## 👥 Rôles disponibles

### ADMIN

- Peut créer tous types d'utilisateurs
- Accès complet à toutes les fonctionnalités
- Gestion des demandes citoyennes
- Gestion des utilisateurs

### AGENT

- Accès limité aux données citoyennes
- Peut traiter les demandes
- Pas d'accès à la gestion des utilisateurs

### USER

- Citoyen / usager
- Peut soumettre des demandes
- Peut consulter ses propres demandes

## 🔐 Processus de création d'utilisateur

### 1. Création par l'administrateur

1. L'admin se connecte avec ses identifiants
2. Accède à "Gestion des utilisateurs" dans le menu
3. Remplit le formulaire de création :
   - Prénom et nom
   - Adresse email
   - Mot de passe
   - Rôle (USER, AGENT, ADMIN)
4. Clique sur "Créer l'utilisateur"

### 2. Validation par email

1. Un email de validation est automatiquement envoyé
2. L'utilisateur reçoit un lien unique avec un token
3. Le token expire après 24 heures
4. L'utilisateur clique sur le lien pour activer son compte

### 3. Activation du compte

1. L'utilisateur est redirigé vers la page de vérification
2. Le système valide le token
3. Le compte est marqué comme "email_verified = true"
4. Un email de bienvenue est envoyé
5. L'utilisateur peut maintenant se connecter

## 🛠️ Fonctionnalités techniques

### Backend (Spring Boot)

#### Entités

- **User** : Modèle complet avec tous les champs requis
- **UserRequest** : DTO pour la création d'utilisateur
- **UserResponse** : DTO pour les réponses utilisateur

#### Services

- **UserService** : Gestion des utilisateurs et validation email
- **EmailService** : Envoi d'emails de validation et de bienvenue
- **EmailVerificationService** : Gestion des tokens de validation

#### Contrôleurs

- **UserController** : API REST pour la gestion des utilisateurs
- **AuthController** : Mise à jour pour la validation email

#### Sécurité

- Seuls les ADMIN peuvent créer des utilisateurs
- Validation d'email obligatoire pour la connexion
- Tokens JWT pour l'authentification

### Frontend (React)

#### Composants

- **UserManagement** : Interface d'administration des utilisateurs
- **EmailVerification** : Page de vérification d'email
- **Login** : Mise à jour pour utiliser email

#### Fonctionnalités

- Formulaire de création d'utilisateur
- Liste des utilisateurs avec statuts
- Suppression d'utilisateurs
- Navigation sécurisée selon les rôles

## 📧 Configuration Email

### Prérequis

- Service SMTP configuré (Gmail, Outlook, etc.)
- Authentification à 2 facteurs activée (pour Gmail)
- Mot de passe d'application généré

### Configuration

1. Modifiez `backend/src/main/resources/application.properties`
2. Ajoutez vos identifiants SMTP
3. Consultez `EMAIL_CONFIG.md` pour les détails

### Test

1. Redémarrez le backend
2. Créez un nouvel utilisateur
3. Vérifiez la réception de l'email

## 🗄️ Base de données

### Structure de la table users

```sql
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) CHECK (role IN ('USER', 'ADMIN', 'AGENT')) NOT NULL DEFAULT 'USER',
    email_verified BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Migration

1. Exécutez le script `update_users_table.sql`
2. Les données existantes sont sauvegardées automatiquement
3. Les nouveaux champs sont ajoutés

## 🔒 Sécurité

### Authentification

- Mots de passe hashés avec BCrypt
- Tokens JWT pour les sessions
- Validation d'email obligatoire

### Autorisation

- Contrôle d'accès basé sur les rôles
- Seuls les ADMIN peuvent créer des utilisateurs
- Protection des routes sensibles

### Validation

- Validation des emails
- Tokens de validation sécurisés
- Expiration automatique des tokens

## 🚀 Démarrage

### Script automatique

```bash
# Windows
start_with_users.bat

# Linux/Mac
./start_with_users.sh
```

### Manuel

1. Mettez à jour la base de données
2. Configurez l'email SMTP
3. Démarrez le backend
4. Démarrez le frontend

## 📝 Utilisation

### Pour les administrateurs

1. Connectez-vous avec admin@econsulat.com / admin123
2. Accédez à "Gestion des utilisateurs"
3. Créez de nouveaux utilisateurs
4. Gérez les comptes existants

### Pour les nouveaux utilisateurs

1. Recevez l'email de validation
2. Cliquez sur le lien de validation
3. Connectez-vous avec vos identifiants
4. Accédez à vos fonctionnalités

## 🐛 Dépannage

### Problèmes courants

- **Email non reçu** : Vérifiez la configuration SMTP
- **Token expiré** : L'admin doit recréer l'utilisateur
- **Erreur de connexion** : Vérifiez que l'email est validé
- **Accès refusé** : Vérifiez les permissions de rôle

### Logs

- Vérifiez les logs du backend pour les erreurs
- Les erreurs d'email sont loggées
- Les validations de tokens sont tracées

## 🔄 Maintenance

### Nettoyage des tokens

- Les tokens expirés sont automatiquement supprimés
- Pas de maintenance manuelle requise

### Sauvegarde

- Sauvegardez régulièrement la table users
- Les mots de passe sont hashés de manière sécurisée

### Mise à jour

- Suivez les mises à jour de sécurité
- Testez les nouvelles fonctionnalités
- Sauvegardez avant les mises à jour
