# 👥 Guide - Suppression d'utilisateurs (Admin)

## 📋 Vue d'ensemble

Cette fonctionnalité permet aux administrateurs de supprimer définitivement des utilisateurs depuis l'interface d'administration, avec suppression en cascade de toutes les demandes associées.

## 🔧 Fonctionnalités

### ✅ Actions disponibles

- **Suppression sécurisée** : Confirmation obligatoire avant suppression
- **Suppression en cascade** : Suppression automatique de toutes les demandes associées
- **Feedback visuel** : Indicateur de chargement pendant la suppression
- **Notifications** : Messages de succès/erreur après l'action
- **Modal personnalisé** : Interface moderne remplaçant `window.confirm()`

### 🛡️ Sécurité

- **Authentification requise** : Seuls les administrateurs peuvent supprimer
- **Confirmation utilisateur** : Double validation avant suppression
- **Gestion d'erreurs** : Messages d'erreur détaillés en cas de problème
- **Protection contre auto-suppression** : Empêche la suppression de son propre compte

## 🎯 Utilisation

### 1. Accès à la fonctionnalité

1. Connectez-vous en tant qu'administrateur
2. Accédez à la section "Gestion des utilisateurs"
3. Localisez la colonne "Actions" dans le tableau

### 2. Suppression d'un utilisateur

1. **Cliquez sur l'icône 🗑️** (poubelle) dans la ligne de l'utilisateur
2. **Confirmez la suppression** dans le modal de confirmation personnalisé
3. **Attendez la confirmation** de suppression
4. **Vérifiez** que l'utilisateur a disparu du tableau

### 3. Indicateurs visuels

- **Icône normale** : 🗑️ (rouge) - Prêt à supprimer
- **Icône de chargement** : ⏳ (gris) - Suppression en cours
- **Bouton désactivé** : Pendant la suppression

### 4. Modal de confirmation

- **Design personnalisé** : Modal moderne et cohérent avec l'interface
- **Message détaillé** : Affichage du nom, email et nombre de demandes
- **Avertissement** : Information sur l'irréversibilité et l'impact
- **Types de modal** : Support pour danger, warning, info avec couleurs adaptées
- **États de chargement** : Indicateur visuel pendant la suppression
- **Fermeture multiple** : Bouton Annuler, clic sur overlay, touche Échap

## 🔌 API Backend

### Endpoint de suppression

```
DELETE /api/admin/users/{id}
```

### Headers requis

```
Authorization: Bearer {token_admin}
```

### Réponse de succès

```json
{
  "message": "Utilisateur supprimé avec succès"
}
```

### Codes de réponse

- **200 OK** : Utilisateur supprimé avec succès
- **404 Not Found** : Utilisateur non trouvé
- **403 Forbidden** : Accès non autorisé
- **400 Bad Request** : Tentative de suppression de son propre compte
- **500 Internal Server Error** : Erreur serveur

## 🗄️ Logique de suppression

### Suppression en cascade

1. **Documents générés** : Suppression automatique de tous les documents générés
2. **Demandes** : Suppression de toutes les demandes de l'utilisateur
3. **Utilisateur** : Suppression de l'utilisateur et de ses données associées

### Entités supprimées

- ✅ Utilisateur principal
- ✅ Toutes les demandes associées
- ✅ Tous les documents générés
- ✅ Adresses associées (si pas d'autres références)
- ❌ Civilités (conservées)
- ❌ Pays (conservés)
- ❌ Types de documents (conservés)

## 🧪 Tests

### Fichier de test

Utilisez `test_admin_delete_user.html` pour tester la fonctionnalité :

1. **Authentification** : Testez l'accès admin
2. **Liste des utilisateurs** : Chargez les utilisateurs disponibles
3. **Sélection** : Choisissez un utilisateur à supprimer
4. **Suppression** : Testez la suppression complète

### Tests manuels

```bash
# Test avec curl
curl -X DELETE \
  -H "Authorization: Bearer YOUR_ADMIN_TOKEN" \
  http://localhost:8080/api/admin/users/1
```

## ⚠️ Points d'attention

### ⚠️ Actions irréversibles

- **Pas de restauration** : La suppression est définitive
- **Pas de corbeille** : Aucun système de récupération
- **Confirmation obligatoire** : Double validation requise
- **Impact majeur** : Suppression de toutes les demandes associées

### 🔒 Permissions

- **Admin uniquement** : Seuls les administrateurs peuvent supprimer
- **Token valide** : Authentification JWT requise
- **Session active** : Connexion admin nécessaire
- **Protection auto-suppression** : Empêche la suppression de son propre compte

### 📊 Impact sur les statistiques

- **Compteurs mis à jour** : Les statistiques sont recalculées
- **Documents supprimés** : Impact sur les métriques de génération
- **Demandes supprimées** : Impact sur les statistiques globales
- **Historique** : Pas de trace dans les logs d'audit

## 🚨 Gestion d'erreurs

### Erreurs courantes

1. **Utilisateur non trouvé** : ID invalide ou déjà supprimé
2. **Accès refusé** : Token invalide ou permissions insuffisantes
3. **Auto-suppression** : Tentative de suppression de son propre compte
4. **Erreur de connexion** : Problème réseau ou serveur indisponible

### Messages d'erreur

- ✅ Messages clairs et informatifs
- ✅ Suggestions de résolution
- ✅ Logs détaillés côté serveur

## 📝 Logs et monitoring

### Logs côté serveur

```java
// Suppression des documents générés
generatedDocumentRepository.deleteAllByDemandeUserId(userId);

// Suppression des demandes
demandeRepository.deleteAllByUserId(userId);

// Suppression de l'utilisateur
userRepository.delete(user);
```

### Logs côté client

```javascript
console.log(`Suppression de l'utilisateur ${userId}`);
console.log(`Réponse suppression: ${response.status}`);
```

## 🔄 Intégration

### Composants React

- **AdminUsersList.jsx** : Interface principale
- **ConfirmationModal.jsx** : Modal de confirmation personnalisé
- **TrashIcon** : Icône de suppression (Heroicons)
- **handleDeleteUser** : Fonction de suppression

### Services backend

- **AdminController.deleteUser()** : Endpoint REST
- **AdminService.deleteUser()** : Logique métier
- **@Transactional** : Gestion des transactions

## 📈 Métriques

### Suivi des suppressions

- **Nombre de suppressions** : Par jour/semaine/mois
- **Types d'utilisateurs** : Quels rôles sont le plus supprimés
- **Impact sur les demandes** : Nombre de demandes supprimées par utilisateur
- **Administrateurs** : Quels admins suppriment le plus

### Alertes

- **Suppressions massives** : Plus de X suppressions par jour
- **Erreurs fréquentes** : Taux d'erreur élevé
- **Performance** : Temps de suppression anormal
- **Auto-suppression** : Tentatives de suppression de son propre compte

## 🔮 Évolutions futures

### Fonctionnalités envisagées

- **Archivage** : Système d'archivage au lieu de suppression définitive
- **Historique** : Logs d'audit des suppressions
- **Restauration** : Possibilité de restaurer un utilisateur supprimé
- **Notifications** : Alertes aux autres administrateurs
- **Export** : Export des données avant suppression

### Améliorations techniques

- **Soft delete** : Suppression logique au lieu de physique
- **Backup automatique** : Sauvegarde avant suppression
- **Validation avancée** : Vérifications supplémentaires
- **Performance** : Optimisation des suppressions en cascade

## 🛠️ Dépannage

### Problèmes courants

#### Modal ne s'affiche pas

- **Vérifiez l'import** : `ConfirmationModal` doit être importé
- **Vérifiez les états** : `showDeleteModal` doit être `true`
- **Console du navigateur** : Recherchez les erreurs JavaScript

#### Erreur 404 lors de la suppression

- **Vérifiez l'ID** : L'utilisateur existe-t-il ?
- **Vérifiez l'URL** : L'endpoint est-il correct ?
- **Logs backend** : Consultez les logs du serveur

#### Erreur 403 (Forbidden)

- **Vérifiez le token** : Le token admin est-il valide ?
- **Vérifiez les permissions** : L'utilisateur a-t-il le rôle ADMIN ?

#### Suppression échoue

- **Vérifiez la base de données** : Contraintes de clés étrangères ?
- **Logs backend** : Erreurs dans les logs du serveur
- **Console navigateur** : Erreurs réseau ou JavaScript

### Solutions

#### Redémarrage du backend

```bash
# Arrêter le serveur
Ctrl+C

# Redémarrer
mvn spring-boot:run
```

#### Vérification de la base de données

```sql
-- Vérifier l'existence de l'utilisateur
SELECT * FROM users WHERE id = ?;

-- Vérifier les demandes associées
SELECT * FROM demandes WHERE user_id = ?;

-- Vérifier les documents générés
SELECT * FROM generated_documents WHERE demande_id IN (
  SELECT id FROM demandes WHERE user_id = ?
);
```

#### Nettoyage du cache navigateur

- **Ctrl+F5** : Rechargement forcé
- **F12 → Application → Storage** : Vider le cache
- **Mode navigation privée** : Tester sans cache

---

**⚠️ ATTENTION** : Cette fonctionnalité est critique et irréversible. La suppression d'un utilisateur entraîne la suppression de toutes ses demandes et documents associés. Assurez-vous de bien comprendre les implications avant de procéder.
