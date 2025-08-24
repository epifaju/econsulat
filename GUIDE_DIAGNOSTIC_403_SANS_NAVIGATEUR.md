# 🔐 Diagnostic Erreur 403 - Sans Navigateur

## 📋 **Problème identifié**

Erreur **403 (Forbidden)** lors de la soumission du formulaire de demande. Cette erreur indique un problème d'autorisation dans le système d'authentification.

## 🚀 **Approche de diagnostic**

Puisque le navigateur ne peut pas accéder au backend, nous allons diagnostiquer directement depuis la ligne de commande.

## 🔧 **Outils de diagnostic créés**

### 1. **Diagnostic du backend** (`diagnostic_backend.ps1`)

- Vérifie les processus Java
- Vérifie les ports utilisés
- Teste la connexion au backend
- Vérifie la base de données

### 2. **Démarrage du backend** (`start_backend_diagnostic.bat`)

- Vérifie l'environnement (Java, Maven)
- Vérifie PostgreSQL
- Compile et démarre le backend

### 3. **Test API direct** (`test_api_direct.ps1`)

- Teste la connexion au backend
- Teste l'authentification
- Teste la création de demande
- Teste les autres endpoints

## 📝 **Procédure de diagnostic étape par étape**

### **Étape 1 : Vérifier l'état actuel**

```powershell
# Exécuter le diagnostic du backend
.\diagnostic_backend.ps1
```

**Résultats attendus :**

- ✅ Processus Java trouvés
- ✅ Port 8080 utilisé
- ✅ Backend accessible
- ✅ Base de données accessible

### **Étape 2 : Démarrer le backend si nécessaire**

Si le diagnostic révèle des problèmes :

```batch
# Démarrer le backend avec vérifications
start_backend_diagnostic.bat
```

**Vérifications automatiques :**

- Java 17+ installé
- Maven installé
- PostgreSQL démarré
- Compilation réussie

### **Étape 3 : Test complet de l'API**

```powershell
# Test complet de l'API
.\test_api_direct.ps1
```

**Tests effectués :**

1. **Connexion au backend** : Endpoint public `/api/demandes/document-types`
2. **Authentification** : Login avec `user@test.com` / `password123`
3. **Création de demande** : POST vers `/api/demandes`
4. **Autres endpoints** : Vérification des autorisations

## 🚨 **Scénarios d'erreur et solutions**

### **Scénario 1 : Backend non accessible**

**Symptômes :**

```
❌ Backend non accessible
   Erreur: Unable to connect to the remote server
```

**Solutions :**

1. Démarrer le backend : `start_backend_diagnostic.bat`
2. Vérifier que le port 8080 est libre
3. Vérifier les logs du backend

### **Scénario 2 : Base de données inaccessible**

**Symptômes :**

```
❌ Aucun processus PostgreSQL trouvé
❌ Connexion à la base échouée
```

**Solutions :**

1. Démarrer PostgreSQL : `net start postgresql-x64-15`
2. Vérifier les services Windows
3. Vérifier le mot de passe PostgreSQL

### **Scénario 3 : Authentification échouée**

**Symptômes :**

```
❌ Échec de l'authentification
   Status: 401
```

**Solutions :**

1. Créer un utilisateur de test : `diagnostic_auth_403.sql`
2. Vérifier la structure de la table `users`
3. Vérifier les rôles et permissions

### **Scénario 4 : Erreur 403 sur la création de demande**

**Symptômes :**

```
❌ Échec de la création de demande
   Erreur: 403 Forbidden
```

**Solutions :**

1. Vérifier le rôle de l'utilisateur dans la base
2. Vérifier la configuration Spring Security
3. Analyser les logs du backend

## 🛠️ **Commandes de diagnostic manuel**

### **Vérifier les processus**

```powershell
# Processus Java
Get-Process -Name "java"

# Processus PostgreSQL
Get-Process -Name "postgres"

# Ports utilisés
Get-NetTCPConnection -LocalPort 8080
```

### **Tester la base de données**

```bash
# Connexion directe
psql -h localhost -p 5432 -U postgres -d econsulat

# Vérifier les utilisateurs
SELECT id, email, role, enabled FROM users ORDER BY id;

# Vérifier les rôles
SELECT DISTINCT role, COUNT(*) FROM users GROUP BY role;
```

### **Tester l'API avec curl**

```bash
# Test de connexion
curl -v http://127.0.0.1:8080/api/demandes/document-types

# Test d'authentification
curl -X POST http://127.0.0.1:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"user@test.com","password":"password123"}'

# Test de création de demande (avec token)
curl -X POST http://127.0.0.1:8080/api/demandes \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer VOTRE_TOKEN" \
  -d '{"firstName":"Test","lastName":"User",...}'
```

## 📊 **Interprétation des résultats**

### **Diagnostic réussi**

```
✅ Backend accessible!
✅ Authentification réussie!
✅ Demande créée avec succès!
```

**Signification :** Le système fonctionne correctement, l'erreur 403 peut venir du frontend.

### **Diagnostic partiel**

```
✅ Backend accessible!
✅ Authentification réussie!
❌ Échec de la création de demande
```

**Signification :** Problème d'autorisation dans la configuration Spring Security.

### **Diagnostic échoué**

```
❌ Backend non accessible
❌ Base de données inaccessible
```

**Signification :** Problème d'infrastructure, le backend doit être démarré.

## 🔍 **Analyse des logs**

### **Logs du backend**

Regardez la console où le backend est démarré pour identifier :

- Les tentatives d'authentification
- Les erreurs de validation JWT
- Les problèmes de base de données
- Les erreurs de sécurité

### **Logs de la base de données**

```sql
-- Vérifier les connexions actives
SELECT * FROM pg_stat_activity WHERE datname = 'econsulat';

-- Vérifier les erreurs récentes
SELECT * FROM pg_stat_database WHERE datname = 'econsulat';
```

## 📝 **Checklist de diagnostic**

- [ ] **Backend démarré** : Port 8080 accessible
- [ ] **Base de données** : PostgreSQL connecté
- [ ] **Utilisateurs** : Au moins un utilisateur avec rôle USER
- [ ] **Authentification** : Login réussi avec token JWT
- [ ] **Autorisations** : Endpoint `/api/demandes` accessible
- [ ] **Création de demande** : POST réussi

## 🆘 **Si le problème persiste**

### **Vérifications avancées**

1. **Configuration Spring Security** : Vérifier `SecurityConfig.java`
2. **Filtre JWT** : Vérifier `JwtAuthenticationFilter.java`
3. **Entités de base** : Vérifier les tables `civilites`, `pays`, `document_types`
4. **Logs détaillés** : Activer le logging DEBUG dans `application.properties`

### **Redémarrage complet**

```batch
# 1. Arrêter tous les processus Java
taskkill /F /IM java.exe

# 2. Redémarrer PostgreSQL
net stop postgresql-x64-15
net start postgresql-x64-15

# 3. Redémarrer le backend
start_backend_diagnostic.bat
```

## 📋 **Résumé des étapes**

1. **🔍 Diagnostic** : `diagnostic_backend.ps1`
2. **🚀 Démarrage** : `start_backend_diagnostic.bat` (si nécessaire)
3. **🧪 Test API** : `test_api_direct.ps1`
4. **📊 Analyse** : Interpréter les résultats
5. **🔧 Correction** : Appliquer les solutions identifiées
6. **✅ Vérification** : Re-tester l'API

---

**Note :** Ce guide permet de diagnostiquer l'erreur 403 sans avoir besoin d'un navigateur, en utilisant uniquement la ligne de commande et les outils PowerShell.

