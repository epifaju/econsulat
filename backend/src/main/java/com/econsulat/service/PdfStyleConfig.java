package com.econsulat.service;

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
    public static final DeviceRgb COLOR_TITLE = new DeviceRgb(0x0D, 0x47, 0xA1);
    /** Sous-titres de section (ex. "INFORMATIONS DU DEMANDEUR") */
    public static final DeviceRgb COLOR_SECTION_HEADER = new DeviceRgb(0x15, 0x65, 0xC0);
    /** Corps de texte */
    public static final DeviceRgb COLOR_BODY = new DeviceRgb(0, 0, 0);
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

    // ----- Structure visuelle (tableaux, sections) -----
    /** Barre verticale à gauche de chaque section (points) */
    public static final float SECTION_LEFT_BORDER_WIDTH = 3f;
    /** Couleur de la barre de section */
    public static final DeviceRgb COLOR_SECTION_BORDER = new DeviceRgb(0x15, 0x65, 0xC0);
    /** Padding interne du bloc de section (points) */
    public static final float SECTION_PADDING_LEFT = 10f;
    /** Largeur colonne libellé du tableau (pourcent) */
    public static final float TABLE_LABEL_WIDTH_PERCENT = 38f;
    /** Largeur colonne valeur (pourcent) */
    public static final float TABLE_VALUE_WIDTH_PERCENT = 62f;
    /** Padding des cellules (points) */
    public static final float TABLE_CELL_PADDING = 4f;

    // ----- Structure du contenu et lisibilité -----
    /** Fond très léger du bloc de section (gris clair) */
    public static final DeviceRgb COLOR_SECTION_BACKGROUND = new DeviceRgb(0xF5, 0xF5, 0xF5);
    /** Padding vertical/horizontal du bloc de section (points) */
    public static final float SECTION_PADDING = 12f;
    /** Épaisseur du filet sous le titre de section (points) */
    public static final float SECTION_HEADER_BORDER_BOTTOM_WIDTH = 1f;
    /** Couleur du filet entre les lignes du tableau (gris léger) */
    public static final DeviceRgb TABLE_ROW_BORDER_COLOR = new DeviceRgb(0xE0, 0xE0, 0xE0);
    /** Épaisseur du filet entre les lignes du tableau (points) */
    public static final float TABLE_ROW_BORDER_WIDTH = 0.5f;

    // ----- En-tête et pied de page (répétés sur chaque page) -----
    /** Taille de police de l'en-tête */
    public static final float FONT_SIZE_HEADER = 10f;
    /** Distance du bord haut pour l'en-tête (points) */
    public static final float HEADER_TOP_OFFSET = 28f;
    /** Distance du bord bas pour le pied de page (points) */
    public static final float FOOTER_BOTTOM_OFFSET = 28f;
}
