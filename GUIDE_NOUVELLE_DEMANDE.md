# Guide d'utilisation - Nouvelle demande eConsulat

## 🎯 Vue d'ensemble

La fonctionnalité "Nouvelle demande" permet aux utilisateurs connectés avec le rôle `USER` de soumettre des demandes de documents administratifs via un formulaire multistep moderne et intuitif.

## 🚀 Installation et configuration

### 1. Base de données

Exécutez le script de configuration de la base de données :

```bash
# Windows
setup_demandes_db.bat

# Linux/Mac
psql -h localhost -p 5432 -U postgres -d econsulat -f backend/src/main/resources/schema_demandes.sql
```

### 2. Backend

Le backend Spring Boot inclut maintenant :

- Nouvelles entités : `Civilite`, `Pays`, `Adresse`, `Demande`
- Nouveaux repositories et services
- Nouveau contrôleur `DemandeController`
- API REST complète pour les demandes

### 3. Frontend

Le frontend React inclut maintenant :

- Composant principal `NewDemandeForm`
- 6 étapes de formulaire modulaires
- Composant `DemandesList` pour afficher les demandes
- Intégration dans le `UserDashboard`

## 📋 Étapes du formulaire

### Étape 1 : Informations personnelles

- **Civilité** : Monsieur, Madame, Mademoiselle
- **Prénom** : Obligatoire
- **Nom de famille** : Obligatoire
- **Date de naissance** : Obligatoire
- **Lieu de naissance** : Obligatoire
- **Pays de naissance** : Liste déroulante (100+ pays)

### Étape 2 : Adresse

- **Nom de rue** : Obligatoire
- **Numéro** : Obligatoire
- **Boîte** : Facultatif
- **Code postal** : Obligatoire
- **Ville** : Obligatoire
- **Pays** : Liste déroulante

### Étape 3 : Filiation (parents)

**Père :**

- Prénom, Nom, Date de naissance, Lieu de naissance, Pays de naissance

**Mère :**

- Prénom, Nom, Date de naissance, Lieu de naissance, Pays de naissance

### Étape 4 : Type de document

Types disponibles :

- Passeport
- Acte de naissance
- Certificat de mariage
- Carte d'identité
- Autre

### Étape 5 : Upload de documents

- Formats acceptés : PDF, JPG, PNG
- Taille maximale : 10MB par fichier
- Drag & drop ou sélection de fichiers
- Aperçu et gestion des fichiers

### Étape 6 : Récapitulatif

- Affichage de toutes les informations saisies
- Validation finale avant soumission
- Bouton de soumission

## 🔧 API Endpoints

### Demandes

- `POST /api/demandes` - Créer une nouvelle demande
- `GET /api/demandes/my` - Récupérer les demandes de l'utilisateur
- `GET /api/demandes/{id}` - Récupérer une demande spécifique
- `GET /api/demandes/all` - Récupérer toutes les demandes (admin)

### Données de référence

- `GET /api/demandes/civilites` - Liste des civilités
- `GET /api/demandes/pays` - Liste des pays
- `GET /api/demandes/document-types` - Types de documents

## 💾 Structure de la base de données

### Tables créées

1. **civilites** : Civilités (Monsieur, Madame, Mademoiselle)
2. **pays** : Liste des pays (100+ pays)
3. **adresses** : Adresses des demandeurs
4. **demandes** : Demandes principales avec toutes les informations

### Relations

- `demandes` → `civilites` (ManyToOne)
- `demandes` → `pays` (ManyToOne pour pays de naissance)
- `demandes` → `adresses` (OneToOne)
- `demandes` → `users` (ManyToOne)

## 🎨 Interface utilisateur

### Design

- Interface moderne avec Tailwind CSS
- Formulaire multistep avec barre de progression
- Validation en temps réel
- Messages d'erreur contextuels
- Design responsive (mobile-friendly)

### Fonctionnalités UX

- Navigation entre les étapes
- Sauvegarde automatique des données
- Validation étape par étape
- Aperçu des fichiers uploadés
- Récapitulatif complet avant soumission

## 🔐 Sécurité

### Authentification

- Toutes les routes nécessitent une authentification JWT
- Vérification du rôle utilisateur
- Protection CSRF

### Validation

- Validation côté client (React)
- Validation côté serveur (Spring Boot)
- Sanitisation des données
- Validation des fichiers uploadés

## 📊 Gestion des demandes

### Statuts

- **PENDING** : En attente
- **APPROVED** : Approuvé
- **REJECTED** : Rejeté
- **COMPLETED** : Terminé

### Suivi

- Numéro de demande unique
- Horodatage de création et modification
- Historique des statuts
- Notifications par email (à implémenter)

## 🚀 Utilisation

### Pour l'utilisateur

1. Se connecter avec un compte `USER`
2. Cliquer sur "Nouvelle demande" dans le dashboard
3. Remplir le formulaire étape par étape
4. Uploader les documents justificatifs
5. Vérifier le récapitulatif
6. Soumettre la demande

### Pour l'administrateur

1. Se connecter avec un compte `ADMIN`
2. Accéder à la liste de toutes les demandes
3. Gérer les statuts des demandes
4. Télécharger les documents générés

## 🔧 Configuration

### Variables d'environnement

```properties
# Base de données
spring.datasource.url=jdbc:postgresql://localhost:5432/econsulat
spring.datasource.username=postgres
spring.datasource.password=postgres

# Upload de fichiers
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=50MB
```

### Personnalisation

- Ajouter de nouveaux types de documents dans `Demande.DocumentType`
- Modifier la liste des pays dans `schema_demandes.sql`
- Personnaliser les validations dans les services
- Adapter l'interface selon les besoins

## 🐛 Dépannage

### Problèmes courants

1. **Erreur de base de données** : Vérifier que PostgreSQL est démarré
2. **Erreur d'authentification** : Vérifier le token JWT
3. **Erreur d'upload** : Vérifier la taille et le format des fichiers
4. **Erreur de validation** : Vérifier que tous les champs obligatoires sont remplis

### Logs

- Backend : `application.log`
- Frontend : Console du navigateur
- Base de données : Logs PostgreSQL

## 📈 Évolutions futures

### Fonctionnalités prévues

- Notifications par email
- Génération automatique de documents PDF
- Signature électronique
- Intégration avec des services tiers
- Tableau de bord administrateur avancé
- Rapports et statistiques

### Améliorations techniques

- Cache Redis pour les données de référence
- Upload de fichiers vers le cloud
- API GraphQL
- Tests automatisés complets
- Documentation OpenAPI/Swagger

---

## 📞 Support

Pour toute question ou problème :

1. Consulter ce guide
2. Vérifier les logs d'erreur
3. Contacter l'équipe de développement

**Version** : 1.0.0  
**Date** : 2024  
**Auteur** : Équipe eConsulat
