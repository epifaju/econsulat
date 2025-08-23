# 🔧 Solution - Sélection du Type de Document dans l'Interface Admin

## 🚨 Problème Identifié

Le paramètre `documentTypeId` était toujours égal à **1** pour chaque document Word téléchargé via l'endpoint :

```
http://localhost:8080/api/admin/documents/generate?demandeId=4&documentTypeId=1
```

### Cause Racine

Le problème était dans le composant `AdminDemandesList.jsx` où le `documentTypeId` était **codé en dur** :

```jsx
// ❌ AVANT - Code en dur
onClick={() => handleGenerateDocument(demande.id, 1)} // ID du type de document
onClick={() => handleGeneratePdfDocument(demande.id, 1)} // ID du type de document
```

## 🛠️ Solution Implémentée

### 1. Ajout d'un État pour les Types de Documents

```jsx
const [documentTypes, setDocumentTypes] = useState([]);
const [selectedDocumentType, setSelectedDocumentType] = useState({});
```

### 2. Mapping Automatique avec le Type de la Demande

Le système **pré-remplit automatiquement** le sélecteur avec le type de document correspondant à celui de la demande :

```jsx
// Mapping intelligent entre le type de la demande et le type en base
const matchingType = data.find(
  (type) =>
    type.libelle.toLowerCase() === demande.documentTypeDisplay?.toLowerCase() ||
    type.libelle
      .toLowerCase()
      .includes(demande.documentTypeDisplay?.toLowerCase()) ||
    demande.documentTypeDisplay
      ?.toLowerCase()
      .includes(type.libelle.toLowerCase())
);

if (matchingType) {
  initialSelection[demande.id] = matchingType.id;
} else {
  // Fallback : utiliser le premier type disponible
  initialSelection[demande.id] = data[0]?.id || null;
}
```

### 3. Chargement des Types de Documents avec Mapping

```jsx
const fetchDocumentTypes = async () => {
  try {
    const response = await fetch(
      "http://localhost:8080/api/admin/document-types",
      {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      }
    );

    if (response.ok) {
      const data = await response.json();
      setDocumentTypes(data);

      // Initialiser le type sélectionné pour chaque demande
      // en mappant le type de document de la demande avec le type correspondant en base
      const initialSelection = {};
      demandes.forEach((demande) => {
        // Chercher le type de document correspondant en base
        const matchingType = data.find(
          (type) =>
            type.libelle.toLowerCase() ===
              demande.documentTypeDisplay?.toLowerCase() ||
            type.libelle
              .toLowerCase()
              .includes(demande.documentTypeDisplay?.toLowerCase()) ||
            demande.documentTypeDisplay
              ?.toLowerCase()
              .includes(type.libelle.toLowerCase())
        );

        if (matchingType) {
          initialSelection[demande.id] = matchingType.id;
        } else {
          // Fallback : utiliser le premier type disponible
          initialSelection[demande.id] = data[0]?.id || null;
        }
      });

      setSelectedDocumentType(initialSelection);
    }
  } catch (err) {
    console.error("Erreur lors du chargement des types de documents:", err);
  }
};
```

### 3. Gestion du Changement de Type

```jsx
const handleDocumentTypeChange = (demandeId, documentTypeId) => {
  setSelectedDocumentType((prev) => ({
    ...prev,
    [demandeId]: parseInt(documentTypeId),
  }));
};
```

### 4. Modification des Fonctions de Génération

```jsx
// ✅ APRÈS - Type dynamique
const handleGenerateDocument = async (demandeId) => {
  const documentTypeId = selectedDocumentType[demandeId];
  if (!documentTypeId) {
    onNotification(
      "error",
      "Erreur",
      "Veuillez sélectionner un type de document"
    );
    return;
  }
  // ... reste de la logique
};
```

### 5. Interface Utilisateur Améliorée

#### Nouvelle Colonne "Type à générer"

```jsx
<th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
  Type à générer
</th>
```

#### Sélecteur de Type par Demande

```jsx
<td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
  <select
    value={selectedDocumentType[demande.id] || ""}
    onChange={(e) => handleDocumentTypeChange(demande.id, e.target.value)}
    className="text-xs border border-gray-300 rounded px-2 py-1 min-w-[120px]"
    title="Sélectionner le type de document à générer"
  >
    <option value="">Sélectionner...</option>
    {documentTypes.map((type) => (
      <option key={type.id} value={type.id}>
        {type.libelle}
      </option>
    ))}
  </select>
</td>
```

#### Boutons de Génération Désactivés

```jsx
<button
  onClick={() => handleGenerateDocument(demande.id)}
  disabled={!selectedDocumentType[demande.id]}
  title={
    !selectedDocumentType[demande.id]
      ? "Sélectionnez d'abord un type de document"
      : "Générer document Word"
  }
>
  <DocumentArrowDownIcon className="h-4 w-4" />
</button>
```

## 📋 Types de Documents Disponibles

L'interface charge automatiquement tous les types de documents actifs depuis la base de données :

- **Passeport** (ID: 1)
- **Acte de naissance** (ID: 2)
- **Certificat de mariage** (ID: 3)
- **Certificat de coutume** (ID: 4)
- **Carte d'identité** (ID: 5)
- **Certificat d'hébergement** (ID: 6)
- **Certificat de célibat** (ID: 7)
- **Certificat de résidence** (ID: 8)

## 🔄 Flux de Travail Amélioré

### Avant (Problématique)

1. ❌ Admin clique sur "Générer document"
2. ❌ `documentTypeId` est toujours 1 (codé en dur)
3. ❌ Même type de document pour toutes les demandes

### Après (Solution)

1. ✅ **Mapping automatique** : Le type de document est pré-rempli selon la demande
2. ✅ **Vérification** : L'admin peut modifier le type si nécessaire
3. ✅ **Validation** : Impossible de générer sans type sélectionné
4. ✅ **Génération** : Le bon type de document est utilisé
5. ✅ **Feedback visuel** : Indicateur "✓ Pré-sélectionné" et boutons désactivés

## 🧪 Test de la Solution

### Test Principal

Un fichier de test complet a été créé : `test_admin_document_type_selection.html`

### Fonctionnalités de Test

- **Authentification admin**
- **Chargement des types de documents**
- **Sélection et génération**
- **Test complet de l'interface**

### Test de Mapping

Un fichier de test spécialisé a été créé : `test_mapping_type_demande.html`

#### Fonctionnalités de Test de Mapping

- **Vérification du mapping automatique** entre types de demandes et types en base
- **Analyse des correspondances** (exactes, partielles, par mots-clés)
- **Statistiques de réussite** du mapping
- **Détection des problèmes** de correspondance

## 📁 Fichiers Modifiés

- `frontend/src/components/AdminDemandesList.jsx` - Composant principal modifié
- `test_admin_document_type_selection.html` - Fichier de test principal créé
- `test_mapping_type_demande.html` - Fichier de test de mapping créé

## 🎯 Avantages de la Solution

1. **Mapping automatique** : Le type de document est pré-rempli automatiquement selon la demande
2. **Flexibilité** : L'admin peut modifier le type si nécessaire
3. **Validation** : Impossible de générer sans sélectionner un type
4. **Interface claire** : Dropdown dédié avec indicateur visuel "✓ Pré-sélectionné"
5. **Feedback utilisateur** : Boutons désactivés et tooltips informatifs
6. **Maintenance** : Plus de code en dur, tout est dynamique
7. **Intelligence** : Correspondance intelligente entre types de demandes et types en base

## 🚀 Utilisation

1. **Accéder à l'interface admin** (`/admin`)
2. **Aller à l'onglet "Demandes"**
3. **Pour chaque demande** : sélectionner le type de document dans la colonne "Type à générer"
4. **Cliquer sur le bouton de génération** (Word ou PDF)
5. **Le document sera généré** avec le bon type sélectionné

---

**Résultat** : Le paramètre `documentTypeId` n'est plus fixé à 1, mais correspond au type de document sélectionné par l'administrateur pour chaque demande spécifique.
