# 🔧 Guide de Dépannage Port - eConsulat

## 🚨 Problème: "Port 8080 was already in use"

### Symptômes

- Erreur "Web server failed to start. Port 8080 was already in use."
- Le backend Spring Boot ne peut pas démarrer
- Échec de build Maven

### Solutions

#### Solution 1: Arrêt automatique (Recommandée)

```bash
# Arrêter automatiquement tous les processus Java
stop_java_processes.bat
```

#### Solution 2: Démarrage propre

```bash
# Démarrage avec arrêt automatique des processus
start_clean.bat
```

#### Solution 3: Arrêt manuel du port

```bash
# Arrêter manuellement les processus sur le port 8080
kill_port_8080.bat
```

## 🔍 Diagnostic étape par étape

### Étape 1: Vérifier les processus sur le port 8080

```bash
# Voir quels processus utilisent le port 8080
netstat -aon | findstr :8080
```

**Résultat attendu:**

```
TCP    0.0.0.0:8080    0.0.0.0:0    LISTENING    1234
```

### Étape 2: Identifier le processus

```bash
# Voir le nom du processus avec le PID
tasklist /fi "PID eq 1234"
```

### Étape 3: Arrêter le processus

```bash
# Arrêter le processus par PID
taskkill /PID 1234 /F
```

## 🛠️ Solutions manuelles

### Solution 1: Arrêt de tous les processus Java

```bash
# Arrêter tous les processus Java
taskkill /IM java.exe /F
```

### Solution 2: Arrêt par PID

```bash
# Trouver le PID
netstat -aon | findstr :8080

# Arrêter le processus
taskkill /PID [PID] /F
```

### Solution 3: Redémarrage de l'ordinateur

Si rien ne fonctionne, redémarrez l'ordinateur pour libérer tous les ports.

## 📋 Checklist de vérification

- [ ] Aucun processus Java en cours d'exécution
- [ ] Le port 8080 est libre
- [ ] Aucun autre service n'utilise le port 8080
- [ ] Le backend peut démarrer sans erreur
- [ ] L'application est accessible sur http://localhost:8080

## 🐛 Erreurs courantes

### Erreur: "Access is denied"

**Cause:** Droits d'accès insuffisants pour arrêter le processus
**Solution:** Exécuter en tant qu'administrateur

### Erreur: "The process is not responding"

**Cause:** Le processus est bloqué
**Solution:**

```bash
# Arrêt forcé
taskkill /PID [PID] /F
```

### Erreur: "No such process"

**Cause:** Le processus a déjà été arrêté
**Solution:** Vérifier que le port est libre avec `netstat -aon | findstr :8080`

## 🔄 Procédure de récupération complète

Si rien ne fonctionne, suivez cette procédure complète:

1. **Arrêter tous les processus**

   ```bash
   stop_java_processes.bat
   ```

2. **Vérifier que le port est libre**

   ```bash
   netstat -aon | findstr :8080
   ```

3. **Redémarrer l'ordinateur** (si nécessaire)

4. **Démarrer proprement**
   ```bash
   start_clean.bat
   ```

## 📞 Support avancé

Si les problèmes persistent:

1. **Vérifier les services Windows**

   ```bash
   # Voir les services qui utilisent le port 8080
   netstat -aon | findstr :8080
   ```

2. **Vérifier les processus détaillés**

   ```bash
   # Voir tous les processus Java
   tasklist /fi "IMAGENAME eq java.exe"
   ```

3. **Changer le port** (solution temporaire)

   ```properties
   # Dans backend/src/main/resources/application.properties
   server.port=8081
   ```

4. **Utiliser un autre port**
   ```bash
   # Démarrer sur un autre port
   mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=8081"
   ```

## 🎯 Solutions rapides

### Pour un arrêt rapide:

```bash
stop_java_processes.bat
```

### Pour un démarrage propre:

```bash
start_clean.bat
```

### Pour un diagnostic:

```bash
kill_port_8080.bat
```

## ⚠️ Prévention

Pour éviter ce problème à l'avenir:

1. **Toujours arrêter proprement** l'application avec Ctrl+C
2. **Utiliser le script d'arrêt** `stop_java_processes.bat`
3. **Vérifier les processus** avant de redémarrer
4. **Redémarrer l'ordinateur** si nécessaire

## 🔧 Scripts disponibles

- **`stop_java_processes.bat`** - Arrête tous les processus Java
- **`kill_port_8080.bat`** - Arrête les processus sur le port 8080
- **`start_clean.bat`** - Démarrage avec arrêt automatique
- **`start_complete.bat`** - Démarrage complet standard
