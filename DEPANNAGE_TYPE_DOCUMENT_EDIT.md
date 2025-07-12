# 🔧 Dépannage - Type de document non remonté dans l'édition

## 🚨 Problème

Dans le formulaire de modification d'une demande par l'admin, le type de document ne s'affiche pas correctement dans le select.

## 🔍 Diagnostic

### 1. Vérification du format des données

Le problème vient de l'incompatibilité entre :

- **Backend** : `DemandeAdminResponse.documentType` = chaîne (ex: "PASSEPORT")
- **API** : `/api/demandes/document-types` retourne des objets avec `value` et `label`
- **Frontend** : Le select attend une valeur qui correspond à `type.value`

### 2. Endpoints concernés

#### Endpoint des types de documents

```http
GET /api/demandes/document-types
```

**Avant la correction :**

```json
["PASSEPORT", "ACTE_NAISSANCE", "CERTIFICAT_MARIAGE", "CARTE_IDENTITE", "AUTRE"]
```

**Après la correction :**

```json
[
  {
    "value": "PASSEPORT",
    "label": "Passeport"
  },
  {
    "value": "ACTE_NAISSANCE",
    "label": "Acte de naissance"
  },
  {
    "value": "CERTIFICAT_MARIAGE",
    "label": "Certificat de mariage"
  },
  {
    "value": "CARTE_IDENTITE",
    "label": "Carte d'identité"
  },
  {
    "value": "AUTRE",
    "label": "Autre"
  }
]
```

#### Endpoint de récupération d'une demande

```http
GET /api/admin/demandes/{id}
```

**Réponse :**

```json
{
  "id": 1,
  "firstName": "Jean",
  "lastName": "Dupont",
  "documentType": "PASSEPORT", // ← Cette valeur doit correspondre à type.value
  "documentTypeDisplay": "Passeport"
  // ... autres champs
}
```

## 🛠️ Solutions appliquées

### 1. Modification de l'endpoint backend

**Fichier :** `backend/src/main/java/com/econsulat/controller/DemandeController.java`

**Avant :**

```java
@GetMapping("/document-types")
public ResponseEntity<Demande.DocumentType[]> getDocumentTypes() {
    Demande.DocumentType[] types = Demande.DocumentType.values();
    return ResponseEntity.ok(types);
}
```

**Après :**

```java
@GetMapping("/document-types")
public ResponseEntity<List<Map<String, String>>> getDocumentTypes() {
    List<Map<String, String>> types = new ArrayList<>();
    for (Demande.DocumentType type : Demande.DocumentType.values()) {
        Map<String, String> typeMap = new HashMap<>();
        typeMap.put("value", type.name());
        typeMap.put("label", type.getDisplayName());
        types.add(typeMap);
    }
    return ResponseEntity.ok(types);
}
```

### 2. Ajout de logs de debug

**Fichier :** `frontend/src/components/AdminDemandeEditModal.jsx`

```javascript
// Debug de la demande reçue
console.log("🔍 Debug - Demande reçue:", demande);
console.log("🔍 Debug - DocumentType de la demande:", demande.documentType);

// Debug des types chargés
console.log("🔍 Debug - Types de documents chargés:", typesData);
```

## 🧪 Tests

### 1. Fichier de test

- **Fichier :** `test_document_types.html`
- **Fonctionnalités :**
  - Test de l'endpoint `/api/demandes/document-types`
  - Test de l'endpoint `/api/admin/document-types`
  - Affichage du format des données

### 2. Comment tester

1. Ouvrez `test_document_types.html` dans votre navigateur
2. Cliquez sur "📋 /api/demandes/document-types"
3. Vérifiez que le format est correct :
   ```json
   [
     {
       "value": "PASSEPORT",
       "label": "Passeport"
     }
   ]
   ```

### 3. Test dans l'interface admin

1. Connectez-vous en tant qu'admin
2. Ouvrez une demande pour modification
3. Vérifiez dans la console du navigateur (F12) :
   - Les logs de debug de la demande
   - Les logs de debug des types de documents
   - La correspondance entre `demande.documentType` et `type.value`

## 🔍 Vérifications

### 1. Console du navigateur

Ouvrez les outils de développement (F12) et vérifiez :

```javascript
// Ces logs doivent apparaître lors de l'ouverture du modal
🔍 Debug - Demande reçue: {id: 1, documentType: "PASSEPORT", ...}
🔍 Debug - DocumentType de la demande: PASSEPORT
🔍 Debug - Types de documents chargés: [{value: "PASSEPORT", label: "Passeport"}, ...]
```

### 2. Correspondance des valeurs

- `demande.documentType` doit être égal à `type.value`
- Exemple : `"PASSEPORT"` doit correspondre à `"PASSEPORT"`

### 3. Structure du select

Le select doit avoir cette structure :

```html
<select value="PASSEPORT">
  <option value="">Sélectionner</option>
  <option value="PASSEPORT">Passeport</option>
  <option value="ACTE_NAISSANCE">Acte de naissance</option>
  <!-- ... -->
</select>
```

## 🚨 Problèmes courants

### 1. Backend non redémarré

**Symptôme :** L'endpoint retourne encore l'ancien format
**Solution :** Redémarrez le backend avec `mvn spring-boot:run`

### 2. Cache du navigateur

**Symptôme :** Les anciennes données sont encore affichées
**Solution :**

- Videz le cache (Ctrl+F5)
- Ou ouvrez en navigation privée

### 3. Valeurs non correspondantes

**Symptôme :** Le select ne sélectionne pas la bonne valeur
**Vérification :** Comparez `demande.documentType` avec `type.value` dans la console

## 📋 Checklist de résolution

- [ ] Backend redémarré avec les modifications
- [ ] Endpoint `/api/demandes/document-types` retourne le bon format
- [ ] Console du navigateur affiche les logs de debug
- [ ] `demande.documentType` correspond à `type.value`
- [ ] Le select affiche la bonne valeur sélectionnée
- [ ] Test avec différents types de documents

## 🔄 Workflow de correction

1. **Identifier le problème** : Type de document non sélectionné
2. **Vérifier le format** : Endpoint retourne-t-il `{value, label}` ?
3. **Corriger le backend** : Modifier l'endpoint si nécessaire
4. **Ajouter du debug** : Logs pour tracer les valeurs
5. **Tester** : Vérifier la correspondance des valeurs
6. **Valider** : Le select affiche la bonne valeur

## 📞 Support

Si le problème persiste :

1. Vérifiez les logs du backend
2. Vérifiez la console du navigateur
3. Utilisez le fichier de test pour isoler le problème
4. Vérifiez que les valeurs correspondent exactement
