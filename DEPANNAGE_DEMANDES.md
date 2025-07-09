# 🔧 Guide de Dépannage - Module Demandes eConsulat

## 🚨 Problème: Les listes déroulantes sont vides

### Symptômes

- Les listes "Civilité", "Pays" et "Type de document" sont vides
- Le formulaire ne charge pas les données de référence
- Erreurs dans la console du navigateur

### Solutions

#### 1. Vérifier la base de données

```bash
# Exécuter le script de correction
fix_demandes_db.bat
```

#### 2. Vérifier que le backend est démarré

- Ouvrir http://localhost:8080
- Vérifier que la page se charge
- Vérifier les logs du backend pour les erreurs

#### 3. Tester les API directement

- Ouvrir `test_api_demandes.html` dans le navigateur
- Cliquer sur "Tester tous les endpoints"
- Vérifier les résultats

#### 4. Vérifier la configuration CORS

Si vous avez des erreurs CORS, vérifiez que le backend autorise les requêtes depuis le frontend.

## 🔍 Diagnostic étape par étape

### Étape 1: Vérifier la base de données

```sql
-- Connectez-vous à PostgreSQL et exécutez:
SELECT COUNT(*) FROM civilites;
SELECT COUNT(*) FROM pays;
SELECT COUNT(*) FROM demandes;
```

**Résultats attendus:**

- `civilites`: 3 lignes
- `pays`: 100+ lignes
- `demandes`: 0 ou plus

### Étape 2: Tester les endpoints API

```bash
# Test des civilités
curl http://localhost:8080/api/demandes/civilites

# Test des pays
curl http://localhost:8080/api/demandes/pays

# Test des types de documents
curl http://localhost:8080/api/demandes/document-types
```

### Étape 3: Vérifier les logs du backend

Regardez les logs Spring Boot pour:

- Erreurs de connexion à la base de données
- Erreurs de requêtes SQL
- Erreurs de configuration

## 🛠️ Solutions rapides

### Solution 1: Redémarrer avec correction automatique

```bash
start_with_fixed_demandes.bat
```

### Solution 2: Correction manuelle de la base

```bash
# 1. Arrêter le backend
# 2. Exécuter le script de correction
fix_demandes_db.bat
# 3. Redémarrer le backend
cd backend && mvn spring-boot:run
```

### Solution 3: Vérifier les URLs dans le frontend

Vérifiez que les URLs dans `NewDemandeForm.jsx` pointent vers le bon serveur:

```javascript
// Doit être:
fetch("http://localhost:8080/api/demandes/civilites", ...)
// Pas:
fetch("/api/demandes/civilites", ...)
```

## 📋 Checklist de vérification

- [ ] PostgreSQL est démarré
- [ ] La base de données 'econsulat' existe
- [ ] Les tables `civilites`, `pays`, `adresses`, `demandes` existent
- [ ] Les tables contiennent des données
- [ ] Le backend Spring Boot est démarré sur le port 8080
- [ ] Le frontend React est démarré sur le port 5173
- [ ] Les URLs dans le frontend pointent vers `http://localhost:8080`
- [ ] Pas d'erreurs CORS dans la console du navigateur
- [ ] L'utilisateur est connecté avec un token valide

## 🐛 Erreurs courantes

### Erreur: "Cannot connect to database"

**Cause:** PostgreSQL n'est pas démarré ou la base n'existe pas
**Solution:**

```bash
# Démarrer PostgreSQL
# Créer la base si elle n'existe pas
createdb -U postgres econsulat
```

### Erreur: "CORS policy"

**Cause:** Le backend n'autorise pas les requêtes depuis le frontend
**Solution:** Vérifier la configuration CORS dans `SecurityConfig.java`

### Erreur: "401 Unauthorized"

**Cause:** L'utilisateur n'est pas connecté ou le token est invalide
**Solution:** Se reconnecter et vérifier que le token est bien stocké

### Erreur: "500 Internal Server Error"

**Cause:** Erreur côté serveur (base de données, logique métier)
**Solution:** Vérifier les logs du backend pour plus de détails

## 📞 Support

Si les problèmes persistent:

1. Vérifiez les logs complets du backend
2. Vérifiez la console du navigateur
3. Testez avec `test_api_demandes.html`
4. Vérifiez la configuration de la base de données

## 🔄 Redémarrage complet

Pour un redémarrage complet propre:

```bash
# 1. Arrêter tous les services
# 2. Corriger la base de données
fix_demandes_db.bat
# 3. Redémarrer avec le script complet
start_with_fixed_demandes.bat
```
