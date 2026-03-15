# Implémentation : Charte PDF + marges + données complètes

Ce document détaille l’implémentation de l’étape **« charte + marges + données complètes »** pour les PDF générés par `PdfDocumentService`, sans modifier le comportement métier (génération, watermark, stockage).

---

## 1. Objectifs

- **Charte** : marges explicites, couleurs et tailles de police centralisées.
- **Marges** : contenu ne collant plus aux bords de la page A4.
- **Données complètes** : civilité, code postal, boîte, filiation complète (dates/lieux père et mère).

---

## 2. Fichiers à créer ou modifier

| Fichier | Action |
|--------|--------|
| `com.econsulat.service.PdfStyleConfig` | **Créer** – constantes de mise en page et couleurs |
| `com.econsulat.service.PdfDocumentService` | **Modifier** – utiliser la charte, appliquer marges, afficher toutes les données |

Aucun changement aux contrôleurs, repositories, ni au `WatermarkService`.

---

## 3. Classe `PdfStyleConfig`

### 3.1 Rôle

- Centraliser marges, couleurs et tailles de police pour tous les PDF « document officiel ».
- Permettre d’ajuster le rendu plus tard en un seul endroit.

### 3.2 Emplacement

- Package : `com.econsulat.service`
- Nom : `PdfStyleConfig`

### 3.3 Contenu proposé

```java
package com.econsulat.service;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;

/**
 * Charte graphique pour les PDF officiels eConsulat (marges, couleurs, tailles).
 */
public final class PdfStyleConfig {

    private PdfStyleConfig() {}

    // ----- Marges (points, 1 pt ≈ 0,35 mm) -----
    /** Marge haute et basse */
    public static final float MARGIN_TOP_BOTTOM = 50f;
    /** Marge gauche et droite */
    public static final float MARGIN_LEFT_RIGHT = 45f;

    // ----- Couleurs (texte) -----
    /** Titre principal (ex. "DOCUMENT OFFICIEL - eCONSULAT") */
    public static final DeviceRgb COLOR_TITLE = new DeviceRgb(0x0D, 0x47, 0xA1);  // bleu officiel
    /** Sous-titres de section (ex. "INFORMATIONS DU DEMANDEUR") */
    public static final DeviceRgb COLOR_SECTION_HEADER = new DeviceRgb(0x15, 0x65, 0xC0);
    /** Corps de texte : noir (déjà défaut, explicite pour cohérence) */
    public static final DeviceRgb COLOR_BODY = ColorConstants.BLACK;
    /** Métadonnées (date de génération, pied de page) */
    public static final DeviceRgb COLOR_META = new DeviceRgb(0x55, 0x55, 0x55);

    // ----- Tailles de police (points) -----
    public static final float FONT_SIZE_TITLE = 18f;
    public static final float FONT_SIZE_DOC_TYPE = 14f;
    public static final float FONT_SIZE_SECTION = 12f;
    public static final float FONT_SIZE_BODY = 11f;
    public static final float FONT_SIZE_META = 10f;
    public static final float FONT_SIZE_FOOTER = 8f;

    // ----- Espacements (points) -----
    /** Espace après le titre principal */
    public static final float SPACING_AFTER_TITLE = 8f;
    /** Espace après le type de document */
    public static final float SPACING_AFTER_DOC_TYPE = 4f;
    /** Espace avant un titre de section */
    public static final float SPACING_BEFORE_SECTION = 14f;
    /** Espace après un titre de section */
    public static final float SPACING_AFTER_SECTION = 6f;
    /** Espace entre deux lignes de contenu dans une section */
    public static final float SPACING_BETWEEN_LINES = 2f;
}
```

### 3.4 Remarques

- `DeviceRgb` est dans `com.itextpdf.kernel.colors.DeviceRgb` (module kernel, déjà présent).
- Si vous préférez ne pas introduire de couleur tout de suite, remplacer `COLOR_TITLE` / `COLOR_SECTION_HEADER` par `ColorConstants.BLACK` et garder les tailles et espacements.

---

## 4. Modifications dans `PdfDocumentService`

### 4.1 Imports à ajouter

```java
import com.itextpdf.kernel.colors.DeviceRgb;
import com.econsulat.service.PdfStyleConfig;
```

(Adapter selon votre convention : `ColorConstants` si vous restez en noir uniquement.)

### 4.2 Méthode `createPdfWithData` – marges et document

**Avant** (extrait actuel) :

```java
try (PdfWriter writer = new PdfWriter(pdfPath);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf, PageSize.A4)) {

    // Titre principal
    Paragraph title = new Paragraph("DOCUMENT OFFICIEL - eCONSULAT");
    title.setFontSize(18);
    title.setBold();
    document.add(title);
```

**Après** :

```java
try (PdfWriter writer = new PdfWriter(pdfPath);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf, PageSize.A4)) {

    document.setMargins(
            PdfStyleConfig.MARGIN_TOP_BOTTOM,
            PdfStyleConfig.MARGIN_LEFT_RIGHT,
            PdfStyleConfig.MARGIN_TOP_BOTTOM,
            PdfStyleConfig.MARGIN_LEFT_RIGHT);

    // Titre principal
    Paragraph title = new Paragraph("DOCUMENT OFFICIEL - eCONSULAT");
    title.setFontSize(PdfStyleConfig.FONT_SIZE_TITLE);
    title.setBold();
    title.setFontColor(PdfStyleConfig.COLOR_TITLE);
    title.setMarginBottom(PdfStyleConfig.SPACING_AFTER_TITLE);
    document.add(title);
```

- On applique les marges une fois au début du `Document`.
- Titre : taille, couleur et marge inférieure via `PdfStyleConfig`.

### 4.3 Type de document

Remplacer la création du paragraphe « Type: … » par l’utilisation de la charte et d’un espacement après :

```java
Paragraph docType = new Paragraph(
        "Type: " + (documentType != null ? documentType.getLibelle() : "Non spécifié"));
docType.setFontSize(PdfStyleConfig.FONT_SIZE_DOC_TYPE);
docType.setFontColor(PdfStyleConfig.COLOR_BODY);
docType.setMarginBottom(PdfStyleConfig.SPACING_AFTER_DOC_TYPE);
document.add(docType);
```

(Si vous gardez tout en noir, `COLOR_BODY` reste noir.)

### 4.4 Section « INFORMATIONS DU DEMANDEUR »

- Titre de section : `FONT_SIZE_SECTION`, `COLOR_SECTION_HEADER`, `SPACING_BEFORE_SECTION`, `SPACING_AFTER_SECTION`.
- Chaque ligne de contenu : `FONT_SIZE_BODY`, `COLOR_BODY`, `SPACING_BETWEEN_LINES` (margin bottom).
- Données à afficher dans l’ordre suivant (avec gestion des null) :
  - **Civilité** : `demande.getCivilite() != null ? demande.getCivilite().getLibelle() : "Non renseigné"`.
  - **Nom** : comme aujourd’hui.
  - **Prénom** : comme aujourd’hui.
  - **Date de naissance** : comme aujourd’hui (format existant).
  - **Lieu de naissance** : lieu + pays, comme aujourd’hui.

Exemple pour le titre de section et les deux premières lignes :

```java
Paragraph demandeur = new Paragraph("INFORMATIONS DU DEMANDEUR");
demandeur.setFontSize(PdfStyleConfig.FONT_SIZE_SECTION);
demandeur.setBold();
demandeur.setFontColor(PdfStyleConfig.COLOR_SECTION_HEADER);
demandeur.setMarginTop(PdfStyleConfig.SPACING_BEFORE_SECTION);
demandeur.setMarginBottom(PdfStyleConfig.SPACING_AFTER_SECTION);
document.add(demandeur);

String civiliteLibelle = demande.getCivilite() != null && demande.getCivilite().getLibelle() != null
        ? demande.getCivilite().getLibelle()
        : "Non renseigné";
document.add(newBodyLine("Civilité: " + civiliteLibelle));
document.add(newBodyLine("Nom: " + (demande.getLastName() != null ? demande.getLastName() : "Non renseigné")));
document.add(newBodyLine("Prénom: " + (demande.getFirstName() != null ? demande.getFirstName() : "Non renseigné")));
// ... date de naissance, lieu de naissance idem avec newBodyLine(...)
```

Il faut introduire une méthode privée pour éviter la duplication :

```java
private Paragraph newBodyLine(String text) {
    Paragraph p = new Paragraph(text);
    p.setFontSize(PdfStyleConfig.FONT_SIZE_BODY);
    p.setFontColor(PdfStyleConfig.COLOR_BODY);
    p.setMarginBottom(PdfStyleConfig.SPACING_BETWEEN_LINES);
    return p;
}
```

Même principe pour toutes les lignes « libellé: valeur » du demandeur.

### 4.5 Section « ADRESSE »

- Titre de section : même style que « INFORMATIONS DU DEMANDEUR » (section header).
- Lignes à afficher (avec null-safety) :
  - Ligne 1 : numéro + nom de rue (+ boîte si présent).  
    Ex. `streetNumber + " " + streetName + (boxNumber != null && !boxNumber.isBlank() ? " bte " + boxNumber : "")`.
  - Ligne 2 : **code postal + ville** (ex. `postalCode + " " + city`).  
    Utiliser `demande.getAdresse().getPostalCode()` et `getCity()`.
  - Ligne 3 : pays (libellé du pays).

Si `adresse == null`, garder un seul paragraphe « ADRESSE: Non renseignée » comme aujourd’hui, avec le style body.

Exemple (après le titre de section « ADRESSE ») :

```java
String streetNumber = adresse.getStreetNumber() != null ? adresse.getStreetNumber() : "";
String streetName = adresse.getStreetName() != null ? adresse.getStreetName() : "";
String boxNumber = adresse.getBoxNumber();
String streetLine = (streetNumber + " " + streetName).trim();
if (boxNumber != null && !boxNumber.isBlank()) {
    streetLine += " bte " + boxNumber;
}
document.add(newBodyLine(streetLine));

String postalCode = adresse.getPostalCode() != null ? adresse.getPostalCode() : "";
String city = adresse.getCity() != null ? adresse.getCity() : "Non renseignée";
document.add(newBodyLine((postalCode + " " + city).trim().isEmpty() ? "Non renseignée" : (postalCode + " " + city).trim()));

String countryLibelle = adresse.getCountry() != null && adresse.getCountry().getLibelle() != null
        ? adresse.getCountry().getLibelle()
        : "Non renseigné";
document.add(newBodyLine(countryLibelle));
```

(On peut affiner le cas « postalCode + city » pour afficher « Non renseignée » seulement si les deux sont vides.)

### 4.6 Section « FILIATION »

- Titre de section : même style que les autres sections.
- **Père** : une ou plusieurs lignes selon les champs disponibles :
  - Prénom + Nom (comme aujourd’hui).
  - Date de naissance : `demande.getFatherBirthDate() != null ? demande.getFatherBirthDate().toString() : null`.
  - Lieu de naissance : `getFatherBirthPlace()` + pays (libellé de `getFatherBirthCountry()`).
  - Afficher « Père: Non renseigné » seulement si prénom et nom sont vides/null.
- **Mère** : même schéma avec `getMotherFirstName()`, `getMotherLastName()`, `getMotherBirthDate()`, `getMotherBirthPlace()`, `getMotherBirthCountry()`.

Exemple pour le père (après le titre « FILIATION ») :

```java
boolean fatherPresent = demande.getFatherFirstName() != null || demande.getFatherLastName() != null;
if (fatherPresent) {
    String fatherNames = ((demande.getFatherFirstName() != null ? demande.getFatherFirstName() : "")
            + " " + (demande.getFatherLastName() != null ? demande.getFatherLastName() : "")).trim();
    document.add(newBodyLine("Père: " + fatherNames));
    if (demande.getFatherBirthDate() != null) {
        document.add(newBodyLine("  Date de naissance: " + demande.getFatherBirthDate()));
    }
    if (demande.getFatherBirthPlace() != null || (demande.getFatherBirthCountry() != null && demande.getFatherBirthCountry().getLibelle() != null)) {
        String place = demande.getFatherBirthPlace() != null ? demande.getFatherBirthPlace() : "";
        String country = demande.getFatherBirthCountry() != null && demande.getFatherBirthCountry().getLibelle() != null
                ? demande.getFatherBirthCountry().getLibelle() : "";
        document.add(newBodyLine("  Lieu de naissance: " + (place + ", " + country).replaceAll(", $", "").trim()));
    }
} else {
    document.add(newBodyLine("Père: Non renseigné"));
}
```

Idem pour la mère avec les getters `Mother*`.

### 4.7 Date de génération et pied de page

- Date de génération : `FONT_SIZE_META`, `COLOR_META`, italique, et un petit `setMarginTop` si besoin pour séparer du bloc filiation.
- Pied de page : `FONT_SIZE_FOOTER`, `COLOR_META`, italique.

Exemple :

```java
Paragraph generation = new Paragraph("Document généré le " +
        LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm")));
generation.setFontSize(PdfStyleConfig.FONT_SIZE_META);
generation.setFontColor(PdfStyleConfig.COLOR_META);
generation.setItalic();
generation.setMarginTop(PdfStyleConfig.SPACING_BEFORE_SECTION);
document.add(generation);

Paragraph footer = new Paragraph("Ce document a été généré automatiquement par le système eConsulat");
footer.setFontSize(PdfStyleConfig.FONT_SIZE_FOOTER);
footer.setFontColor(PdfStyleConfig.COLOR_META);
footer.setItalic();
footer.setMarginTop(PdfStyleConfig.SPACING_BETWEEN_LINES);
document.add(footer);
```

---

## 5. Récapitulatif des changements de données

| Donnée | Actuel | Après |
|--------|--------|--------|
| Civilité | Non affichée | Affichée (libellé de `Demande.civilite`) |
| Adresse – boîte | Non affichée | Affichée si présente (ex. "bt 5") |
| Adresse – code postal | Non affiché | Affiché avec la ville |
| Père | Prénom + nom uniquement | + date de naissance, lieu + pays de naissance si présents |
| Mère | Prénom + nom uniquement | + date de naissance, lieu + pays de naissance si présents |

---

## 6. Ordre de mise en œuvre recommandé

1. Créer `PdfStyleConfig` avec marges, couleurs (ou tout en noir), tailles et espacements.
2. Dans `PdfDocumentService` : ajouter `newBodyLine`, appliquer marges et styles au titre, au type de document et aux titres de section.
3. Ajouter la civilité et passer toutes les lignes « demandeur » en `newBodyLine`.
4. Étendre la section adresse (rue + boîte, code postal + ville, pays) avec `newBodyLine`.
5. Étendre la filiation (père puis mère) avec les champs date/lieu/pays.
6. Appliquer les styles métadonnées au paragraphe de date de génération et au footer.

---

## 7. Tests et régression

- Relancer les tests existants de `PdfDocumentService` (génération, nom de fichier, pas d’exception).
- Générer un PDF de test avec une demande contenant : civilité, adresse avec boîte et code postal, père et mère avec dates/lieux renseignés, et une demande avec champs optionnels vides, pour vérifier l’absence de NPE et le bon affichage de « Non renseigné » / « Non renseignée ».

Aucun fichier n’est modifié dans ce document ; il sert de spécification pour l’implémentation à faire après votre validation.
