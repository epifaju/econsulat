# 📄 Guide de Génération PDF avec Filigrane - eConsulat

## 🎯 Vue d'ensemble

Cette fonctionnalité permet de générer automatiquement des documents PDF avec filigrane à partir de modèles Word, selon le type de document demandé (passeport, naissance, certificat, etc.).

## 🔧 Fonctionnalités implémentées

### ✅ Côté Backend

- **Service `PdfDocumentService`** : Génération de documents PDF avec filigrane
- **Contrôleur `PdfDocumentController`** : Endpoints REST pour la génération et téléchargement
- **Support iText 7** : Bibliothèque pour la manipulation PDF
- **Filigrane automatique** : "DOCUMENT OFFICIEL - eCONSULAT" sur toutes les pages
- **Templates dynamiques** : Remplissage automatique des données du citoyen
- **Contrôle d'accès** : Seuls les rôles `ADMIN` et `AGENT` peuvent générer des documents

### ✅ Côté Frontend

- **Bouton "Générer PDF"** : Ajouté dans la colonne Actions de la page "Gestion des Demandes"
- **Interface intuitive** : Icône distincte pour les documents PDF
- **Téléchargement automatique** : Le document se télécharge après génération
- **Notifications** : Retour utilisateur sur le succès/échec de l'opération

## 🚀 Utilisation

### 1. Accès à la fonctionnalité

1. Connectez-vous en tant qu'administrateur ou agent
2. Accédez à la page "Gestion des Demandes"
3. Dans la colonne "Actions", vous verrez deux boutons :
   - 📄 **Générer document Word** (vert)
   - 📝 **Générer document PDF** (bleu)

### 2. Génération d'un document

1. Cliquez sur le bouton "Générer document PDF"
2. Le système va :
   - Récupérer les données de la demande
   - Identifier le type de document
   - Sélectionner le template approprié
   - Remplir les placeholders avec les données
   - Convertir en PDF
   - Ajouter le filigrane
   - Télécharger automatiquement le fichier

### 3. Types de documents supportés

- **Passeport** : `FORMULARIO_DE_PEDIDO_DE_PASSAPORTE.docx`
- **Certificat de naissance** : `ATTESTATION_DE_NAISSANCE.docx`
- **Certificat de coutume** : `CERTIFICAT_DE_COUTUME.docx`
- **Autres** : Template par défaut généré automatiquement

## 🔗 Endpoints API

### Génération de document PDF

```http
POST /api/admin/pdf-documents/generate
Authorization: Bearer {token}
Content-Type: application/json

Parameters:
- demandeId: ID de la demande
- documentTypeId: ID du type de document
```

### Téléchargement de document PDF

```http
GET /api/admin/pdf-documents/download/{documentId}
Authorization: Bearer {token}
```

## 📋 Placeholders supportés

### Informations personnelles

- `{{FIRST_NAME}}` → Prénom
- `{{LAST_NAME}}` → Nom de famille
- `{{BIRTH_DATE}}` → Date de naissance
- `{{BIRTH_PLACE}}` → Lieu de naissance
- `{{BIRTH_COUNTRY}}` → Pays de naissance

### Placeholders en portugais

- `{{Nome}}` → Prénom
- `{{Apelido}}` → Nom de famille
- `{{Data de nascimento}}` → Date de naissance
- `{{Local de nascimento}}` → Lieu de naissance

### Adresse

- `{{ADDRESS}}` → Adresse complète
- `{{CITY}}` → Ville
- `{{POSTAL_CODE}}` → Code postal
- `{{COUNTRY}}` → Pays

### Filiation

- `{{FATHER_FIRST_NAME}}` → Prénom du père
- `{{FATHER_LAST_NAME}}` → Nom du père
- `{{FATHER_BIRTH_DATE}}` → Date de naissance du père
- `{{FATHER_BIRTH_PLACE}}` → Lieu de naissance du père
- `{{FATHER_BIRTH_COUNTRY}}` → Pays de naissance du père
- `{{MOTHER_FIRST_NAME}}` → Prénom de la mère
- `{{MOTHER_LAST_NAME}}` → Nom de la mère
- `{{MOTHER_BIRTH_DATE}}` → Date de naissance de la mère
- `{{MOTHER_BIRTH_PLACE}}` → Lieu de naissance de la mère
- `{{MOTHER_BIRTH_COUNTRY}}` → Pays de naissance de la mère

### Métadonnées

- `{{GENERATION_DATE}}` → Date de génération
- `{{GENERATION_TIME}}` → Heure de génération

## 🧪 Test de la fonctionnalité

### Fichier de test

Ouvrez le fichier `test_pdf_generation.html` dans votre navigateur pour tester la fonctionnalité.

### Instructions de test

1. Assurez-vous que le backend est démarré sur `http://localhost:8080`
2. Connectez-vous en tant qu'admin/agent pour obtenir un token JWT
3. Entrez le token dans le champ "Token JWT"
4. Entrez l'ID d'une demande existante
5. Cliquez sur "Générer Document PDF" pour tester

## 📁 Structure des fichiers

```
backend/
├── src/main/java/com/econsulat/
│   ├── service/
│   │   └── PdfDocumentService.java          # Service de génération PDF
│   ├── controller/
│   │   └── PdfDocumentController.java       # Contrôleur PDF
│   └── resources/
│       └── templates/
│           ├── template_content.txt         # Contenu du template
│           └── default_template.docx        # Template par défaut
├── documents/
│   └── generated/                           # Documents générés
└── pom.xml                                  # Dépendances iText 7

frontend/
└── src/components/
    └── AdminDemandesList.jsx               # Interface avec boutons PDF
```

## 🔒 Sécurité

### Contrôle d'accès

- **Rôles autorisés** : `ADMIN`, `AGENT`
- **Authentification** : Token JWT requis
- **Validation** : Vérification des données de la demande

### Validation des données

- Prénom et nom requis
- Date de naissance requise
- Lieu de naissance requis
- Adresse complète requise
- Informations de filiation requises

## 🐛 Dépannage

### Erreurs courantes

#### "Template non trouvé"

- **Cause** : Le fichier template n'existe pas
- **Solution** : Le système utilise un template par défaut généré automatiquement

#### "Données manquantes"

- **Cause** : Informations incomplètes dans la demande
- **Solution** : Compléter les données de la demande avant génération

#### "Erreur de génération PDF"

- **Cause** : Problème avec iText ou conversion
- **Solution** : Vérifier les logs du serveur et les dépendances

### Logs utiles

```bash
# Vérifier les logs du backend
tail -f backend/logs/application.log

# Vérifier les erreurs Maven
mvn clean compile
```

## 🔄 Améliorations futures

### Fonctionnalités prévues

- [ ] Filigrane avec rotation (45 degrés)
- [ ] Support de templates personnalisés
- [ ] Prévisualisation avant génération
- [ ] Historique des documents générés
- [ ] Signature électronique
- [ ] Compression des PDF

### Optimisations

- [ ] Cache des templates
- [ ] Génération asynchrone
- [ ] Compression des images
- [ ] Support multi-langues

## 📞 Support

Pour toute question ou problème :

1. Consultez les logs du serveur
2. Vérifiez la configuration des dépendances
3. Testez avec le fichier `test_pdf_generation.html`
4. Contactez l'équipe de développement

---

**Version** : 1.0.0  
**Date** : 2024  
**Auteur** : Équipe eConsulat
