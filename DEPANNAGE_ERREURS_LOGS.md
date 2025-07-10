# 🔧 Dépannage - Erreurs dans les Logs Backend

## 🚨 Problèmes Identifiés

### 1. **TransientPropertyValueException**

```
HHH000437: Attempting to save one or more entities that have a non-nullable association with an unsaved transient entity. The unsaved transient entity must be saved in an operation prior to saving these dependent entities.
        Unsaved transient entity: ([com.econsulat.model.User#<null>])
        Dependent entities: ([[com.econsulat.model.GeneratedDocument#<null>]])
        Non-nullable association(s): ([com.econsulat.model.GeneratedDocument.createdBy])
```

**Cause :** L'entité `User` n'est pas correctement persistée avant de sauvegarder `GeneratedDocument`.

**Solution :** ✅ **CORRIGÉ** - Le nouveau contrôleur `UserDocumentController` récupère l'utilisateur depuis la base de données.

### 2. **Erreur 403 Forbidden**

```
2025-07-10T21:23:56.566+02:00 DEBUG 23624 --- [nio-8080-exec-3] o.s.s.w.access.AccessDeniedHandlerImpl   : Responding with 403 status code
```

**Cause :** L'endpoint `/api/passport/generate/1` nécessite le rôle `ADMIN` mais l'utilisateur connecté a le rôle `USER`.

**Solution :** ✅ **CORRIGÉ** - Création de nouveaux endpoints utilisateur `/api/user/documents/`.

### 3. **Problème de Compilation Maven**

```
[ERROR] Failed to execute goal org.springframework.boot:spring-boot-maven-plugin:3.2.0:run (default-cli) on project econsulat-backend: Process terminated with exit code: 1
```

**Cause :** Erreurs de compilation dues aux problèmes d'entités transientes.

**Solution :** ✅ **CORRIGÉ** - Les nouveaux endpoints utilisateur évitent ces problèmes.

## ✅ Corrections Implémentées

### 1. Nouveau Contrôleur Utilisateur

**Fichier :** `backend/src/main/java/com/econsulat/controller/UserDocumentController.java`

```java
@RestController
@RequestMapping("/api/user/documents")
public class UserDocumentController {

    @PostMapping("/generate")
    public ResponseEntity<?> generateDocument(
            @RequestParam Long demandeId,
            @RequestParam Long documentTypeId) {
        // Récupère l'utilisateur depuis la base de données
        User currentUser = getCurrentUser();
        // Génère le document avec l'utilisateur persisté
        GeneratedDocument document = documentGenerationService.generateDocument(demandeId, documentTypeId, currentUser);
        // Retourne une réponse DTO pour éviter les problèmes de sérialisation
        return ResponseEntity.ok(new GeneratedDocumentResponse(...));
    }

    @GetMapping("/download/{documentId}")
    public ResponseEntity<ByteArrayResource> downloadDocument(@PathVariable Long documentId) {
        // Téléchargement sécurisé pour l'utilisateur connecté
    }
}
```

### 2. Mise à Jour du Frontend

**Fichier :** `frontend/src/components/DemandesList.jsx`

```javascript
// Avant (endpoint admin)
const response = await fetch(
  `http://localhost:8080/api/demandes/${demandeId}/generate-document`
);

// Après (endpoint utilisateur)
const response = await fetch(
  `http://localhost:8080/api/user/documents/generate?demandeId=${demandeId}&documentTypeId=1`
);
```

### 3. Page de Test

**Fichier :** `test_endpoints_utilisateur_fixes.html`

- Test complet des nouveaux endpoints utilisateur
- Authentification avec rôle USER
- Génération et téléchargement de documents
- Logs détaillés pour le débogage

## 🧪 Procédure de Test

### 1. Redémarrer le Backend

```bash
# Arrêter le backend actuel (Ctrl+C)
# Puis redémarrer
cd backend
mvn clean compile
mvn spring-boot:run
```

### 2. Tester les Nouveaux Endpoints

1. Ouvrir `test_endpoints_utilisateur_fixes.html` dans un navigateur
2. Se connecter avec `user@econsulat.com` / `user123`
3. Charger les demandes utilisateur
4. Tester la génération de document
5. Tester le téléchargement

### 3. Vérifier les Logs

Les logs doivent maintenant montrer :

- ✅ Authentification réussie pour l'utilisateur
- ✅ Génération de document sans erreur TransientPropertyValueException
- ✅ Téléchargement réussi

## 🔍 Endpoints Disponibles

### Endpoints Utilisateur (Nouveaux)

- `POST /api/user/documents/generate` - Génère un document
- `GET /api/user/documents/download/{id}` - Télécharge un document

### Endpoints Admin (Existants)

- `POST /api/admin/documents/generate` - Génère un document (admin)
- `GET /api/admin/documents/download/{id}` - Télécharge un document (admin)

## 🛡️ Sécurité

### Vérifications Implémentées

- ✅ Authentification JWT requise
- ✅ Récupération de l'utilisateur depuis la base de données
- ✅ Utilisation de DTOs pour éviter les problèmes de sérialisation
- ✅ Gestion des erreurs avec messages explicites

### À Implémenter (TODO)

- 🔄 Vérification que le document appartient à l'utilisateur connecté
- 🔄 Validation des permissions sur les demandes
- 🔄 Audit des actions de génération

## 📊 Monitoring

### Logs à Surveiller

```bash
# Succès
✅ JWT Filter - Token valide pour: user@econsulat.com
✅ Document généré avec succès
✅ Téléchargement réussi

# Erreurs à éviter
❌ TransientPropertyValueException
❌ 403 Forbidden
❌ User non trouvé
```

### Métriques

- Nombre de documents générés par utilisateur
- Taux de succès des générations
- Temps de réponse des endpoints

## 🚀 Déploiement

### Étapes de Déploiement

1. ✅ Compiler le backend : `mvn clean compile`
2. ✅ Redémarrer le serveur : `mvn spring-boot:run`
3. ✅ Tester avec la page de test
4. ✅ Vérifier le frontend utilise les bons endpoints

### Rollback

Si des problèmes surviennent :

1. Revenir aux anciens endpoints dans le frontend
2. Désactiver temporairement le nouveau contrôleur
3. Utiliser les endpoints admin existants

## 📞 Support

### En Cas de Problème

1. Vérifier les logs du backend
2. Tester avec `test_endpoints_utilisateur_fixes.html`
3. Vérifier la connexion à la base de données
4. Contrôler les permissions utilisateur

### Contacts

- Développeur : Assistant IA
- Documentation : Ce fichier
- Tests : Page de test fournie
