# 🔧 Correction - Mapping des Types de Documents Incorrect

## 🚨 **Problème Identifié**

**Question de l'utilisateur :** _"Quand l'utilisateur crée une demande de document de type 'Acte de naissance', dans le tableau 'Gestion des Demandes' le type de document n'est pas 'Acte de naissance' mais 'Carte d'identité'."_

### 🔍 **Cause Racine**

Le problème venait d'un **mapping incorrect entre les enums Java et les IDs de la base de données** dans la classe `DemandeAdminResponse.java`.

## 📊 **Mapping Incorrect (AVANT)**

```java
// ❌ MAPPING INCORRIGÉ
private Long getDocumentTypeIdFromEnum(Demande.DocumentType documentType) {
    switch (documentType) {
        case PASSEPORT: return 1L;           // ✅ Correct
        case ACTE_NAISSANCE: return 2L;      // ✅ Correct
        case CERTIFICAT_MARIAGE: return 3L;  // ✅ Correct
        case CARTE_IDENTITE: return 4L;      // ❌ Incorrect (devrait être 5L)
        case AUTRE: return 5L;               // ❌ Incorrect (devrait être 6L)
        default: return 1L;
    }
}
```

### **Problème :**

- `ACTE_NAISSANCE` était mappé à l'ID 2 ✅
- `CARTE_IDENTITE` était mappé à l'ID 4 ❌ (mais en base c'est l'ID 5)
- `AUTRE` était mappé à l'ID 5 ❌ (mais en base c'est l'ID 6)

## 🛠️ **Solution Implémentée**

### **1. Correction du Mapping Backend**

**Fichier :** `backend/src/main/java/com/econsulat/dto/DemandeAdminResponse.java`

```java
// ✅ MAPPING CORRIGÉ
private Long getDocumentTypeIdFromEnum(Demande.DocumentType documentType) {
    switch (documentType) {
        case PASSEPORT:
            return 1L; // ID du type "Passeport" en base
        case ACTE_NAISSANCE:
            return 2L; // ID du type "Acte de naissance" en base
        case CERTIFICAT_MARIAGE:
            return 3L; // ID du type "Certificat de mariage" en base
        case CARTE_IDENTITE:
            return 5L; // ID du type "Carte d'identité" en base (corrigé de 4L à 5L)
        case AUTRE:
            return 6L; // ID du type "Autre" en base (corrigé de 5L à 6L)
        default:
            return 1L; // Fallback vers Passeport
    }
}
```

### **2. Script de Vérification SQL**

**Fichier :** `fix_document_type_mapping.sql`

Ce script permet de :

- Vérifier l'état actuel de la table `document_types`
- Identifier les incohérences de mapping
- Corriger automatiquement les types manquants
- Valider le mapping final

## 📋 **Mapping Final Correct**

| Enum Demande.DocumentType | ID en base | Libellé en base       | Statut |
| ------------------------- | ---------- | --------------------- | ------ |
| PASSEPORT                 | 1          | Passeport             | ✅     |
| ACTE_NAISSANCE            | 2          | Acte de naissance     | ✅     |
| CERTIFICAT_MARIAGE        | 3          | Certificat de mariage | ✅     |
| CARTE_IDENTITE            | 5          | Carte d'identité      | ✅     |
| AUTRE                     | 6          | Autre                 | ✅     |

## 🔄 **Flux de Données Corrigé**

### **Avant (Incorrect) :**

```
Demande ACTE_NAISSANCE → Enum ACTE_NAISSANCE → Mapping → ID 2 → Base → "Acte de naissance" ✅
Demande CARTE_IDENTITE → Enum CARTE_IDENTITE → Mapping → ID 4 → Base → "Certificat de coutume" ❌
```

### **Après (Correct) :**

```
Demande ACTE_NAISSANCE → Enum ACTE_NAISSANCE → Mapping → ID 2 → Base → "Acte de naissance" ✅
Demande CARTE_IDENTITE → Enum CARTE_IDENTITE → Mapping → ID 5 → Base → "Carte d'identité" ✅
```

## 🧪 **Tests de Validation**

### **1. Redémarrer le Backend**

```bash
# Arrêter le backend
./stop_java_processes.bat

# Redémarrer avec les corrections
./start_backend.bat
```

### **2. Vérifier l'Interface Admin**

- Aller dans "Gestion des Demandes"
- Créer une demande de type "Acte de naissance"
- Vérifier que la colonne "Type de document" affiche "Acte de naissance"
- Créer une demande de type "Carte d'identité"
- Vérifier que la colonne "Type de document" affiche "Carte d'identité"

### **3. Vérifier la Base de Données**

```sql
-- Exécuter le script de vérification
\i fix_document_type_mapping.sql
```

## 🎯 **Résultat Attendu**

Après cette correction :

- ✅ Les demandes de type "Acte de naissance" afficheront correctement "Acte de naissance"
- ✅ Les demandes de type "Carte d'identité" afficheront correctement "Carte d'identité"
- ✅ Tous les autres types de documents seront correctement mappés
- ✅ La génération de documents utilisera les bons templates

## 📝 **Fichiers Modifiés**

1. **`backend/src/main/java/com/econsulat/dto/DemandeAdminResponse.java`**

   - Correction du mapping des IDs des types de documents

2. **`fix_document_type_mapping.sql`** (nouveau)

   - Script de vérification et correction de la base de données

3. **`CORRECTION_MAPPING_TYPE_DOCUMENT.md`** (nouveau)
   - Documentation complète de la correction

## 🚀 **Prochaines Étapes**

1. **Redémarrer le backend** pour appliquer les corrections
2. **Exécuter le script SQL** pour vérifier la base de données
3. **Tester l'interface** avec différents types de documents
4. **Valider** que tous les types s'affichent correctement

---

**Note :** Cette correction résout le problème d'affichage incorrect des types de documents dans l'interface "Gestion des Demandes" de l'administrateur.
