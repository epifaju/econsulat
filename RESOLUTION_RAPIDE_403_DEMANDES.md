# 🚀 Résolution Rapide - Erreur 403 sur /api/demandes

## 📊 **Analyse des Logs Serveur**

**✅ Ce qui fonctionne :**

- Authentification JWT : **FONCTIONNE**
- Utilisateur : Epifanio Gonçalves (ROLE_USER)
- Endpoints accessibles : `/api/notifications/my`
- Token valide et autorités correctes

**❌ Problème identifié :**

- Seul l'endpoint `/api/demandes` retourne 403 Forbidden
- L'utilisateur est authentifié mais n'a pas l'autorisation

## 🎯 **Diagnostic Rapide**

### **1. Test Immédiat**

Ouvrez `diagnostic_rapide_403.html` et testez avec :

- **Email :** `guinebissauanuncios@gmail.com`
- **Mot de passe :** `password123`

### **2. Vérification Automatique**

Exécutez `fix_403_demande_cible.bat` pour :

- Analyser la configuration de sécurité
- Identifier le problème exact
- Appliquer les corrections

## 🔍 **Cause Probable**

**Configuration de sécurité trop restrictive dans `SecurityConfig.java`**

L'endpoint `/api/demandes` n'est probablement pas autorisé pour le rôle `USER`.

## 🛠️ **Solution Immédiate**

### **Option 1: Correction Automatique**

```bash
# Windows
fix_403_demande_cible.bat

# Le script va :
# 1. Analyser SecurityConfig.java
# 2. Identifier les règles manquantes
# 3. Redémarrer le backend
# 4. Tester la correction
```

### **Option 2: Correction Manuelle**

**Fichier :** `backend/src/main/java/com/econsulat/config/SecurityConfig.java`

**Ajoutez ces règles :**

```java
.authorizeHttpRequests(auth -> auth
    .requestMatchers("/api/auth/**").permitAll()
    .requestMatchers("/api/demandes/document-types").permitAll()
    .requestMatchers("/api/demandes/civilites").permitAll()
    .requestMatchers("/api/demandes/pays").permitAll()
    // ✅ AJOUTER CETTE LIGNE :
    .requestMatchers("/api/demandes/**").hasAnyRole("USER", "ADMIN", "AGENT")
    .anyRequest().authenticated())
```

**Ou plus spécifiquement :**

```java
.requestMatchers("/api/demandes").hasAnyRole("USER", "ADMIN", "AGENT")
.requestMatchers("/api/demandes/my").hasAnyRole("USER", "ADMIN", "AGENT")
```

## 🔄 **Redémarrage et Test**

### **1. Redémarrer le Backend**

```bash
# Arrêter (Ctrl+C)
# Puis redémarrer
cd backend
mvn spring-boot:run
```

### **2. Test de Validation**

```bash
# Test des endpoints publics
curl http://127.0.0.1:8080/api/demandes/civilites
curl http://127.0.0.1:8080/api/demandes/pays
curl http://127.0.0.1:8080/api/demandes/document-types

# Test avec authentification
curl -H "Authorization: Bearer <TOKEN>" \
  http://127.0.0.1:8080/api/demandes/my
```

## 🧪 **Test Complet**

### **1. Diagnostic Rapide**

- Ouvrir `diagnostic_rapide_403.html`
- Connexion automatique avec les identifiants des logs
- Test de tous les endpoints
- Vérification des permissions

### **2. Test Frontend**

- Retourner à l'application eConsulat
- Tenter de créer une nouvelle demande
- Vérifier que l'erreur 403 ne se reproduit plus

## 🔧 **Vérifications Supplémentaires**

### **1. Comparaison avec /api/notifications**

```java
// Si /api/notifications fonctionne, copiez sa configuration :
.requestMatchers("/api/notifications/**").hasAnyRole("USER", "ADMIN", "AGENT")
.requestMatchers("/api/demandes/**").hasAnyRole("USER", "ADMIN", "AGENT")
```

### **2. Vérification des Annotations**

**Fichier :** `DemandeController.java`

```java
@RestController
@RequestMapping("/api/demandes")
@CrossOrigin(origins = "*")
public class DemandeController {

    @PostMapping
    // ✅ Ajouter si nécessaire :
    // @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'AGENT')")
    public ResponseEntity<DemandeResponse> createDemande(@RequestBody DemandeRequest request) {
        // ... code existant
    }
}
```

### **3. Vérification de la Base de Données**

```sql
-- Vérifier que l'utilisateur a le bon rôle
SELECT email, role, enabled, email_verified
FROM users
WHERE email = 'guinebissauanuncios@gmail.com';

-- Résultat attendu :
-- role: USER
-- enabled: true
-- email_verified: true
```

## 📋 **Checklist de Résolution**

- [ ] **Diagnostic rapide** avec `diagnostic_rapide_403.html`
- [ ] **Exécution** de `fix_403_demande_cible.bat`
- [ ] **Vérification** de `SecurityConfig.java`
- [ ] **Ajout** des règles pour `/api/demandes`
- [ ] **Redémarrage** du backend
- [ ] **Test** de création de demande
- [ ] **Vérification** que l'erreur 403 ne se reproduit plus

## 🆘 **Si le Problème Persiste**

### **1. Vérifier les Logs Après Correction**

```bash
# Dans le terminal du backend, chercher :
# - "Secured POST /api/demandes"
# - "Authorized method invocation"
# - Erreurs de sécurité
```

### **2. Comparer avec /api/notifications**

```bash
# Analyser pourquoi /api/notifications fonctionne
# Copier sa configuration pour /api/demandes
```

### **3. Vérifier les Dépendances**

```xml
<!-- Dans pom.xml, vérifier : -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

## 📚 **Fichiers de Référence**

- **`diagnostic_rapide_403.html`** - Test rapide et ciblé
- **`fix_403_demande_cible.bat`** - Correction automatique
- **`RESOLUTION_ERREUR_403_CREATION_DEMANDE.md`** - Guide complet

## ⏱️ **Temps de Résolution Estimé**

- **Diagnostic :** 2-3 minutes
- **Correction :** 5-10 minutes
- **Test :** 2-3 minutes
- **Total :** 10-15 minutes

---

**Note :** Cette résolution rapide est basée sur l'analyse des logs qui montrent que l'authentification fonctionne. Le problème est probablement dans la configuration de sécurité spécifiquement pour l'endpoint `/api/demandes`.
