# Guide d'utilisation - Interface Administrateur eConsulat

## 📋 Table des matières

1. [Installation et configuration](#installation-et-configuration)
2. [Connexion à l'interface admin](#connexion-à-linterface-admin)
3. [Tableau de bord - Statistiques](#tableau-de-bord---statistiques)
4. [Gestion des demandes](#gestion-des-demandes)
5. [Gestion des utilisateurs](#gestion-des-utilisateurs)
6. [Gestion des types de documents](#gestion-des-types-de-documents)
7. [Génération de documents](#génération-de-documents)
8. [Dépannage](#dépannage)

---

## 🚀 Installation et configuration

### Prérequis

- PostgreSQL installé et en cours d'exécution
- Java 17+ installé
- Node.js 16+ installé
- Maven installé

### Configuration de la base de données

1. Exécutez le script de configuration :

   ```bash
   setup_admin_interface.bat
   ```

2. Ce script va :
   - Créer les tables nécessaires (`document_types`, `generated_documents`)
   - Insérer les types de documents par défaut
   - Créer un compte agent de test

### Démarrage de l'application

1. **Backend** (dans le dossier `backend`) :

   ```bash
   mvn spring-boot:run
   ```

2. **Frontend** (dans le dossier `frontend`) :
   ```bash
   npm run dev
   ```

---

## 🔐 Connexion à l'interface admin

### Comptes de test

- **Administrateur** : `admin@econsulat.com` / `admin123`
- **Agent** : `agent@econsulat.com` / `agent123`

### Accès

1. Ouvrez votre navigateur
2. Allez sur `http://localhost:5173`
3. Connectez-vous avec un compte administrateur
4. Vous serez automatiquement redirigé vers l'interface admin

---

## 📊 Tableau de bord - Statistiques

### Vue d'ensemble

Le tableau de bord affiche :

- **Total des demandes** : Nombre total de demandes dans le système
- **Demandes en attente** : Demandes avec statut "En attente"
- **Demandes approuvées** : Demandes avec statut "Approuvé"
- **Demandes rejetées** : Demandes avec statut "Rejeté"
- **Demandes terminées** : Demandes avec statut "Terminé"
- **Total utilisateurs** : Nombre d'utilisateurs inscrits
- **Documents générés** : Nombre de documents officiels générés

### Graphiques

- **Répartition des demandes** : Graphique en barres montrant la distribution par statut
- **Activité récente** : Résumé des activités du système

---

## 📝 Gestion des demandes

### Consultation des demandes

1. Cliquez sur l'onglet **"Gestion des Demandes"**
2. Les demandes sont affichées dans un tableau avec :
   - Nom et prénom du demandeur
   - Type de document demandé
   - Statut actuel
   - Date de création
   - Actions disponibles

### Filtres et recherche

- **Recherche** : Tapez un nom pour filtrer les demandes
- **Filtre par statut** : Sélectionnez un statut spécifique
- **Tri** : Cliquez sur les en-têtes de colonnes pour trier

### Actions sur les demandes

- **Voir détails** : Cliquez sur l'icône 👁️ pour voir les informations complètes
- **Changer le statut** : Utilisez le menu déroulant pour modifier le statut
- **Générer document** : Cliquez sur l'icône 📄 pour générer un document officiel

### Statuts disponibles

- **En attente** : Demande soumise, en attente de traitement
- **Approuvé** : Demande validée par l'administrateur
- **Rejeté** : Demande refusée
- **Terminé** : Document généré et livré

---

## 👥 Gestion des utilisateurs

### Consultation des utilisateurs

1. Cliquez sur l'onglet **"Gestion des Utilisateurs"**
2. Le tableau affiche :
   - Nom et prénom
   - Adresse email
   - Rôle dans le système
   - Statut de vérification email
   - Nombre de demandes
   - Date d'inscription

### Création d'un nouvel utilisateur

1. Cliquez sur **"Nouvel utilisateur"**
2. Remplissez le formulaire :
   - **Prénom** et **Nom** (obligatoires)
   - **Email** (obligatoire, doit être unique)
   - **Mot de passe** (obligatoire)
   - **Rôle** : Utilisateur, Agent, Administrateur ou Citoyen
   - **Email vérifié** : Cochez si l'email est déjà vérifié

### Modification d'un utilisateur

1. Cliquez sur l'icône ✏️ à côté de l'utilisateur
2. Modifiez les champs souhaités
3. **Mot de passe** : Laissez vide pour ne pas le changer
4. Cliquez sur **"Mettre à jour"**

### Suppression d'un utilisateur

1. Cliquez sur l'icône 🗑️ à côté de l'utilisateur
2. Confirmez la suppression

### Rôles disponibles

- **Utilisateur** : Peut soumettre des demandes
- **Agent** : Peut traiter les demandes et générer des documents
- **Administrateur** : Accès complet à toutes les fonctionnalités
- **Citoyen** : Utilisateur avec accès limité

---

## 📄 Gestion des types de documents

### Consultation des types

1. Cliquez sur l'onglet **"Types de Documents"**
2. Les types sont affichés sous forme de cartes avec :
   - Libellé du type
   - Description
   - Chemin du template
   - Statut (actif/inactif)
   - Date de création

### Création d'un nouveau type

1. Cliquez sur **"Nouveau type"**
2. Remplissez le formulaire :
   - **Libellé** : Nom du type de document (obligatoire)
   - **Description** : Description détaillée (optionnel)
   - **Chemin du template** : Chemin vers le fichier Word template (optionnel)
   - **Actif** : Cochez pour rendre le type disponible

### Modification d'un type

1. Cliquez sur l'icône ✏️ sur la carte du type
2. Modifiez les champs souhaités
3. Cliquez sur **"Mettre à jour"**

### Activation/Désactivation

- **Désactiver** : Cliquez sur l'icône 🗑️ (le type devient inactif)
- **Activer** : Cliquez sur l'icône ✅ pour réactiver un type inactif

---

## 🖨️ Génération de documents

### Génération d'un document

1. Dans la liste des demandes, cliquez sur l'icône 📄
2. Le système va :
   - Récupérer les données de la demande
   - Utiliser le template Word correspondant
   - Remplir automatiquement les champs
   - Ajouter le filigrane officiel
   - Convertir en PDF
   - Télécharger automatiquement le document

### Filigrane officiel

Tous les documents générés incluent le filigrane :

> "Document émis par le eConsulat - République de Guinée-Bissau"

### Templates disponibles

- **Passeport** : `templates/passeport_template.docx`
- **Acte de naissance** : `templates/acte_naissance_template.docx`
- **Certificat de mariage** : `templates/certificat_mariage_template.docx`
- **Carte d'identité** : `templates/carte_identite_template.docx`
- **Certificat d'hébergement** : `templates/certificat_hebergement_template.docx`
- **Certificat de célibat** : `templates/certificat_celibat_template.docx`
- **Certificat de résidence** : `templates/certificat_residence_template.docx`

### Placeholders disponibles

Les templates peuvent utiliser ces placeholders :

- `{{FIRST_NAME}}` : Prénom du demandeur
- `{{LAST_NAME}}` : Nom du demandeur
- `{{BIRTH_DATE}}` : Date de naissance
- `{{BIRTH_PLACE}}` : Lieu de naissance
- `{{BIRTH_COUNTRY}}` : Pays de naissance
- `{{ADDRESS}}` : Adresse
- `{{CITY}}` : Ville
- `{{POSTAL_CODE}}` : Code postal
- `{{COUNTRY}}` : Pays
- `{{FATHER_FIRST_NAME}}` : Prénom du père
- `{{FATHER_LAST_NAME}}` : Nom du père
- `{{MOTHER_FIRST_NAME}}` : Prénom de la mère
- `{{MOTHER_LAST_NAME}}` : Nom de la mère
- `{{GENERATION_DATE}}` : Date de génération
- `{{GENERATION_TIME}}` : Heure de génération

---

## 🔧 Dépannage

### Problèmes courants

#### L'interface admin ne s'affiche pas

- Vérifiez que vous êtes connecté avec un compte administrateur
- Vérifiez que le backend est démarré sur le port 8080
- Vérifiez que le frontend est démarré sur le port 5173

#### Erreur de connexion à la base de données

- Vérifiez que PostgreSQL est en cours d'exécution
- Vérifiez les paramètres de connexion dans `application.properties`
- Exécutez le script `setup_admin_interface.bat`

#### Impossible de générer un document

- Vérifiez que le template Word existe
- Vérifiez que les données de la demande sont complètes
- Vérifiez les permissions d'écriture dans le dossier `documents`

#### Erreur 403 - Accès interdit

- Vérifiez que votre compte a le rôle "ADMIN"
- Vérifiez que le token JWT est valide
- Essayez de vous reconnecter

### Logs utiles

- **Backend** : Consultez les logs dans la console où vous avez lancé `mvn spring-boot:run`
- **Frontend** : Ouvrez les outils de développement (F12) et consultez l'onglet Console

### Support

Pour toute question ou problème, consultez :

1. Les logs d'erreur
2. La documentation technique
3. Le guide de dépannage général du projet

---

## 📞 Contact

Pour toute assistance supplémentaire, contactez l'équipe de développement eConsulat.
