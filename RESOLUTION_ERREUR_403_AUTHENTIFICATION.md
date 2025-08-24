# 🔐 Résolution de l'Erreur 403 (Forbidden) - eConsulat

## 📋 Problème identifié

Vous recevez une erreur **403 (Forbidden)** lors de la soumission du formulaire de demande. Cette erreur indique un problème d'autorisation dans le système d'authentification.

## 🔍 Diagnostic du problème

### 1. Analyse de l'erreur

L'erreur 403 se produit généralement pour l'une des raisons suivantes :

- **Token JWT invalide ou expiré**
- **Utilisateur sans rôle approprié**
- **Configuration de sécurité incorrecte**
- **Problème de base de données**

### 2. Vérifications préliminaires

#### A. Backend accessible ?

```bash
# Test de connexion au backend
curl -v http://127.0.0.1:8080/api/demandes/document-types
```

#### B. Base de données accessible ?

```bash
# Test de connexion à PostgreSQL
psql -h localhost -p 5432 -U postgres -d econsulat -c "SELECT 1;"
```

## 🛠️ Solutions étape par étape

### Étape 1 : Diagnostic complet

Exécutez le script de diagnostic :

```bash
# Windows
diagnostic_auth_403.bat

# Ou directement en SQL
psql -h localhost -p 5432 -U postgres -d econsulat -f diagnostic_auth_403.sql
```

### Étape 2 : Vérification des utilisateurs

#### A. Créer un utilisateur de test

```sql
-- Vérifier si l'utilisateur existe
SELECT * FROM users WHERE email = 'user@test.com';

-- Créer un utilisateur de test si nécessaire
INSERT INTO users (email, password, role, enabled, created_at, updated_at)
VALUES (
    'user@test.com',
    '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', -- password123
    'USER',
    true,
    NOW(),
    NOW()
);
```

#### B. Vérifier les rôles

```sql
-- Lister tous les rôles disponibles
SELECT DISTINCT role, COUNT(*) as count
FROM users
GROUP BY role
ORDER BY role;

-- Vérifier les utilisateurs sans rôle
SELECT id, email, role, enabled
FROM users
WHERE role IS NULL OR role = '';
```

### Étape 3 : Test d'authentification

#### A. Utiliser l'outil de diagnostic

1. Ouvrez `test_auth_diagnostic.html` dans votre navigateur
2. Testez la connexion au backend
3. Testez l'authentification avec `user@test.com` / `password123`
4. Testez la création de demande

#### B. Test manuel avec curl

```bash
# 1. Authentification
curl -X POST http://127.0.0.1:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"user@test.com","password":"password123"}'

# 2. Création de demande (avec le token reçu)
curl -X POST http://127.0.0.1:8080/api/demandes \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer VOTRE_TOKEN_ICI" \
  -d '{
    "civiliteId": 1,
    "firstName": "Test",
    "lastName": "Utilisateur",
    "birthDate": "1990-01-01",
    "birthPlace": "Paris",
    "birthCountryId": 1,
    "streetName": "Rue de Test",
    "streetNumber": "123",
    "postalCode": "75001",
    "city": "Paris",
    "countryId": 1,
    "fatherFirstName": "Jean",
    "fatherLastName": "Utilisateur",
    "fatherBirthDate": "1960-01-01",
    "fatherBirthPlace": "Lyon",
    "fatherBirthCountryId": 1,
    "motherFirstName": "Marie",
    "motherLastName": "Utilisateur",
    "motherBirthDate": "1965-01-01",
    "motherBirthPlace": "Marseille",
    "motherBirthCountryId": 1,
    "documentTypeId": 1,
    "documentFiles": []
  }'
```

### Étape 4 : Vérification de la configuration

#### A. Configuration de sécurité Spring

Vérifiez que `SecurityConfig.java` contient :

```java
.requestMatchers("/api/demandes/**").hasAnyRole("USER", "ADMIN", "AGENT")
```

#### B. Filtre JWT

Vérifiez que `JwtAuthenticationFilter.java` :

- Extrait correctement le token
- Valide le token
- Établit l'authentification dans le SecurityContext

### Étape 5 : Vérification des logs

#### A. Logs du backend

Regardez les logs Spring Boot pour identifier :

- Les tentatives d'authentification
- Les erreurs de validation JWT
- Les problèmes de base de données

#### B. Logs du frontend

Ouvrez la console du navigateur pour voir :

- Les erreurs de requête
- Les détails de la réponse 403

## 🚨 Problèmes courants et solutions

### Problème 1 : Token JWT invalide

**Symptômes :**

- Erreur 403 sur toutes les requêtes authentifiées
- Token vide ou malformé

**Solutions :**

```javascript
// Vérifier le token dans le localStorage
console.log("Token:", localStorage.getItem("token"));

// Vérifier les headers de la requête
console.log("Headers:", {
  "Content-Type": "application/json",
  Authorization: `Bearer ${localStorage.getItem("token")}`,
});
```

### Problème 2 : Utilisateur sans rôle

**Symptômes :**

- Authentification réussie mais erreur 403
- Utilisateur créé sans rôle

**Solutions :**

```sql
-- Corriger le rôle de l'utilisateur
UPDATE users
SET role = 'USER'
WHERE email = 'user@test.com' AND (role IS NULL OR role = '');

-- Vérifier que l'utilisateur est activé
UPDATE users
SET enabled = true
WHERE email = 'user@test.com';
```

### Problème 3 : Configuration de sécurité incorrecte

**Symptômes :**

- Erreur 403 même avec un token valide
- Problèmes de CORS

**Solutions :**

```java
// Vérifier la configuration CORS
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173"));
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
    configuration.setAllowedHeaders(Arrays.asList("*"));
    configuration.setAllowCredentials(true);
    return source;
}
```

## 🔧 Scripts de correction automatique

### Script batch Windows

```batch
@echo off
echo Correction automatique de l'erreur 403...

REM Redémarrer le backend
echo Redemarrage du backend...
cd backend
mvn spring-boot:run

REM Créer un utilisateur de test
echo Creation d'un utilisateur de test...
psql -h localhost -p 5432 -U postgres -d econsulat -f diagnostic_auth_403.sql

echo Correction terminee !
pause
```

### Script SQL de correction

```sql
-- Correction automatique des problèmes d'authentification
DO $$
BEGIN
    -- 1. Corriger les utilisateurs sans rôle
    UPDATE users
    SET role = 'USER'
    WHERE role IS NULL OR role = '';

    -- 2. Activer tous les utilisateurs
    UPDATE users
    SET enabled = true
    WHERE enabled = false;

    -- 3. Créer un utilisateur de test si nécessaire
    IF NOT EXISTS (SELECT 1 FROM users WHERE email = 'user@test.com') THEN
        INSERT INTO users (email, password, role, enabled, created_at, updated_at)
        VALUES (
            'user@test.com',
            '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.',
            'USER',
            true,
            NOW(),
            NOW()
        );
    END IF;

    RAISE NOTICE 'Correction automatique terminee';
END $$;
```

## 📊 Vérification de la résolution

### 1. Test de l'authentification

- ✅ Connexion au backend réussie
- ✅ Authentification réussie
- ✅ Token JWT reçu et valide

### 2. Test de la création de demande

- ✅ Requête POST vers `/api/demandes` réussie
- ✅ Pas d'erreur 403
- ✅ Demande créée en base de données

### 3. Vérification des logs

- ✅ Pas d'erreur d'authentification dans les logs
- ✅ Utilisateur correctement identifié
- ✅ Autorisations correctement appliquées

## 🆘 Si le problème persiste

### 1. Vérifications avancées

```bash
# Vérifier les processus Java
jps -l

# Vérifier les ports utilisés
netstat -ano | findstr :8080

# Vérifier les logs du backend
tail -f backend/logs/application.log
```

### 2. Redémarrage complet

```bash
# 1. Arrêter tous les processus Java
taskkill /F /IM java.exe

# 2. Redémarrer PostgreSQL
net stop postgresql-x64-15
net start postgresql-x64-15

# 3. Redémarrer le backend
cd backend
mvn clean spring-boot:run
```

### 3. Contact support

Si le problème persiste après toutes ces étapes :

- Collectez les logs du backend
- Collectez les logs du navigateur
- Documentez les étapes de reproduction
- Contactez l'équipe de développement

## 📝 Résumé des étapes

1. **🔍 Diagnostic** : Exécuter `diagnostic_auth_403.bat`
2. **👤 Vérification utilisateurs** : Créer des utilisateurs de test
3. **🔐 Test authentification** : Utiliser `test_auth_diagnostic.html`
4. **⚙️ Configuration** : Vérifier la configuration Spring Security
5. **🔧 Correction** : Appliquer les corrections automatiques
6. **✅ Vérification** : Tester la création de demande

---

**Note :** Ce guide couvre les causes les plus courantes de l'erreur 403. Si le problème persiste, il peut s'agir d'un problème plus complexe nécessitant une analyse approfondie du code.

