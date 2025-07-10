# 📋 Guide - Actions Utilisateur

## 🎯 Vue d'ensemble

Les utilisateurs peuvent maintenant consulter les détails de leurs demandes et générer des documents directement depuis leur tableau de bord. Cette fonctionnalité améliore l'expérience utilisateur en permettant un accès direct aux informations et aux documents.

## ✨ Nouvelles fonctionnalités

### 1. 👁️ Consultation des détails

- **Bouton "Voir les détails"** : Disponible pour toutes les demandes
- **Modal détaillé** : Affiche toutes les informations de la demande
- **Sections organisées** :
  - Informations générales
  - Informations personnelles
  - Adresse
  - Filiation (père/mère)
  - Documents joints

### 2. 📄 Génération de document

- **Bouton "Générer document"** : Visible uniquement pour les demandes approuvées
- **Téléchargement automatique** : Le document est généré et téléchargé automatiquement
- **Indicateur de progression** : Animation pendant la génération
- **Gestion d'erreurs** : Messages d'erreur clairs en cas de problème

## 🎨 Interface utilisateur

### Tableau des demandes

```
┌─────────────────────────────────────────────────────────────┐
│ 📋 Mes demandes                                    Actualiser │
├─────────────────────────────────────────────────────────────┤
│ 📄 Passeport - APPROVED                              ACTIONS │
│ Demandeur: Jean Dupont                                👁️ Voir │
│ Soumis le: 10 juillet 2025                            📄 Générer │
│ Dernière mise à jour: 10 juillet 2025                      │
│ Numéro de demande: #1                                      │
└─────────────────────────────────────────────────────────────┘
```

### Modal de détails

```
┌─────────────────────────────────────────────────────────────┐
│ Détails de la demande #1                              [×] │
├─────────────────────────────────────────────────────────────┤
│ 📊 Informations générales                                  │
│ Type de document: Passeport                               │
│ Statut: [APPROVED]                                         │
│ Date de création: 10 juillet 2025                          │
│                                                             │
│ 👤 Informations personnelles                               │
│ Nom: Dupont                                                │
│ Prénom: Jean                                               │
│ Date de naissance: 15/03/1980                              │
│                                                             │
│ 🏠 Adresse                                                  │
│ Rue: 123 Avenue de la République                           │
│ Ville: Paris                                               │
│ Code postal: 75001                                         │
│                                                             │
│ [Fermer] [Générer document]                                │
└─────────────────────────────────────────────────────────────┘
```

## 🔧 Fonctionnalités techniques

### Consultation des détails

```javascript
// Endpoint utilisé
GET /api/demandes/{id}

// Headers requis
Authorization: Bearer {token}

// Réponse
{
  "id": 1,
  "firstName": "Jean",
  "lastName": "Dupont",
  "documentTypeDisplay": "Passeport",
  "statusDisplay": "Approuvé",
  "birthDate": "1980-03-15",
  "birthPlace": "Paris",
  "birthCountry": "France",
  "civilite": "M.",
  "adresse": {
    "streetNumber": "123",
    "streetName": "Avenue de la République",
    "city": "Paris",
    "postalCode": "75001",
    "country": "France"
  },
  "fatherFirstName": "Pierre",
  "fatherLastName": "Dupont",
  "motherFirstName": "Marie",
  "motherLastName": "Martin"
}
```

### Génération de document

```javascript
// 1. Générer le document
POST /api/admin/documents/generate?demandeId={id}&documentTypeId=1

// 2. Télécharger le document
GET /api/admin/documents/download/{documentId}

// Réponse de génération
{
  "id": 1,
  "fileName": "passeport_jean_dupont.docx",
  "fileSize": 24576,
  "status": "GENERATED"
}
```

## 🚀 Utilisation

### Pour consulter les détails d'une demande

1. Connectez-vous à votre compte utilisateur
2. Accédez à la page "Mes demandes"
3. Cliquez sur le bouton **"Voir les détails"** à côté de la demande souhaitée
4. Un modal s'ouvre avec toutes les informations détaillées
5. Cliquez sur **"Fermer"** pour fermer le modal

### Pour générer un document

1. Assurez-vous que votre demande est **approuvée** (statut "APPROVED")
2. Cliquez sur le bouton **"Générer document"** à côté de la demande
3. Le système génère le document (animation de progression)
4. Le document est automatiquement téléchargé
5. Un message de confirmation s'affiche

## ⚠️ Conditions et limitations

### Consultation des détails

- ✅ Disponible pour toutes les demandes
- ✅ Nécessite une authentification valide
- ✅ L'utilisateur ne peut voir que ses propres demandes

### Génération de document

- ✅ Disponible uniquement pour les demandes **approuvées**
- ✅ Nécessite une authentification valide
- ✅ Le document est généré au format Word (.docx)
- ❌ Non disponible pour les demandes en attente ou rejetées

## 🔍 Dépannage

### Problème : "Erreur lors du chargement des détails"

**Solutions :**

1. Vérifiez votre connexion internet
2. Reconnectez-vous à l'application
3. Actualisez la page

### Problème : "Erreur lors de la génération"

**Solutions :**

1. Vérifiez que la demande est bien approuvée
2. Attendez quelques secondes et réessayez
3. Contactez l'administrateur si le problème persiste

### Problème : Le document ne se télécharge pas

**Solutions :**

1. Vérifiez les paramètres de téléchargement de votre navigateur
2. Désactivez temporairement les bloqueurs de popup
3. Vérifiez l'espace disque disponible

## 🧪 Test des fonctionnalités

Utilisez le fichier `test_user_actions.html` pour tester les nouvelles fonctionnalités :

1. Ouvrez `test_user_actions.html` dans votre navigateur
2. Collez votre token JWT utilisateur
3. Cliquez sur "Tester les actions utilisateur"
4. Vérifiez que toutes les fonctionnalités fonctionnent correctement

## 📝 Notes de développement

### Frontend (React)

- Composant : `DemandesList.jsx`
- État local pour le modal et la génération
- Gestion des erreurs avec try/catch
- Téléchargement automatique des fichiers

### Backend (Spring Boot)

- Endpoint de consultation : `DemandeController.getDemandeById()`
- Endpoint de génération : `DocumentGenerationController.generateDocument()`
- Endpoint de téléchargement : `DocumentGenerationController.downloadDocument()`

### Sécurité

- Authentification JWT requise
- Vérification des permissions utilisateur
- L'utilisateur ne peut accéder qu'à ses propres demandes

## 🎯 Améliorations futures

- [ ] Historique des documents générés
- [ ] Prévisualisation des documents avant téléchargement
- [ ] Notifications par email lors de la génération
- [ ] Support de formats supplémentaires (PDF)
- [ ] Génération en lot pour plusieurs demandes
