# 🔧 Résolution Complète - Erreur 403 lors de la Création de Demande

## 🚨 **Problème Identifié**

**Erreur :** `POST http://127.0.0.1:8080/api/demandes [HTTP/1.1 403 86ms]`

**Symptômes :**

- Erreur 403 (Forbidden) lors de la création de demande
- Problème de parsing JSON : "SyntaxError: JSON.parse: unexpected end of data at line 1 column 1"
- L'endpoint `/api/demandes` n'est pas accessible
- L'utilisateur connecté reçoit une erreur d'autorisation

## 🔍 **Causes Possibles**

### 1. **Problème d'Authentification JWT**

- Token JWT invalide ou expiré
- Problème dans le filtre d'authentification
- Utilisateur non authentifié

### 2. **Problème de Configuration de Sécurité**

- Endpoint `/api/demandes` non autorisé
- Règles de sécurité trop restrictives
- Problème de CORS

### 3. **Problème de Structure de Base de Données**

- Colonnes manquantes dans les tables
- Contraintes de clés étrangères manquantes
- Données de référence manquantes

### 4. **Problème de Relations JPA**

- Relations `@ManyToOne` non initialisées
- Problème de lazy loading
- Entités liées non trouvées

## 🛠️ **Solutions par Ordre de Priorité**

### **Étape 1: Diagnostic Complet**

Utiliser le fichier `diagnostic_erreur_403_demande.html` pour identifier le problème exact :

1. **Ouvrir le fichier** dans un navigateur
2. **Se connecter** avec un compte utilisateur (ex: `citizen@econsulat.com` / `citizen123`)
3. **Vérifier l'authentification** via `/api/auth/me`
4. **Récupérer les données de référence**
5. **Tester la création** de demande
6. **Analyser les logs** et erreurs

### **Étape 2: Correction Automatique**

Exécuter le script de correction automatique :

```bash
# Windows
fix_erreur_403_demande.bat

# Linux/Mac
./fix_erreur_403_demande.sh
```

**Ce script va :**

- ✅ Vérifier la structure de la base de données
- ✅ Vérifier la configuration de sécurité
- ✅ Redémarrer le backend
- ✅ Tester les endpoints

### **Étape 3: Correction Manuelle de la Configuration de Sécurité**

**Fichier :** `backend/src/main/java/com/econsulat/config/SecurityConfig.java`

**Vérifier que :**

```java
.authorizeHttpRequests(auth -> auth
    .requestMatchers("/api/auth/**").permitAll()
    .requestMatchers("/api/demandes/document-types").permitAll()
    .requestMatchers("/api/demandes/civilites").permitAll()
    .requestMatchers("/api/demandes/pays").permitAll()
    .anyRequest().authenticated()) // ← /api/demandes tombe ici
```

**Si le problème persiste, ajouter explicitement :**

```java
.requestMatchers("/api/demandes").authenticated()
```

### **Étape 4: Vérification du Filtre JWT**

**Fichier :** `backend/src/main/java/com/econsulat/config/JwtAuthenticationFilter.java`

**Vérifier les logs :**

```
🔍 JWT Filter - URL: /api/demandes
🔍 JWT Filter - Authorization header: Bearer <token>
🔍 JWT Filter - Username extrait: <email>
🔍 JWT Filter - UserDetails chargé: <email>
✅ JWT Filter - Token valide pour: <email>
🔐 JWT Filter - Authentification établie dans SecurityContext
```

**Si les logs montrent des erreurs :**

- Vérifier la validité du token
- Vérifier la configuration JWT
- Vérifier que l'utilisateur existe en base

### **Étape 5: Vérification des Relations JPA**

**Fichier :** `backend/src/main/java/com/econsulat/model/Demande.java`

**Vérifier que toutes les relations sont correctement définies :**

```java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "document_type_id", nullable = false)
private DocumentType documentType;

@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "civilite_id", nullable = false)
private Civilite civilite;

@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
@JoinColumn(name = "adresse_id", nullable = false)
private Adresse adresse;
```

### **Étape 6: Vérification du Service de Création**

**Fichier :** `backend/src/main/java/com/econsulat/service/DemandeService.java`

**Vérifier que :**

- L'utilisateur est correctement récupéré depuis le token
- Toutes les entités liées sont trouvées
- Les validations passent
- La transaction se déroule correctement

## 🧪 **Tests de Validation**

### **Test 1: Vérification de l'Authentification**

```bash
# Test de connexion
curl -X POST http://127.0.0.1:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"citizen@econsulat.com","password":"citizen123"}'

# Test du token
curl -H "Authorization: Bearer <TOKEN>" \
  http://127.0.0.1:8080/api/auth/me
```

### **Test 2: Vérification des Endpoints**

```bash
# Endpoints publics (doivent être accessibles)
curl http://127.0.0.1:8080/api/demandes/civilites
curl http://127.0.0.1:8080/api/demandes/pays
curl http://127.0.0.1:8080/api/demandes/document-types

# Endpoints protégés (doivent nécessiter une authentification)
curl -H "Authorization: Bearer <TOKEN>" \
  http://127.0.0.1:8080/api/demandes/my
```

### **Test 3: Création de Demande**

```bash
curl -X POST http://127.0.0.1:8080/api/demandes \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <TOKEN>" \
  -d '{
    "civiliteId": 1,
    "firstName": "Jean",
    "lastName": "Dupont",
    "birthDate": "1990-01-01",
    "birthPlace": "Paris",
    "birthCountryId": 1,
    "streetName": "Rue de la Paix",
    "streetNumber": "123",
    "postalCode": "75001",
    "city": "Paris",
    "countryId": 1,
    "fatherFirstName": "Pierre",
    "fatherLastName": "Dupont",
    "fatherBirthDate": "1950-01-01",
    "fatherBirthPlace": "Lyon",
    "fatherBirthCountryId": 1,
    "motherFirstName": "Marie",
    "motherLastName": "Martin",
    "motherBirthDate": "1955-01-01",
    "motherBirthPlace": "Marseille",
    "motherBirthCountryId": 1,
    "documentTypeId": 1,
    "documentFiles": []
  }'
```

## 🔍 **Diagnostic Avancé**

### **Vérification des Logs Backend**

Regarder les logs du backend pour identifier les erreurs :

```bash
# Dans le terminal du backend
tail -f logs/spring.log

# Ou dans la console Java
# Chercher les erreurs liées à :
# - JWT Filter
# - Security Context
# - User Details
# - Demande Service
```

### **Vérification de la Base de Données**

```sql
-- Vérifier la structure des tables
\d users
\d demandes
\d civilites
\d pays
\d document_types

-- Vérifier les données
SELECT * FROM users WHERE email = 'citizen@econsulat.com';
SELECT COUNT(*) FROM civilites;
SELECT COUNT(*) FROM pays;
SELECT COUNT(*) FROM document_types;

-- Vérifier les contraintes
SELECT * FROM information_schema.table_constraints
WHERE table_name IN ('users', 'demandes', 'civilites', 'pays', 'document_types');
```

### **Vérification du Frontend**

Dans la console du navigateur, vérifier :

```javascript
// Token stocké
console.log("Token:", localStorage.getItem("token"));

// Headers envoyés
console.log("Headers:", {
  "Content-Type": "application/json",
  Authorization: `Bearer ${token}`,
});

// Données envoyées
console.log("FormData:", formData);
```

## 🚀 **Solutions Rapides**

### **Solution 1: Redémarrage Complet**

```bash
# 1. Arrêter le backend
Ctrl+C

# 2. Arrêter le frontend
Ctrl+C

# 3. Vérifier que les ports sont libres
netstat -ano | findstr :8080
netstat -ano | findstr :5173

# 4. Redémarrer le backend
cd backend
mvn spring-boot:run

# 5. Redémarrer le frontend
cd frontend
npm run dev
```

### **Solution 2: Réinitialisation de la Base**

```bash
# 1. Arrêter le backend
# 2. Réinitialiser la base
psql -U postgres -d econsulat -f backend/src/main/resources/schema_demandes.sql

# 3. Recréer les données de référence
psql -U postgres -d econsulat -f insert_document_types.sql

# 4. Redémarrer le backend
```

### **Solution 3: Vérification des Rôles**

```sql
-- Vérifier le rôle de l'utilisateur
SELECT email, role, enabled, email_verified
FROM users
WHERE email = 'citizen@econsulat.com';

-- Si le rôle est incorrect, le corriger
UPDATE users
SET role = 'USER', enabled = true, email_verified = true
WHERE email = 'citizen@econsulat.com';
```

## 📚 **Documentation Supplémentaire**

### **Fichiers de Diagnostic**

- `diagnostic_erreur_403_demande.html` - Diagnostic complet
- `fix_erreur_403_demande.bat` - Correction automatique

### **Guides de Dépannage**

- `DEPANNAGE_ERREURS_LOGS.md` - Analyse des logs
- `DEPANNAGE_MAVEN.md` - Problèmes Maven
- `RESOLUTION_RAPIDE.md` - Solutions rapides

### **Tests de Validation**

- `test_demande_creation_debug.html` - Test de création
- `test_auth_diagnostic.html` - Diagnostic d'authentification

## 🎯 **Vérification Finale**

Après application des corrections, vérifier que :

1. ✅ L'utilisateur peut se connecter
2. ✅ Le token JWT est valide
3. ✅ Les données de référence sont accessibles
4. ✅ La création de demande fonctionne
5. ✅ Aucune erreur 403 n'apparaît
6. ✅ Les logs du backend sont propres

## 🆘 **En Cas de Problème Persistant**

Si l'erreur 403 persiste après toutes les corrections :

1. **Vérifier les logs complets** du backend
2. **Comparer avec une installation propre** de l'application
3. **Vérifier la version** de Spring Boot et des dépendances
4. **Consulter la documentation** officielle de Spring Security
5. **Créer un ticket** avec tous les détails du problème

---

**Note :** Ce guide couvre les causes les plus courantes de l'erreur 403. Si le problème persiste, il peut s'agir d'un problème plus spécifique nécessitant une analyse approfondie de l'environnement.
