# 🔧 Dépannage - Erreur 400 lors de la génération de documents de passeport

## 🚨 Problème identifié

L'erreur 400 (Bad Request) lors de la génération de documents de passeport peut être causée par plusieurs problèmes liés aux données ou à la configuration.

## 🔍 Causes possibles et solutions

### 1. Données du citoyen manquantes ou invalides

#### Symptômes

- Erreur 400 avec message "Données invalides"
- Messages spécifiques sur les champs manquants

#### Solutions

**Vérifier que tous les champs obligatoires sont remplis :**

- Prénom (firstName)
- Nom de famille (lastName)
- Date de naissance (birthDate)
- Lieu de naissance (birthPlace)

**Utiliser le diagnostic :**

1. Ouvrir `debug_passport_400.html`
2. Se connecter en tant qu'admin
3. Récupérer la liste des citoyens
4. Diagnostiquer un citoyen spécifique

### 2. Template Word manquant

#### Symptômes

- Erreur 400 avec message "Template de passeport non trouvé"

#### Solutions

**Vérifier l'emplacement du template :**

```bash
# Le fichier doit être dans
backend/src/main/resources/FORMULARIO DE PEDIDO DE PASSAPORTE.docx
```

**Copier le template :**

```bash
# Depuis la racine du projet
copy "FORMULARIO DE PEDIDO DE PASSAPORTE.docx" "backend/src/main/resources/"
```

### 3. ID de citoyen invalide

#### Symptômes

- Erreur 400 avec message "ID de citoyen invalide"
- Erreur 404 avec message "Citoyen non trouvé"

#### Solutions

**Vérifier que l'ID existe :**

1. Utiliser l'interface admin pour voir la liste des citoyens
2. Noter l'ID d'un citoyen valide
3. Utiliser cet ID pour la génération

### 4. Problèmes de permissions

#### Symptômes

- Erreur 500 lors de la création du fichier
- Messages d'erreur liés aux permissions

#### Solutions

**Vérifier les permissions du dossier uploads :**

```bash
# Créer le dossier s'il n'existe pas
mkdir -p backend/uploads

# Vérifier les permissions
ls -la backend/uploads/
```

## 🧪 Diagnostic étape par étape

### 1. Utiliser le fichier de diagnostic

Ouvrir `debug_passport_400.html` et suivre les étapes :

1. **Authentification** - Vérifier la connexion admin
2. **Récupération des citoyens** - Voir la liste complète
3. **Diagnostic d'un citoyen** - Vérifier les données
4. **Test de génération** - Identifier l'erreur exacte
5. **Vérification du template** - S'assurer qu'il existe
6. **Vérification des permissions** - Tester l'écriture

### 2. Vérifier les logs du backend

```bash
# Suivre les logs en temps réel
tail -f backend/logs/application.log

# Chercher les erreurs spécifiques
grep -i "error\|exception\|400" backend/logs/application.log
```

### 3. Test manuel via curl

```bash
# 1. Se connecter
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# 2. Tester la génération (remplacer YOUR_TOKEN et CITIZEN_ID)
curl -X POST http://localhost:8080/api/passport/generate/CITIZEN_ID \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -v
```

## ✅ Corrections apportées

### 1. Validation des données améliorée

**Fichier :** `backend/src/main/java/com/econsulat/service/PassportDocumentService.java`

**Ajout de la méthode `validateCitizenData()` :**

```java
private void validateCitizenData(Citizen citizen) {
    if (citizen == null) {
        throw new IllegalArgumentException("Le citoyen ne peut pas être null");
    }
    if (citizen.getId() == null) {
        throw new IllegalArgumentException("L'ID du citoyen ne peut pas être null");
    }
    if (citizen.getFirstName() == null || citizen.getFirstName().trim().isEmpty()) {
        throw new IllegalArgumentException("Le prénom du citoyen ne peut pas être vide");
    }
    // ... autres validations
}
```

### 2. Gestion d'erreurs améliorée

**Fichier :** `backend/src/main/java/com/econsulat/controller/PassportController.java`

**Amélioration de la gestion des exceptions :**

```java
} catch (IllegalArgumentException e) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body("Données invalides: " + e.getMessage());
} catch (IOException e) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body("Erreur lors de la génération du document: " + e.getMessage());
} catch (Exception e) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body("Erreur inattendue: " + e.getMessage());
}
```

### 3. Messages d'erreur plus détaillés

- Validation de l'ID de citoyen
- Messages spécifiques pour chaque type d'erreur
- Indication de l'emplacement attendu du template

## 🚀 Procédure de résolution

### Étape 1 : Diagnostic

1. Ouvrir `debug_passport_400.html`
2. Suivre toutes les étapes de diagnostic
3. Noter l'erreur exacte retournée

### Étape 2 : Correction selon l'erreur

**Si "Données invalides" :**

- Vérifier que le citoyen a tous les champs obligatoires
- Utiliser l'interface admin pour corriger les données

**Si "Template non trouvé" :**

- Copier le template dans `backend/src/main/resources/`
- Redémarrer l'application

**Si "Citoyen non trouvé" :**

- Vérifier l'ID utilisé
- Utiliser un ID valide depuis la liste des citoyens

**Si erreur de permissions :**

- Créer le dossier `backend/uploads/`
- Vérifier les permissions d'écriture

### Étape 3 : Test

1. Redémarrer l'application si nécessaire
2. Tester à nouveau la génération
3. Vérifier que le document est créé dans `backend/uploads/`

## 📋 Checklist de vérification

- [ ] Template Word présent dans `backend/src/main/resources/`
- [ ] Dossier `backend/uploads/` existe et accessible en écriture
- [ ] Citoyen sélectionné a tous les champs obligatoires
- [ ] ID de citoyen valide et existant
- [ ] Authentification admin réussie
- [ ] Application redémarrée après modifications

## 🔧 En cas de problème persistant

### 1. Vérifier la base de données

```sql
-- Vérifier les données d'un citoyen
SELECT id, first_name, last_name, birth_date, birth_place
FROM citizens
WHERE id = YOUR_CITIZEN_ID;
```

### 2. Tester avec des données minimales

Créer un citoyen de test avec toutes les données requises.

### 3. Vérifier les dépendances Maven

```bash
cd backend
mvn clean compile
```

## 📞 Support

Si le problème persiste après ces étapes :

1. Utiliser `debug_passport_400.html` pour obtenir l'erreur exacte
2. Vérifier les logs du backend
3. Contacter l'équipe de développement avec les détails de l'erreur
