# 🔧 Dépannage - Mise à jour du statut des demandes

## 🚨 Problème

Dans l'écran "Gestion des Demandes", lors du changement de statut via la liste déroulante (Approuvé, En attente, etc.), l'erreur suivante apparaît :

```
Erreur
Impossible de mettre à jour le statut
```

## 🔍 Diagnostic

### 1. Vérification du backend

Assurez-vous que le backend est démarré :

```bash
# Dans le dossier backend
mvn spring-boot:run
```

### 2. Vérification de l'endpoint

L'endpoint de mise à jour de statut est :

```
PUT /api/admin/demandes/{id}/status?status={newStatus}
```

### 3. Test avec l'outil de diagnostic

Utilisez le fichier `test_status_update.html` pour tester :

1. Ouvrez `test_status_update.html` dans votre navigateur
2. Collez votre token JWT admin
3. Cliquez sur "📋 Récupérer les demandes"
4. Sélectionnez une demande
5. Choisissez un nouveau statut
6. Cliquez sur "🔄 Tester mise à jour statut"

## 🛠️ Solutions

### Solution 1 : Vérification du token

Assurez-vous d'être connecté en tant qu'admin avec un token valide.

### Solution 2 : Vérification des logs backend

Dans les logs du backend, cherchez :

- Erreurs 403 (Forbidden)
- Erreurs 404 (Not Found)
- Erreurs de validation

### Solution 3 : Test direct de l'API

Utilisez curl ou Postman pour tester directement :

```bash
curl -X PUT \
  "http://localhost:8080/api/admin/demandes/1/status?status=APPROVED" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json"
```

### Solution 4 : Vérification CORS

Assurez-vous que CORS est configuré pour `http://localhost:5173` dans le backend.

## 📋 Checklist de vérification

- [ ] Backend démarré sur le port 8080
- [ ] Token JWT admin valide
- [ ] Demande existe dans la base de données
- [ ] Statut valide (PENDING, APPROVED, REJECTED, COMPLETED)
- [ ] CORS configuré correctement
- [ ] Pas d'erreurs dans la console du navigateur
- [ ] Pas d'erreurs dans les logs du backend

## 🔧 Corrections apportées

### Frontend (AdminDemandesList.jsx)

- Ajout de logs détaillés pour le débogage
- Amélioration de la gestion d'erreurs
- Ajout du header Content-Type

### Backend

- Vérification de l'endpoint `/api/admin/demandes/{id}/status`
- Validation des paramètres

## 🧪 Test de validation

1. **Test de base** : Utilisez `test_status_update.html`
2. **Test dans l'interface** : Changez le statut d'une demande
3. **Vérification** : Rafraîchissez la liste pour voir le changement

## 📞 Support

Si le problème persiste :

1. Vérifiez les logs du backend
2. Vérifiez la console du navigateur (F12)
3. Utilisez l'outil de test pour isoler le problème
4. Vérifiez que la base de données contient des demandes valides

## 🔄 Statuts disponibles

- `PENDING` : En attente
- `APPROVED` : Approuvé
- `REJECTED` : Rejeté
- `COMPLETED` : Terminé

## 📝 Notes techniques

- L'endpoint utilise `@RequestParam` pour le statut
- L'authentification est gérée par JWT
- Les permissions sont vérifiées avec `@PreAuthorize("hasRole('ADMIN')")`
- La réponse retourne l'objet `Demande` mis à jour
