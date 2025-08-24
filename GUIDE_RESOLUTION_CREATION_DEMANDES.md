# 🔧 Guide de Résolution - Problèmes de Création de Demandes

## 📋 Problème Identifié

La création de demandes ne fonctionne plus à cause d'une **incohérence entre le modèle Java et la structure de la base de données**.

### 🚨 Causes Principales

1. **Colonnes manquantes** dans la table `demandes`
2. **Contraintes de clé étrangère** non définies
3. **Tables de référence** manquantes ou incomplètes
4. **Migration incomplète** de l'ancien système vers le nouveau

## 🛠️ Solutions Disponibles

### Solution 1 : Correction Automatique (Recommandée)

Exécutez le script de correction automatique :

```bash
# Windows
fix_demande_creation_automatic.bat

# Linux/Mac
psql -h localhost -U postgres -d econsulat -f fix_demande_creation_automatic.sql
```

### Solution 2 : Diagnostic Manuel

Exécutez le script de diagnostic pour identifier les problèmes :

```bash
psql -h localhost -U postgres -d econsulat -f diagnostic_creation_demande.sql
```

### Solution 3 : Test de Création

Exécutez le script de test pour vérifier la création :

```bash
psql -h localhost -U postgres -d econsulat -f test_creation_demande.sql
```

## 🔍 Détails Techniques

### Structure Attendue de la Table `demandes`

```sql
CREATE TABLE demandes (
    id SERIAL PRIMARY KEY,
    civilite_id INTEGER NOT NULL REFERENCES civilites(id),
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    birth_date DATE NOT NULL,
    birth_place VARCHAR(100) NOT NULL,
    birth_country_id INTEGER NOT NULL REFERENCES pays(id),
    adresse_id INTEGER NOT NULL REFERENCES adresses(id),
    father_first_name VARCHAR(100) NOT NULL,
    father_last_name VARCHAR(100) NOT NULL,
    father_birth_date DATE NOT NULL,
    father_birth_place VARCHAR(100) NOT NULL,
    father_birth_country_id INTEGER NOT NULL REFERENCES pays(id),
    mother_first_name VARCHAR(100) NOT NULL,
    mother_last_name VARCHAR(100) NOT NULL,
    mother_birth_date DATE NOT NULL,
    mother_birth_place VARCHAR(100) NOT NULL,
    mother_birth_country_id INTEGER NOT NULL REFERENCES pays(id),
    document_type_id INTEGER NOT NULL REFERENCES document_types(id),
    documents_path TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    user_id INTEGER NOT NULL REFERENCES users(id)
);
```

### Tables de Référence Requises

- ✅ `civilites` - Civilités (Monsieur, Madame, Mademoiselle)
- ✅ `pays` - Liste des pays
- ✅ `adresses` - Adresses des demandeurs
- ✅ `document_types` - Types de documents (Passeport, Acte de naissance, etc.)
- ✅ `users` - Utilisateurs du système

## 📝 Étapes de Résolution

### Étape 1 : Vérification de l'État Actuel

1. **Exécuter le diagnostic** :

   ```bash
   psql -h localhost -U postgres -d econsulat -f diagnostic_creation_demande.sql
   ```

2. **Identifier les problèmes** :
   - Colonnes manquantes
   - Tables manquantes
   - Contraintes manquantes

### Étape 2 : Correction Automatique

1. **Exécuter la correction** :

   ```bash
   fix_demande_creation_automatic.bat
   ```

2. **Vérifier les résultats** :
   - Messages de succès ✅
   - Messages d'erreur ❌

### Étape 3 : Test de Validation

1. **Tester la création** :

   ```bash
   psql -h localhost -U postgres -d econsulat -f test_creation_demande.sql
   ```

2. **Vérifier les résultats** :
   - Adresse créée avec succès
   - Demande créée avec succès

### Étape 4 : Redémarrage de l'Application

1. **Arrêter l'application** si elle tourne
2. **Redémarrer le backend** :
   ```bash
   cd backend
   mvn spring-boot:run
   ```

## 🚨 Problèmes Courants et Solutions

### Problème : "Colonne document_type_id n'existe pas"

**Solution** : Exécuter le script de correction automatique

### Problème : "Contrainte de clé étrangère violée"

**Solution** : Vérifier que les tables de référence contiennent des données

### Problème : "Séquence n'existe pas"

**Solution** : Le script de correction crée automatiquement les séquences

### Problème : "Permissions insuffisantes"

**Solution** : Vérifier que l'utilisateur PostgreSQL a les droits d'administration

## 🔧 Vérifications Post-Correction

### 1. Structure de la Base

```sql
-- Vérifier la structure de la table demandes
SELECT column_name, data_type, is_nullable
FROM information_schema.columns
WHERE table_name = 'demandes'
ORDER BY ordinal_position;
```

### 2. Contraintes de Clé Étrangère

```sql
-- Vérifier les contraintes
SELECT
    tc.constraint_name,
    kcu.column_name,
    ccu.table_name AS referenced_table
FROM information_schema.table_constraints tc
JOIN information_schema.key_column_usage kcu
    ON tc.constraint_name = kcu.constraint_name
JOIN information_schema.constraint_column_usage ccu
    ON ccu.constraint_name = tc.constraint_name
WHERE tc.constraint_type = 'FOREIGN KEY'
    AND tc.table_name = 'demandes';
```

### 3. Données de Référence

```sql
-- Vérifier les types de documents
SELECT * FROM document_types WHERE is_active = true;

-- Vérifier les civilités
SELECT * FROM civilites;

-- Vérifier les pays (premiers 10)
SELECT id, libelle FROM pays ORDER BY id LIMIT 10;
```

## 📊 Monitoring et Logs

### Logs de l'Application

Vérifiez les logs du backend pour identifier les erreurs :

```properties
# Dans application.properties
logging.level.com.econsulat=DEBUG
logging.level.org.springframework.transaction=DEBUG
```

### Logs de la Base de Données

Vérifiez les logs PostgreSQL pour les erreurs de contrainte :

```sql
-- Activer les logs détaillés
ALTER SYSTEM SET log_statement = 'all';
SELECT pg_reload_conf();
```

## 🎯 Résultats Attendus

Après la correction, vous devriez voir :

1. ✅ **Structure de base correcte** - Toutes les colonnes requises existent
2. ✅ **Contraintes de clé étrangère** - Toutes les relations sont définies
3. ✅ **Tables de référence** - Données de base insérées
4. ✅ **Test de création** - Adresse et demande créées avec succès
5. ✅ **Application fonctionnelle** - Création de demandes opérationnelle

## 🆘 Support et Dépannage

### Si la Correction Échoue

1. **Vérifier la connexion PostgreSQL** :

   ```bash
   psql -h localhost -U postgres -d econsulat -c "SELECT version();"
   ```

2. **Vérifier les permissions** :

   ```bash
   psql -h localhost -U postgres -d econsulat -c "\du"
   ```

3. **Vérifier l'espace disque** :
   ```bash
   df -h
   ```

### Logs d'Erreur Courants

- **"relation does not exist"** → Table manquante
- **"column does not exist"** → Colonne manquante
- **"foreign key constraint"** → Contrainte manquante
- **"sequence does not exist"** → Séquence manquante

## 📞 Contact et Support

En cas de problème persistant :

1. **Exécuter le diagnostic complet**
2. **Consulter les logs d'erreur**
3. **Vérifier la version de PostgreSQL**
4. **Contacter l'équipe technique**

---

**⚠️ Important** : Toujours faire une sauvegarde de la base de données avant d'exécuter des scripts de correction.

**✅ Recommandation** : Commencer par la correction automatique, puis valider avec les tests.
