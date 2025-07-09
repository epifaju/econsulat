# 🔧 Dépannage - Erreur "Cannot read the array length because "<local3>" is null"

## 🚨 Problème identifié

L'erreur "Cannot read the array length because "<local3>" is null" indique que le fichier Word template est corrompu ou dans un format non supporté par Apache POI.

## 🔍 Causes possibles

### 1. Fichier Word corrompu

- Le fichier `FORMULARIO DE PEDIDO DE PASSAPORTE.docx` est endommagé
- Le fichier a été corrompu lors du transfert ou de la copie

### 2. Format de fichier non supporté

- Le fichier est au format `.doc` (ancien format) au lieu de `.docx`
- Le fichier n'est pas un document Word valide

### 3. Problème de compatibilité Apache POI

- Version d'Apache POI incompatible avec le format du fichier
- Structure interne du document Word non standard

## ✅ Solutions

### Solution 1: Vérifier et corriger le format du fichier

#### Étape 1: Vérifier le format actuel

```bash
# Vérifier l'extension du fichier
ls -la "backend/src/main/resources/FORMULARIO DE PEDIDO DE PASSAPORTE.docx"

# Vérifier le type MIME du fichier
file "backend/src/main/resources/FORMULARIO DE PEDIDO DE PASSAPORTE.docx"
```

#### Étape 2: Ouvrir et resauvegarder dans Word

1. Ouvrir le fichier dans Microsoft Word
2. Aller dans "Fichier" → "Enregistrer sous"
3. Choisir le format "Document Word (.docx)"
4. Sauvegarder sous le même nom
5. Remplacer le fichier dans `backend/src/main/resources/`

### Solution 2: Créer un nouveau template

#### Étape 1: Créer un nouveau document Word

1. Ouvrir Microsoft Word
2. Créer un nouveau document
3. Ajouter le contenu suivant :

```
FORMULARIO DE PEDIDO DE PASSAPORTE

Nome: {{Prénom}}
Apelido: {{Nom de famille}}
Data de nascimento: {{Date de naissance}}
Local de nascimento: {{Local de nascimento}}

Date de demande: _________________
Signature: _______________________
```

#### Étape 2: Sauvegarder au bon format

1. "Fichier" → "Enregistrer sous"
2. Format: "Document Word (.docx)"
3. Nom: "FORMULARIO DE PEDIDO DE PASSAPORTE.docx"
4. Emplacement: `backend/src/main/resources/`

### Solution 3: Utiliser le template de secours

#### Étape 1: Générer le template de secours

1. Ouvrir `fix_template_word.html` dans un navigateur
2. Cliquer sur "Créer un template de secours"
3. Télécharger le fichier HTML

#### Étape 2: Convertir en Word

1. Ouvrir le fichier HTML téléchargé
2. Copier tout le contenu
3. Créer un nouveau document Word
4. Coller le contenu
5. Sauvegarder au format .docx
6. Renommer en "FORMULARIO DE PEDIDO DE PASSAPORTE.docx"
7. Copier dans `backend/src/main/resources/`

## 🧪 Diagnostic et test

### 1. Utiliser le fichier de diagnostic

Ouvrir `fix_template_word.html` et suivre les étapes :

1. "Tester la génération de template" - Identifie le problème
2. "Créer un template de secours" - Génère un template de remplacement
3. "Tester après correction" - Vérifie que la correction fonctionne

### 2. Test manuel via curl

```bash
# Se connecter
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# Tester la génération (remplacer YOUR_TOKEN)
curl -X POST http://localhost:8080/api/passport/generate/1 \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -v
```

### 3. Vérifier les logs

```bash
# Suivre les logs en temps réel
tail -f backend/logs/application.log

# Chercher les erreurs spécifiques
grep -i "array length\|null\|corrupt" backend/logs/application.log
```

## ✅ Corrections apportées au code

### 1. Amélioration de la gestion d'erreurs

**Fichier :** `backend/src/main/java/com/econsulat/service/PassportDocumentService.java`

**Détection spécifique de l'erreur :**

```java
} catch (Exception e) {
    String errorMessage = "Erreur lors de la génération du document";
    if (e.getMessage() != null && e.getMessage().contains("Cannot read the array length")) {
        errorMessage = "Le template Word est corrompu ou dans un format non supporté. Veuillez vérifier le fichier FORMULARIO DE PEDIDO DE PASSAPORTE.docx et vous assurer qu'il s'agit d'un fichier .docx valide.";
    }
    throw new RuntimeException(errorMessage + ": " + e.getMessage(), e);
}
```

### 2. Validation du template

**Validation du format de fichier :**

```java
private boolean isValidWordDocument(byte[] header) {
    // Vérifier les signatures de fichiers Word (.docx)
    // Les fichiers .docx commencent par PK (0x50 0x4B)
    return header.length >= 2 && header[0] == 0x50 && header[1] == 0x4B;
}
```

### 3. Lecture en mémoire

**Lecture complète du template en mémoire :**

```java
// Lire tout le contenu du template en mémoire pour éviter les problèmes de stream
byte[] templateBytes = inputStream.readAllBytes();

if (templateBytes.length == 0) {
    throw new RuntimeException("Le template Word est vide");
}

if (!isValidWordDocument(templateBytes)) {
    throw new RuntimeException("Le fichier ne semble pas être un document Word valide (.docx)");
}
```

## 🚀 Procédure de résolution complète

### Étape 1: Diagnostic

1. Ouvrir `fix_template_word.html`
2. Cliquer sur "Tester la génération de template"
3. Confirmer que l'erreur "Cannot read the array length" apparaît

### Étape 2: Correction

1. **Option A** : Ouvrir le fichier dans Word et resauvegarder au format .docx
2. **Option B** : Créer un nouveau template avec les placeholders
3. **Option C** : Utiliser le template de secours généré

### Étape 3: Test

1. Redémarrer l'application
2. Tester à nouveau la génération
3. Vérifier que le document est créé correctement

## 📋 Checklist de vérification

- [ ] Le fichier est au format .docx (pas .doc)
- [ ] Le fichier peut être ouvert dans Microsoft Word
- [ ] Le fichier contient les placeholders {{Prénom}}, {{Nom de famille}}, etc.
- [ ] Le fichier est copié dans `backend/src/main/resources/`
- [ ] L'application a été redémarrée après remplacement du fichier
- [ ] Le test de génération fonctionne

## 🔧 En cas de problème persistant

### 1. Vérifier les dépendances Apache POI

```xml
<!-- Vérifier les versions dans pom.xml -->
<dependency>
    <groupId>org.apache.poi</groupId>
    <artifactId>poi</artifactId>
    <version>5.2.3</version>
</dependency>
```

### 2. Tester avec un template minimal

Créer un document Word très simple avec seulement du texte et les placeholders.

### 3. Vérifier les permissions

```bash
# Vérifier les permissions du fichier
ls -la "backend/src/main/resources/FORMULARIO DE PEDIDO DE PASSAPORTE.docx"

# Vérifier les permissions du dossier
ls -la backend/src/main/resources/
```

## 📞 Support

Si le problème persiste après ces étapes :

1. Utiliser `fix_template_word.html` pour le diagnostic complet
2. Vérifier que le nouveau template est bien au format .docx
3. Contacter l'équipe de développement avec les détails de l'erreur
