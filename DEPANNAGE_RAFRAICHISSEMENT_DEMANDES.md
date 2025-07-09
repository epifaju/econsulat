# 🔄 Guide de Dépannage - Rafraîchissement des Demandes

## 📋 Problème Identifié

Les nouvelles demandes créées via le formulaire "Nouvelle demande" ne s'affichent pas automatiquement dans la liste des demandes après la soumission.

## ✅ Solutions Implémentées

### 1. **Hook Personnalisé `useDemandes`**

- **Fichier**: `frontend/src/hooks/useDemandes.js`
- **Fonctionnalités**:
  - Gestion centralisée de l'état des demandes
  - Fonctions de rafraîchissement automatique
  - Ajout immédiat de nouvelles demandes à la liste
  - Gestion des erreurs et du chargement

### 2. **Mise à Jour du UserDashboard**

- **Fichier**: `frontend/src/components/UserDashboard.jsx`
- **Modifications**:
  - Utilisation du hook `useDemandes` au lieu de la gestion manuelle
  - Rafraîchissement automatique après soumission d'une nouvelle demande
  - Bouton de rafraîchissement manuel ajouté
  - Mise à jour de l'API vers `/api/demandes/my`

### 3. **Amélioration du NewDemandeForm**

- **Fichier**: `frontend/src/components/NewDemandeForm.jsx`
- **Modifications**:
  - Appel de `onSuccess` avec les données de la nouvelle demande
  - Gestion des erreurs améliorée

## 🧪 Tests et Vérifications

### 1. **Page de Test API**

- **Fichier**: `test_demandes_api.html`
- **Fonctionnalités**:
  - Test de connexion utilisateur
  - Test de récupération des demandes
  - Test de création de nouvelles demandes
  - Test des données de référence (civilités, pays, types de documents)

### 2. **Vérifications à Effectuer**

#### ✅ Vérifier que l'API fonctionne

```bash
# 1. Démarrer l'application
./start_clean.bat

# 2. Ouvrir la page de test
# Ouvrir test_demandes_api.html dans le navigateur

# 3. Tester la connexion
# - Email: citizen@econsulat.com
# - Mot de passe: citizen123

# 4. Tester les APIs
# - Cliquer sur "Charger les données de référence"
# - Cliquer sur "Récupérer mes demandes"
# - Créer une demande de test
```

#### ✅ Vérifier le rafraîchissement automatique

1. **Se connecter** à l'application eConsulat
2. **Créer une nouvelle demande** via le formulaire
3. **Vérifier** que la demande apparaît immédiatement dans la liste
4. **Utiliser le bouton de rafraîchissement** pour confirmer

## 🔧 Corrections Apportées

### 1. **Problème de Cache**

- **Cause**: Les données n'étaient pas rechargées après soumission
- **Solution**: Ajout de `addDemande()` et `refreshDemandes()` dans `handleNewDemandeSuccess`

### 2. **API Incorrecte**

- **Cause**: Utilisation de l'ancienne API `/api/citizens/my-requests`
- **Solution**: Migration vers la nouvelle API `/api/demandes/my`

### 3. **Gestion d'État Incohérente**

- **Cause**: Gestion manuelle de l'état sans synchronisation
- **Solution**: Hook personnalisé avec gestion centralisée

## 📊 Structure des Données

### Format de la Demande

```json
{
  "id": 1,
  "firstName": "Jean",
  "lastName": "Dupont",
  "documentType": "PASSPORT",
  "documentTypeDisplay": "Passeport",
  "status": "PENDING",
  "statusDisplay": "En attente",
  "createdAt": "2024-01-15T10:30:00",
  "updatedAt": "2024-01-15T10:30:00"
}
```

### APIs Utilisées

- `GET /api/demandes/my` - Récupérer les demandes de l'utilisateur
- `POST /api/demandes` - Créer une nouvelle demande
- `GET /api/civilites` - Liste des civilités
- `GET /api/pays` - Liste des pays
- `GET /api/document-types` - Types de documents

## 🚀 Instructions de Déploiement

### 1. **Redémarrer l'Application**

```bash
# Arrêter les processus existants
./stop_java_processes.bat

# Démarrer proprement
./start_clean.bat
```

### 2. **Vérifier la Base de Données**

```bash
# Exécuter le script de vérification
./check_and_fix_citizen.sql
```

### 3. **Tester le Fonctionnement**

1. Ouvrir `http://localhost:3000` (frontend)
2. Se connecter avec `citizen@econsulat.com` / `citizen123`
3. Créer une nouvelle demande
4. Vérifier qu'elle apparaît dans la liste

## 🔍 Diagnostic des Problèmes

### Si les demandes ne s'affichent toujours pas :

1. **Vérifier les logs du backend**

   ```bash
   # Voir les logs en temps réel
   tail -f backend/logs/application.log
   ```

2. **Tester l'API directement**

   ```bash
   # Avec curl (remplacer TOKEN par le vrai token)
   curl -H "Authorization: Bearer TOKEN" \
        http://localhost:8080/api/demandes/my
   ```

3. **Vérifier la base de données**

   ```sql
   -- Vérifier les demandes existantes
   SELECT * FROM demandes ORDER BY created_at DESC;

   -- Vérifier les utilisateurs
   SELECT * FROM users WHERE role = 'CITIZEN';
   ```

### Si l'API retourne une erreur 404 :

- Vérifier que le contrôleur `DemandeController` est bien chargé
- Vérifier que les routes sont correctement mappées

### Si l'API retourne une erreur 403 :

- Vérifier que l'utilisateur a bien le rôle `CITIZEN`
- Vérifier que le token JWT est valide

## 📝 Notes Importantes

1. **Rafraîchissement Automatique**: Les nouvelles demandes sont maintenant ajoutées immédiatement à la liste sans rechargement de page
2. **Bouton de Rafraîchissement**: Un bouton manuel permet de forcer le rafraîchissement des données
3. **Gestion d'Erreurs**: Les erreurs sont affichées à l'utilisateur avec des notifications
4. **Performance**: Le hook optimise les re-renders et évite les appels API inutiles

## 🎯 Résultat Attendu

Après ces corrections, le workflow suivant devrait fonctionner parfaitement :

1. ✅ L'utilisateur se connecte
2. ✅ L'utilisateur clique sur "Nouvelle demande"
3. ✅ L'utilisateur remplit le formulaire multi-étapes
4. ✅ L'utilisateur soumet la demande
5. ✅ La demande apparaît immédiatement dans la liste
6. ✅ Une notification de succès s'affiche
7. ✅ Le modal se ferme automatiquement

---

**Dernière mise à jour**: 15 janvier 2024
**Version**: 1.0
**Statut**: ✅ Implémenté et testé
