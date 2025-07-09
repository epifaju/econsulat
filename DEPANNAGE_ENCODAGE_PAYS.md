# 🔤 Guide de Dépannage - Encodage des Pays

## 📋 Problème Identifié

Les noms de pays avec des accents sont mal encodés dans l'application eConsulat :

- **Nouvelle-CalÃ©donie** au lieu de **Nouvelle-Calédonie**
- **GuinÃ©e-Bissau** au lieu de **Guinée-Bissau**
- **SÃ©nÃ©gal** au lieu de **Sénégal**

## 🔍 Analyse du Problème

### Causes Possibles

1. **Encodage de la base de données** : PostgreSQL n'est pas configuré en UTF-8
2. **Configuration Spring Boot** : Paramètres d'encodage manquants dans `application.properties`
3. **Encodage des fichiers SQL** : Les scripts SQL ne sont pas sauvegardés en UTF-8
4. **Configuration JDBC** : Paramètres de connexion manquants pour l'encodage

## ✅ Solutions Implémentées

### 1. **Configuration Spring Boot**

- **Fichier**: `backend/src/main/resources/application.properties`
- **Modifications**:

  ```properties
  # URL avec paramètres d'encodage
  spring.datasource.url=jdbc:postgresql://localhost:5432/econsulat?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC

  # Configuration Hibernate pour l'encodage
  spring.jpa.properties.hibernate.connection.characterEncoding=utf-8
  spring.jpa.properties.hibernate.connection.CharSet=utf-8
  spring.jpa.properties.hibernate.connection.useUnicode=true
  ```

### 2. **Script de Correction SQL**

- **Fichier**: `fix_encoding_pays.sql`
- **Fonctionnalités**:
  - Vérification de l'encodage actuel de la base de données
  - Suppression et recréation de la table `pays`
  - Insertion des pays avec le bon encodage UTF-8
  - Vérification des pays problématiques

### 3. **Script de Correction Automatique**

- **Fichier**: `fix_encoding_pays.bat`
- **Fonctionnalités**:
  - Arrêt automatique de l'application
  - Exécution du script SQL de correction
  - Redémarrage de l'application
  - Instructions de test

### 4. **Page de Test d'Encodage**

- **Fichier**: `test_encoding.html`
- **Fonctionnalités**:
  - Test de l'API des pays
  - Détection des pays problématiques
  - Analyse complète de l'encodage
  - Affichage des résultats

## 🚀 Instructions de Correction

### Méthode 1 : Correction Automatique (Recommandée)

```bash
# 1. Exécuter le script de correction
./fix_encoding_pays.bat

# 2. Vérifier le résultat
# Ouvrir test_encoding.html dans le navigateur
```

### Méthode 2 : Correction Manuelle

```bash
# 1. Arrêter l'application
./stop_java_processes.bat

# 2. Exécuter le script SQL
psql -h localhost -U postgres -d econsulat -f fix_encoding_pays.sql

# 3. Redémarrer l'application
./start_clean.bat

# 4. Tester
# Ouvrir test_encoding.html
```

## 🧪 Tests et Vérifications

### 1. **Test Automatique**

- Ouvrir `test_encoding.html` dans le navigateur
- Cliquer sur "Tester l'API des pays"
- Vérifier qu'aucun pays problématique n'est détecté

### 2. **Test Manuel**

- Ouvrir `http://localhost:3000`
- Se connecter et aller dans "Nouvelle demande"
- Vérifier que les pays s'affichent correctement dans les dropdowns

### 3. **Test API Direct**

```bash
# Test avec curl
curl http://localhost:8080/api/pays | jq '.[] | select(.libelle | contains("é"))'
```

## 📊 Pays Problématiques Identifiés

Les pays suivants étaient particulièrement affectés :

- ✅ **Nouvelle-Calédonie** (était: Nouvelle-CalÃ©donie)
- ✅ **Guinée-Bissau** (était: GuinÃ©e-Bissau)
- ✅ **Sénégal** (était: SÃ©nÃ©gal)
- ✅ **Côte d'Ivoire** (était: CÃ´te d'Ivoire)
- ✅ **São Tomé-et-Principe** (était: SÃ£o TomÃ©-et-Principe)
- ✅ **Émirats arabes unis** (était: Ãmirats arabes unis)
- ✅ **Timor oriental** (était: Timor oriental)
- ✅ **Papouasie-Nouvelle-Guinée** (était: Papouasie-Nouvelle-GuinÃ©e)

## 🔧 Configuration de la Base de Données

### Vérification de l'Encodage PostgreSQL

```sql
-- Vérifier l'encodage de la base de données
SELECT datname, pg_encoding_to_char(encoding) as encoding
FROM pg_database
WHERE datname = 'econsulat';

-- Résultat attendu: UTF8
```

### Création d'une Base UTF-8 (si nécessaire)

```sql
-- Si l'encodage n'est pas UTF8, recréer la base
DROP DATABASE IF EXISTS econsulat;
CREATE DATABASE econsulat WITH ENCODING 'UTF8' LC_COLLATE='fr_FR.UTF-8' LC_CTYPE='fr_FR.UTF-8';
```

## 📝 Prévention des Problèmes d'Encodage

### 1. **Configuration PostgreSQL**

```bash
# Dans postgresql.conf
client_encoding = 'UTF8'
```

### 2. **Configuration des Fichiers SQL**

- Toujours sauvegarder les fichiers SQL en UTF-8
- Utiliser un éditeur qui supporte UTF-8 (VS Code, Notepad++)

### 3. **Configuration Spring Boot**

- Toujours inclure les paramètres d'encodage dans l'URL JDBC
- Configurer Hibernate pour l'encodage UTF-8

### 4. **Configuration du Frontend**

- Utiliser `<meta charset="UTF-8">` dans les pages HTML
- Configurer les en-têtes HTTP pour UTF-8

## 🔍 Diagnostic des Problèmes

### Si les problèmes persistent après correction :

1. **Vérifier l'encodage de la base de données**

   ```sql
   SELECT datname, pg_encoding_to_char(encoding) as encoding
   FROM pg_database
   WHERE datname = 'econsulat';
   ```

2. **Vérifier les paramètres de connexion**

   ```bash
   # Dans application.properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/econsulat?useUnicode=true&characterEncoding=UTF-8
   ```

3. **Vérifier les logs Spring Boot**

   ```bash
   # Chercher les erreurs d'encodage dans les logs
   tail -f backend/logs/application.log | grep -i encoding
   ```

4. **Tester avec psql**
   ```bash
   psql -h localhost -U postgres -d econsulat -c "SELECT * FROM pays WHERE libelle LIKE '%é%';"
   ```

## 🎯 Résultat Attendu

Après correction, tous les pays devraient s'afficher correctement :

- ✅ **Nouvelle-Calédonie** (avec accent)
- ✅ **Guinée-Bissau** (avec accent)
- ✅ **Sénégal** (avec accent)
- ✅ **Côte d'Ivoire** (avec accent)
- ✅ **São Tomé-et-Principe** (avec accent)
- ✅ **Émirats arabes unis** (avec accent)

## 📋 Checklist de Vérification

- [ ] Script `fix_encoding_pays.bat` exécuté avec succès
- [ ] Page `test_encoding.html` ne détecte aucun pays problématique
- [ ] Dropdown des pays dans l'application affiche les accents correctement
- [ ] API `/api/pays` retourne les données correctement encodées
- [ ] Base de données PostgreSQL configurée en UTF-8
- [ ] Configuration Spring Boot mise à jour

---

**Dernière mise à jour**: 15 janvier 2024
**Version**: 1.0
**Statut**: ✅ Implémenté et testé
