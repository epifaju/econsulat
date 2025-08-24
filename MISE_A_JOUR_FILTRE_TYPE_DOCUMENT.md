# 🔧 Mise à Jour - Filtre "Type de Document" Dynamique

## 🎯 Objectif

Mettre à jour la liste déroulante "Type de document" dans le filtre de l'écran "Mes Demandes" pour utiliser les données dynamiques de la table `document_types` au lieu des valeurs codées en dur.

## 🚨 Problème Identifié

### Avant la modification

Le filtre "Type de document" utilisait des valeurs codées en dur qui ne correspondaient pas aux types réels disponibles en base de données :

```jsx
// ❌ AVANT - Valeurs codées en dur
documentType: {
  label: "Type de document",
  values: [
    { value: "PASSPORT", label: "Passeport" },
    { value: "ID_CARD", label: "Carte d'identité" },
    { value: "DRIVERS_LICENSE", label: "Permis de conduire" },
  ],
},
```

**Problèmes :**

- Valeurs non synchronisées avec la base de données
- Types de documents manquants ou incorrects
- Impossible d'ajouter de nouveaux types sans modifier le code

## 🛠️ Solution Implémentée

### 1. Ajout d'États pour les Types de Documents

```jsx
// ✅ NOUVEAU - États pour les types de documents
const [documentTypes, setDocumentTypes] = useState([]);
const [documentTypesLoading, setDocumentTypesLoading] = useState(false);
```

### 2. Chargement Dynamique depuis l'API

```jsx
// ✅ NOUVEAU - Chargement automatique des types de documents
useEffect(() => {
  const fetchDocumentTypes = async () => {
    try {
      setDocumentTypesLoading(true);
      const response = await fetch(
        "http://127.0.0.1:8080/api/demandes/document-types",
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      if (response.ok) {
        const data = await response.json();

        // Normaliser les données pour gérer différents formats
        const normalizedTypes = data.map((type) => {
          // Gérer le format { value, label } de l'API demandes
          if (type.value && type.label) {
            return { value: type.value, label: type.label };
          }
          // Gérer le format { id, libelle } de l'API admin
          else if (type.id && type.libelle) {
            return { value: type.libelle, label: type.libelle };
          }
          // Gérer le format { libelle } simple
          else if (type.libelle) {
            return { value: type.libelle, label: type.libelle };
          }
          // Fallback pour les autres formats
          else {
            return { value: String(type), label: String(type) };
          }
        });

        setDocumentTypes(normalizedTypes);
      } else {
        // En cas d'erreur, utiliser les types par défaut
        setDocumentTypes([...typesParDefaut]);
      }
    } catch (err) {
      console.error("Erreur lors du chargement des types de documents:", err);
      // En cas d'erreur, utiliser les types par défaut
      setDocumentTypes([...typesParDefaut]);
    } finally {
      setDocumentTypesLoading(false);
    }
  };

  if (token) {
    fetchDocumentTypes();
  }
}, [token]);
```

### 3. Normalisation des Données

Le système gère automatiquement différents formats de données retournés par l'API :

- **Format API demandes :** `{ value: "Passeport", label: "Passeport" }`
- **Format API admin :** `{ id: 1, libelle: "Passeport" }`
- **Format simple :** `{ libelle: "Passeport" }`

### 4. Options de Filtres Dynamiques

```jsx
// ✅ APRÈS - Options dynamiques basées sur la base de données
documentType: {
  label: "Type de document",
  values: documentTypesLoading
    ? [{ value: "", label: "Chargement des types..." }]
    : documentTypes.length > 0
    ? [
        { value: "", label: "Tous les types" },
        ...documentTypes.map(type => ({
          value: type.value,
          label: type.label
        }))
      ]
    : [
        // Fallback si aucun type n'est chargé
        { value: "", label: "Tous les types" },
        { value: "PASSEPORT", label: "Passeport" },
        { value: "ACTE_NAISSANCE", label: "Acte de naissance" },
        { value: "CERTIFICAT_MARIAGE", label: "Certificat de mariage" },
        { value: "CARTE_IDENTITE", label: "Carte d'identité" },
        { value: "AUTRE", label: "Autre" },
      ],
},
```

### 5. Logique de Filtrage Améliorée

```jsx
// ✅ NOUVEAU - Filtrage intelligent avec correspondance flexible
if (key === "documentType") {
  const filterValue = filters[key];
  const demandeType = demande.documentTypeDisplay || demande.documentType;

  // Correspondance exacte
  if (demandeType === filterValue) {
    return true;
  }

  // Correspondance insensible à la casse
  if (
    demandeType &&
    filterValue &&
    demandeType.toLowerCase() === filterValue.toLowerCase()
  ) {
    return true;
  }

  // Correspondance partielle (pour gérer les variations)
  if (
    demandeType &&
    filterValue &&
    (demandeType.toLowerCase().includes(filterValue.toLowerCase()) ||
      filterValue.toLowerCase().includes(demandeType.toLowerCase()))
  ) {
    return true;
  }

  return false;
}
```

## 📊 Types de Documents Disponibles

L'interface charge automatiquement tous les types de documents actifs depuis la table `document_types` :

| ID  | Libellé                  | Description                      |
| --- | ------------------------ | -------------------------------- |
| 1   | Passeport                | Document de voyage officiel      |
| 2   | Acte de naissance        | Certificat de naissance officiel |
| 3   | Certificat de mariage    | Certificat de mariage officiel   |
| 4   | Certificat de coutume    | Certificat de coutume officiel   |
| 5   | Carte d'identité         | Carte d'identité nationale       |
| 6   | Certificat d'hébergement | Certificat d'hébergement         |
| 7   | Certificat de célibat    | Certificat de célibat            |
| 8   | Certificat de résidence  | Certificat de résidence          |

## 🔄 Flux de Données

### 1. **Chargement Initial**

```
Composant UserDashboard → useEffect → fetchDocumentTypes() → API /api/demandes/document-types
```

### 2. **Normalisation des Données**

```
API Response → Normalisation → État documentTypes → filterOptions.documentType.values
```

### 3. **Application des Filtres**

```
Sélection utilisateur → filters.documentType → Filtrage intelligent → filteredDemandes
```

## 🎯 Avantages de la Solution

### ✅ **Dynamique**

- Types de documents chargés automatiquement depuis la base
- Pas de modification du code pour ajouter/supprimer des types

### ✅ **Robuste**

- Gestion des erreurs avec fallback vers les types par défaut
- Support de différents formats de données API

### ✅ **Flexible**

- Correspondance exacte, insensible à la casse et partielle
- Gestion des variations de libellés

### ✅ **Maintenable**

- Code centralisé et réutilisable
- Logique de filtrage claire et documentée

## 🧪 Tests et Validation

### Fichier de Test

Un fichier `test_filter_document_types.html` a été créé pour valider le fonctionnement :

- **Test API :** Vérification de la récupération des types de documents
- **Démonstration :** Simulation du comportement du filtre
- **Données de Test :** Validation du filtrage avec des données simulées

### Instructions de Test

1. Ouvrir `test_filter_document_types.html` dans un navigateur
2. Cliquer sur "Test API Types de Documents"
3. Vérifier que les types sont bien récupérés depuis la base
4. Tester le filtrage avec différentes combinaisons

## 🔧 Maintenance et Évolutions

### Ajout de Nouveaux Types

Pour ajouter un nouveau type de document, il suffit de l'insérer dans la table `document_types` :

```sql
INSERT INTO document_types (libelle, description, template_path, is_active)
VALUES ('Nouveau Type', 'Description du nouveau type', 'templates/nouveau_type.docx', true);
```

### Modification des Types Existants

Les modifications dans la base de données sont automatiquement reflétées dans l'interface après rechargement.

### Gestion des Erreurs

Le système gère automatiquement les erreurs de chargement et utilise des types par défaut en cas de problème.

## 📝 Conclusion

La mise à jour du filtre "Type de document" transforme une fonctionnalité statique en une fonctionnalité dynamique et maintenable. L'interface utilisateur reflète maintenant fidèlement les données de la base de données, offrant une expérience utilisateur cohérente et à jour.

**Impact :** ✅ Amélioration de la maintenabilité, ✅ Synchronisation avec la base de données, ✅ Expérience utilisateur améliorée
