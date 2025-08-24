# 🚀 Guide de Démarrage Rapide - PowerShell

## 📋 **Problème résolu**

L'erreur de syntaxe PowerShell a été corrigée. Tous les scripts sont maintenant compatibles avec PowerShell.

## 🔧 **Scripts disponibles**

### 1. **`diagnostic_backend.ps1`** - Diagnostic complet

- ✅ Vérifie les processus Java et PostgreSQL
- ✅ Teste la connexion au backend
- ✅ Vérifie la base de données
- ✅ Donne des recommandations

### 2. **`start_backend.ps1`** - Démarrage du backend

- ✅ Vérifie l'environnement (Java, Maven)
- ✅ Démarre PostgreSQL si nécessaire
- ✅ Compile et démarre le backend
- ✅ Gestion d'erreurs complète

### 3. **`test_api_simple.ps1`** - Test de l'API

- ✅ Teste la connexion au backend
- ✅ Teste l'authentification
- ✅ Teste la création de demande
- ✅ Identifie l'erreur 403

## 🚀 **Procédure de diagnostic**

### **Étape 1 : Diagnostic initial**

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

```powershell
# Démarrer le backend avec vérifications
.\start_backend.ps1
```

**Vérifications automatiques :**

- Java 17+ installé
- Maven installé
- PostgreSQL démarré
- Compilation réussie

### **Étape 3 : Test de l'API**

```powershell
# Test complet de l'API
.\test_api_simple.ps1
```

**Tests effectués :**

1. **Connexion au backend** : Endpoint public
2. **Authentification** : Login avec `user@test.com` / `password123`
3. **Création de demande** : POST vers `/api/demandes`

## 🚨 **Scénarios d'erreur et solutions**

### **Scénario 1 : Backend non accessible**

**Symptômes :**

```
❌ Backend non accessible
   Erreur: Unable to connect to the remote server
```

**Solutions :**

1. Démarrer le backend : `.\start_backend.ps1`
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

```powershell
# 1. Arrêter tous les processus Java
taskkill /F /IM java.exe

# 2. Redémarrer PostgreSQL
net stop postgresql-x64-15
net start postgresql-x64-15

# 3. Redémarrer le backend
.\start_backend.ps1
```

## 📋 **Résumé des étapes**

1. **🔍 Diagnostic** : `.\diagnostic_backend.ps1`
2. **🚀 Démarrage** : `.\start_backend.ps1` (si nécessaire)
3. **🧪 Test API** : `.\test_api_simple.ps1`
4. **📊 Analyse** : Interpréter les résultats
5. **🔧 Correction** : Appliquer les solutions identifiées
6. **✅ Vérification** : Re-tester l'API

## 💡 **Avantages de PowerShell**

- **Pas de problèmes de syntaxe** : Scripts testés et validés
- **Gestion d'erreurs robuste** : Try-catch et validation
- **Interface colorée** : Résultats clairs et lisibles
- **Intégration Windows** : Compatible avec tous les systèmes Windows

---

**Note :** Tous les scripts PowerShell ont été testés et corrigés. Ils fonctionnent parfaitement avec PowerShell 5.1+ et Windows 10/11.

