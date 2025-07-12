# 🗑️ Guide - Suppression des demandes (Admin)

## 📋 Vue d'ensemble

Cette fonctionnalité permet aux administrateurs de supprimer définitivement des demandes de documents depuis l'interface d'administration.

## 🔧 Fonctionnalités

### ✅ Actions disponibles

- **Suppression sécurisée** : Confirmation obligatoire avant suppression
- **Suppression en cascade** : Suppression automatique des documents générés associés
- **Feedback visuel** : Indicateur de chargement pendant la suppression
- **Notifications** : Messages de succès/erreur après l'action

### 🛡️ Sécurité

- **Authentification requise** : Seuls les administrateurs peuvent supprimer
- **Confirmation utilisateur** : Double validation avant suppression
- **Gestion d'erreurs** : Messages d'erreur détaillés en cas de problème

## 🎯 Utilisation

### 1. Accès à la fonctionnalité

1. Connectez-vous en tant qu'administrateur
2. Accédez à la section "Gestion des demandes"
3. Localisez la colonne "Actions" dans le tableau

### 2. Suppression d'une demande

1. **Cliquez sur l'icône 🗑️** (poubelle) dans la ligne de la demande
2. **Confirmez la suppression** dans le modal de confirmation personnalisé
3. **Attendez la confirmation** de suppression
4. **Vérifiez** que la demande a disparu du tableau

### 3. Indicateurs visuels

- **Icône normale** : 🗑️ (rouge) - Prêt à supprimer
- **Icône de chargement** : ⏳ (gris) - Suppression en cours
- **Bouton désactivé** : Pendant la suppression

### 4. Modal de confirmation

- **Design personnalisé** : Modal moderne et cohérent avec l'interface
- **Message détaillé** : Affichage du nom du demandeur et des conséquences
- **Types de modal** : Support pour danger, warning, info avec couleurs adaptées
- **États de chargement** : Indicateur visuel pendant la suppression
- **Fermeture multiple** : Bouton Annuler, clic sur overlay, touche Échap

## 🔌 API Backend

### Endpoint de suppression

```
DELETE /api/admin/demandes/{id}
```

### Headers requis

```
Authorization: Bearer {token_admin}
```

### Réponse de succès

```json
{
  "message": "Demande supprimée avec succès"
}
```

### Codes de réponse

- **200 OK** : Demande supprimée avec succès
- **404 Not Found** : Demande non trouvée
- **403 Forbidden** : Accès non autorisé
- **500 Internal Server Error** : Erreur serveur

## 🗄️ Logique de suppression

### Suppression en cascade

1. **Documents générés** : Suppression automatique de tous les documents générés
2. **Demande principale** : Suppression de la demande et de ses données associées
3. **Adresse** : Suppression de l'adresse liée (si pas d'autres références)

### Entités supprimées

- ✅ Demande principale
- ✅ Documents générés associés
- ✅ Adresse (si unique)
- ❌ Utilisateur (conservé)
- ❌ Civilité (conservée)
- ❌ Pays (conservés)

## 🧪 Tests

### Fichier de test

Utilisez `test_admin_delete_demande.html` pour tester la fonctionnalité :

1. **Authentification** : Testez l'accès admin
2. **Liste des demandes** : Chargez les demandes disponibles
3. **Sélection** : Choisissez une demande à supprimer
4. **Suppression** : Testez la suppression complète

### Tests manuels

```bash
# Test avec curl
curl -X DELETE \
  -H "Authorization: Bearer YOUR_ADMIN_TOKEN" \
  http://localhost:8080/api/admin/demandes/1
```

## ⚠️ Points d'attention

### ⚠️ Actions irréversibles

- **Pas de restauration** : La suppression est définitive
- **Pas de corbeille** : Aucun système de récupération
- **Confirmation obligatoire** : Double validation requise

### 🔒 Permissions

- **Admin uniquement** : Seuls les administrateurs peuvent supprimer
- **Token valide** : Authentification JWT requise
- **Session active** : Connexion admin nécessaire

### 📊 Impact sur les statistiques

- **Compteurs mis à jour** : Les statistiques sont recalculées
- **Documents supprimés** : Impact sur les métriques de génération
- **Historique** : Pas de trace dans les logs d'audit

## 🚨 Gestion d'erreurs

### Erreurs courantes

1. **Demande non trouvée** : ID invalide ou déjà supprimée
2. **Accès refusé** : Token invalide ou permissions insuffisantes
3. **Erreur de connexion** : Problème réseau ou serveur indisponible

### Messages d'erreur

- ✅ Messages clairs et informatifs
- ✅ Suggestions de résolution
- ✅ Logs détaillés côté serveur

## 📝 Logs et monitoring

### Logs côté serveur

```java
// Suppression des documents générés
generatedDocumentRepository.deleteAll(generatedDocs);

// Suppression de la demande
demandeRepository.delete(demande);
```

### Logs côté client

```javascript
console.log(`Suppression de la demande ${demandeId}`);
console.log(`Réponse suppression: ${response.status}`);
```

## 🔄 Intégration

### Composants React

- **AdminDemandesList.jsx** : Interface principale
- **TrashIcon** : Icône de suppression (Heroicons)
- **handleDeleteDemande** : Fonction de suppression

### Services backend

- **AdminController.deleteDemande()** : Endpoint REST
- **AdminService.deleteDemande()** : Logique métier
- **@Transactional** : Gestion des transactions

## 📈 Métriques

### Suivi des suppressions

- **Nombre de suppressions** : Par jour/semaine/mois
- **Types de documents** : Quels types sont le plus supprimés
- **Utilisateurs** : Quels admins suppriment le plus

### Alertes

- **Suppressions massives** : Plus de X suppressions par jour
- **Erreurs fréquentes** : Taux d'erreur élevé
- **Performance** : Temps de suppression anormal

## 🔮 Évolutions futures

### Fonctionnalités envisagées

- **Suppression en lot** : Supprimer plusieurs demandes
- **Archivage** : Alternative à la suppression définitive
- **Audit trail** : Historique des suppressions
- **Restauration** : Système de récupération limité

### Améliorations techniques

- **Soft delete** : Marquage au lieu de suppression physique
- **Backup automatique** : Sauvegarde avant suppression
- **Notifications** : Alertes aux utilisateurs concernés
