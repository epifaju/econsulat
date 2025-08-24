# 🔧 Guide de Diagnostic - Création de Demandes

## 🚨 Problème

Les tests HTML ne fonctionnent pas car il n'y a pas de connexion au backend et pas de token d'authentification.

## 🔍 Diagnostic Étape par Étape

### **Étape 1: Vérifier l'État du Système**

```bash
# Exécuter le script de vérification
test_connectivite_simple.bat
```

**Résultats attendus:**

- ✅ Ping vers localhost réussi
- ✅ Port 8080 ouvert
- ✅ Processus Java en cours d'exécution
- ✅ Connexion HTTP réussie

**Si des tests échouent:**

- Le backend n'est pas démarré
- Il y a un conflit de port
- Java n'est pas installé

### **Étape 2: Démarrer le Backend**

```bash
# Utiliser le script de démarrage amélioré
start_backend_with_checks.bat
```

**Ce script vérifie:**

1. Port 8080 libre
2. Java installé (version 17+)
3. Maven installé
4. Structure du projet correcte
5. Cache Maven nettoyé

### **Étape 3: Tester l'API avec PowerShell**

```powershell
# Ouvrir PowerShell et exécuter
.\test_api_powershell.ps1
```

**Ce script teste:**

1. Connexion de base au backend
2. Authentification avec admin@econsulat.com
3. Chargement des données de référence
4. Création d'une demande de test

### **Étape 4: Vérifier la Base de Données**

```sql
-- Exécuter le script de diagnostic
\i test_database_structure.sql
```

**Vérifications:**

- Tables existent et contiennent des données
- Civilités disponibles (au moins 1)
- Pays disponibles (au moins 1)
- Types de documents disponibles (au moins 1)
- Utilisateur admin existe avec rôle ADMIN

## 🛠️ Solutions aux Problèmes Courants

### **Problème 1: Port 8080 déjà utilisé**

```bash
# Arrêter tous les processus Java
taskkill /f /im java.exe

# Vérifier qu'aucun processus n'utilise le port
netstat -ano | findstr ":8080"
```

### **Problème 2: Java non installé**

- Télécharger et installer Java 17+ depuis Oracle ou OpenJDK
- Ajouter Java au PATH système
- Redémarrer le terminal

### **Problème 3: Maven non installé**

- Télécharger et installer Maven depuis Apache
- Ajouter Maven au PATH système
- Redémarrer le terminal

### **Problème 4: Base de données inaccessible**

- Vérifier que PostgreSQL est démarré
- Vérifier les paramètres de connexion dans `application.properties`
- Vérifier que la base `econsulat` existe

### **Problème 5: Données de référence manquantes**

```sql
-- Insérer des données de test si nécessaire
INSERT INTO civilites (libelle) VALUES ('Monsieur'), ('Madame');
INSERT INTO pays (nom) VALUES ('France'), ('Belgique');
INSERT INTO document_types (libelle, is_active) VALUES ('Passeport', true);
```

## 📋 Ordre des Opérations

1. **Vérifier l'état** → `test_connectivite_simple.bat`
2. **Démarrer le backend** → `start_backend_with_checks.bat`
3. **Tester l'API** → `test_api_powershell.ps1`
4. **Vérifier la BDD** → `test_database_structure.sql`
5. **Tester la création** → Via PowerShell ou interface web

## ✅ Indicateurs de Succès

- Backend accessible sur http://localhost:8080
- Authentification réussie avec token JWT
- Données de référence chargées (civilités, pays, types)
- Création de demande réussie avec ID retourné
- Plus d'erreurs CORS dans la console

## 🚨 En Cas d'Échec

### **Vérifier les Logs Backend**

- Regarder la console où le backend est démarré
- Chercher les erreurs de démarrage
- Vérifier les erreurs de base de données

### **Vérifier la Configuration**

- `application.properties` : paramètres de base de données
- `SecurityConfig.java` : configuration CORS
- `pom.xml` : dépendances Maven

### **Tester avec des Outils Externes**

- **Postman** : Tester les endpoints API
- **cURL** : Tests en ligne de commande
- **Navigateur** : Vérifier http://localhost:8080

## 📚 Fichiers de Diagnostic

- `test_connectivite_simple.bat` - Test réseau basique
- `test_api_powershell.ps1` - Test API complet
- `start_backend_with_checks.bat` - Démarrage avec vérifications
- `test_database_structure.sql` - Vérification base de données
- `restart_with_cors_fix.bat` - Redémarrage avec correction CORS

## 🎯 Objectif Final

Après avoir suivi ce guide:

- ✅ Backend fonctionnel sur le port 8080
- ✅ API accessible et authentification fonctionnelle
- ✅ Création de demandes opérationnelle
- ✅ Plus d'erreurs CORS
- ✅ Interface web fonctionnelle

---

**Note:** Ce guide remplace les tests HTML qui ne peuvent pas fonctionner sans backend actif. Utilisez PowerShell et les scripts batch pour diagnostiquer et résoudre les problèmes.
