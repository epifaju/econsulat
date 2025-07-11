# 🚨 Résolution Rapide - NetworkError

## 🎯 Problème Identifié

Erreur `NetworkError when attempting to fetch resource` lors de la génération PDF.

## 🔍 Diagnostic Immédiat

### 1. Vérifier si le Backend est Démarré

```bash
# Ouvrir dans le navigateur
http://localhost:8080/actuator/health
```

**Si erreur 404 ou connexion refusée** → Le backend n'est pas démarré

### 2. Test Rapide avec le Fichier de Test

1. Ouvrir `test_backend_connection.html`
2. Cliquer sur "Test Health"
3. Vérifier le résultat

## 🛠️ Solutions par Ordre de Priorité

### Solution 1: Démarrer le Backend (90% des cas)

```bash
# Option A: Script automatisé
check_and_start_backend.bat

# Option B: Manuel
cd backend
mvn spring-boot:run
```

### Solution 2: Vérifier le Port 8080

```bash
# Vérifier si le port est utilisé
netstat -an | findstr :8080

# Si utilisé, arrêter le processus
taskkill /f /im java.exe
```

### Solution 3: Vérifier la Base de Données

- PostgreSQL doit être démarré
- Vérifier la connexion dans `application.properties`

### Solution 4: Vérifier les Dépendances

```bash
cd backend
mvn clean install
```

## 🧪 Tests de Validation

### Test 1: Health Check

```bash
curl http://localhost:8080/actuator/health
# Doit retourner: {"status":"UP"}
```

### Test 2: Test Connexion

1. Ouvrir `test_backend_connection.html`
2. Cliquer sur "Test Health"
3. Vérifier que c'est vert ✅

### Test 3: Test PDF

1. Après health OK
2. Cliquer sur "Test Endpoint PDF"
3. Vérifier que l'endpoint répond

## 📋 Checklist Express

- [ ] Backend démarré sur `http://localhost:8080`
- [ ] Health check retourne `{"status":"UP"}`
- [ ] Port 8080 libre
- [ ] Base de données accessible
- [ ] Pas d'erreurs dans les logs backend

## 🚨 Erreurs Courantes

### "Connection refused"

- **Cause** : Backend non démarré
- **Solution** : `mvn spring-boot:run`

### "Port already in use"

- **Cause** : Autre processus sur le port 8080
- **Solution** : `taskkill /f /im java.exe`

### "Database connection failed"

- **Cause** : PostgreSQL non démarré
- **Solution** : Démarrer PostgreSQL

### "404 Not Found"

- **Cause** : Endpoint inexistant ou mauvaise URL
- **Solution** : Vérifier l'URL et les annotations

## 🔧 Commandes Utiles

```bash
# Vérifier les processus Java
jps -l

# Vérifier le port 8080
netstat -an | findstr :8080

# Arrêter tous les processus Java
taskkill /f /im java.exe

# Redémarrer le backend
cd backend && mvn spring-boot:run

# Vérifier les logs
tail -f backend/logs/application.log
```

## 📞 Support Immédiat

Si le problème persiste après ces étapes :

1. **Collecter les informations** :

   - Résultat de `test_backend_connection.html`
   - Logs du backend
   - Version de Java et Maven

2. **Vérifier la configuration** :

   - `application.properties`
   - `pom.xml`
   - Variables d'environnement

3. **Tester avec curl** :
   ```bash
   curl -X GET http://localhost:8080/actuator/health
   curl -X POST http://localhost:8080/api/admin/pdf-documents/generate?demandeId=1&documentTypeId=1
   ```

---

**Temps de résolution estimé** : 2-5 minutes  
**Cause la plus fréquente** : Backend non démarré (90% des cas)
