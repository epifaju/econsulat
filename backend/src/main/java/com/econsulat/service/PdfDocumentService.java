package com.econsulat.service;

import com.econsulat.model.Demande;
import com.econsulat.model.DocumentType;
import com.econsulat.model.GeneratedDocument;
import com.econsulat.model.User;
import com.econsulat.repository.DemandeRepository;
import com.econsulat.repository.DocumentTypeRepository;
import com.econsulat.repository.GeneratedDocumentRepository;
import com.econsulat.service.WatermarkService;
import com.econsulat.storage.StorageService;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfDocumentInfo;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.context.MessageSource;

@Service
public class PdfDocumentService {

    private static final Logger log = LoggerFactory.getLogger(PdfDocumentService.class);

    /** Types de document pour lesquels la section Filiation est affichée (libellés en minuscules). */
    private static final Set<String> DOCUMENT_TYPES_WITH_FILIATION = Set.of(
            "acte de naissance",
            "certificat de mariage"
    );

    @Autowired
    private DemandeRepository demandeRepository;

    @Autowired
    private DocumentTypeRepository documentTypeRepository;

    @Autowired
    private GeneratedDocumentRepository generatedDocumentRepository;

    @Autowired
    private WatermarkService watermarkService;

    @Autowired
    private StorageService storageService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private EmailNotificationService emailNotificationService;

    @Value("${app.templates.dir:templates}")
    private String templatesDir;

    /**
     * Génère un document PDF pour une demande (locale par défaut : français).
     */
    public GeneratedDocument generatePdfDocument(Long demandeId, Long documentTypeId, User currentUser) {
        return generatePdfDocument(demandeId, documentTypeId, currentUser, null);
    }

    /**
     * Génère un document PDF pour une demande avec prise en charge i18n.
     * @param locale langue du document (titres, libellés). Si null, utilise le français.
     */
    public GeneratedDocument generatePdfDocument(Long demandeId, Long documentTypeId, User currentUser, Locale locale) {
        Locale docLocale = locale != null ? locale : Locale.FRENCH;
        try {
            log.debug("Génération PDF - demandeId: {}, documentTypeId: {}", demandeId, documentTypeId);

            Demande demande = demandeRepository.findById(demandeId)
                    .orElseThrow(() -> new RuntimeException("Demande non trouvée avec l'ID: " + demandeId));

            DocumentType documentType = demande.getDocumentType();
            if (documentType == null) {
                throw new RuntimeException("Le type de document n'est pas défini pour cette demande");
            }

            validateDemandeData(demande);

            GeneratedDocument existingDoc = generatedDocumentRepository
                    .findPdfDocumentByDemandeAndType(demandeId, documentTypeId)
                    .orElse(null);

            if (existingDoc != null) {
                log.debug("Document PDF déjà généré pour demande {} type {}, retour existant", demandeId, documentTypeId);
                return existingDoc;
            }

            String fileName = generatePdfFileName(demande, documentType);
            log.debug("Génération PDF - fichier: {}", fileName);

            Path tempPath = Files.createTempFile("pdfgen_", ".pdf");
            try {
                generateSimplePdf(demande, documentType, tempPath.toString(), docLocale);

                if (!Files.exists(tempPath)) {
                    throw new RuntimeException("Le fichier PDF n'a pas été généré correctement");
                }
                long fileSize = Files.size(tempPath);
                log.debug("Fichier PDF créé: {} bytes", fileSize);

                String actualExtension = getFileExtension(tempPath.toString());
                if (!".pdf".equals(actualExtension)) {
                    log.warn("Fichier créé sans extension .pdf: {}", actualExtension);
                }

                byte[] pdfBytes = Files.readAllBytes(tempPath);
                try {
                    byte[] header = new byte[4];
                    System.arraycopy(pdfBytes, 0, header, 0, Math.min(4, pdfBytes.length));
                    String fileSignature = new String(header);
                    if (!fileSignature.startsWith("%PDF")) {
                        log.error("Fichier généré n'est pas un PDF valide, signature: {}", fileSignature);
                        throw new RuntimeException("Le fichier généré n'est pas un PDF valide. Signature: " + fileSignature);
                    }
                } catch (Exception e) {
                    throw new RuntimeException("Impossible de vérifier l'intégrité du PDF", e);
                }

                storageService.writeDocument(fileName, pdfBytes);
            } finally {
                Files.deleteIfExists(tempPath);
            }

            // Créer l'enregistrement en base
            GeneratedDocument generatedDocument = new GeneratedDocument();
            generatedDocument.setDemande(demande);
            generatedDocument.setDocumentType(documentType);
            generatedDocument.setFileName(fileName);
            generatedDocument.setFilePath(storageService.getStoredDocumentPath(fileName));
            generatedDocument.setCreatedBy(currentUser);
            generatedDocument.setStatus("GENERATED");
            generatedDocument.setExpiresAt(LocalDateTime.now().plusDays(30));
            generatedDocument.setCreatedAt(LocalDateTime.now());

            if (!fileName.toLowerCase().endsWith(".pdf")) {
                log.error("Nom de fichier sans extension .pdf: {}", fileName);
                throw new RuntimeException("Le nom de fichier doit avoir l'extension .pdf");
            }

            GeneratedDocument savedDoc = generatedDocumentRepository.save(generatedDocument);
            log.info("Document PDF enregistré - id: {}, fichier: {}", savedDoc.getId(), savedDoc.getFileName());

            User demandUser = demande.getUser();
            if (demandUser != null) {
                try {
                    emailNotificationService.sendDocumentReadyNotification(demande, demandUser);
                } catch (Exception e) {
                    log.warn("Notification document prêt non envoyée pour demande {} : {}", demande.getId(), e.getMessage());
                }
            }

            return savedDoc;

        } catch (Exception e) {
            log.error("Erreur génération PDF - demandeId: {}", demandeId, e);
            throw new RuntimeException("Erreur lors de la génération du document PDF: " + e.getMessage(), e);
        }
    }

    /**
     * Génère un PDF simple avec filigrane (i18n selon locale).
     */
    private void generateSimplePdf(Demande demande, DocumentType documentType, String outputPath, Locale locale)
            throws IOException {

        log.debug("Génération PDF simple: {}", outputPath);

        try {
            createPdfWithData(demande, documentType, outputPath, locale);

            if (!Files.exists(Paths.get(outputPath))) {
                throw new RuntimeException("Le fichier PDF n'a pas été généré correctement: " + outputPath);
            }

            long fileSize = Files.size(Paths.get(outputPath));
            log.debug("PDF créé, taille: {} bytes", fileSize);

            try (PdfReader reader = new PdfReader(outputPath);
                    PdfDocument pdfDoc = new PdfDocument(reader)) {
                int pages = pdfDoc.getNumberOfPages();
                log.debug("PDF validé, nombre de pages: {}", pages);
            } catch (Exception e) {
                log.warn("Validation PDF échouée: {}", e.getMessage());
                throw new IOException("Le PDF généré n'est pas valide: " + e.getMessage(), e);
            }

            try {
                byte[] pdfBytes = Files.readAllBytes(Paths.get(outputPath));
                String watermarkText = watermarkService.generateCustomWatermark(
                        documentType != null ? documentType.getLibelle() : "Document",
                        (demande.getFirstName() != null ? demande.getFirstName() : "") + " " + (demande.getLastName() != null ? demande.getLastName() : ""));
                byte[] watermarkedPdf = watermarkService.addSimpleWatermarkToPdf(pdfBytes, watermarkText, locale);
                Files.write(Paths.get(outputPath), watermarkedPdf);
                log.debug("Filigrane ajouté sur toutes les pages du PDF");
            } catch (Exception watermarkError) {
                log.warn("Erreur ajout filigrane PDF, document généré sans filigrane: {}", watermarkError.getMessage());
            }

        } catch (Exception e) {
            log.error("Erreur génération PDF simple", e);
            throw e;
        }
    }

    /**
     * Crée un PDF avec iText (charte + données + i18n selon locale).
     */
    private void createPdfWithData(Demande demande, DocumentType documentType, String pdfPath, Locale locale) throws IOException {
        log.debug("Création PDF: {}", pdfPath);

        try (PdfWriter writer = new PdfWriter(pdfPath);
                PdfDocument pdf = new PdfDocument(writer);
                Document document = new Document(pdf, PageSize.A4)) {

            String typeLabel = documentType != null && documentType.getLibelle() != null
                    ? documentType.getLibelle()
                    : msg("pdf.typeNotSpecified", null, locale);
            setPdfMetadata(pdf, typeLabel, locale);

            pdf.addEventHandler(PdfDocumentEvent.START_PAGE, new PdfHeaderFooterHandler(documentType, pdf, messageSource, locale));

            document.setMargins(
                    PdfStyleConfig.MARGIN_TOP_BOTTOM,
                    PdfStyleConfig.MARGIN_LEFT_RIGHT,
                    PdfStyleConfig.MARGIN_TOP_BOTTOM,
                    PdfStyleConfig.MARGIN_LEFT_RIGHT);

            String titleText = msg("pdf.title", null, locale);
            Paragraph title = new Paragraph(titleText);
            title.setFontSize(PdfStyleConfig.FONT_SIZE_TITLE);
            title.setBold();
            title.setFontColor(PdfStyleConfig.COLOR_TITLE);
            title.setMarginBottom(PdfStyleConfig.SPACING_AFTER_TITLE);
            document.add(title);

            Paragraph docType = new Paragraph(msg("pdf.typeLabel", new Object[]{typeLabel}, locale));
            docType.setFontSize(PdfStyleConfig.FONT_SIZE_DOC_TYPE);
            docType.setFontColor(PdfStyleConfig.COLOR_BODY);
            docType.setMarginBottom(PdfStyleConfig.SPACING_AFTER_DOC_TYPE);
            document.add(docType);

            Div demandeurDiv = newSectionDiv();
            demandeurDiv.add(newSectionHeader(msg("pdf.section.applicant", null, locale)));
            Table demandeurTable = newLabelValueTable();
            String civiliteLibelle = demande.getCivilite() != null && demande.getCivilite().getLibelle() != null
                    ? demande.getCivilite().getLibelle()
                    : msg("pdf.notProvided", null, locale);
            addLabelValueRow(demandeurTable, msg("pdf.label.civility", null, locale), civiliteLibelle);
            addLabelValueRow(demandeurTable, msg("pdf.label.lastName", null, locale), demande.getLastName() != null ? demande.getLastName() : msg("pdf.notProvided", null, locale));
            addLabelValueRow(demandeurTable, msg("pdf.label.firstName", null, locale), demande.getFirstName() != null ? demande.getFirstName() : msg("pdf.notProvided", null, locale));
            addLabelValueRow(demandeurTable, msg("pdf.label.birthDate", null, locale),
                    demande.getBirthDate() != null ? demande.getBirthDate().toString() : msg("pdf.notProvidedF", null, locale));
            addLabelValueRow(demandeurTable, msg("pdf.label.birthPlace", null, locale),
                    demande.getBirthPlace() != null ? demande.getBirthPlace() : msg("pdf.notProvided", null, locale));
            String birthCountryLibelle = demande.getBirthCountry() != null && demande.getBirthCountry().getLibelle() != null
                    ? demande.getBirthCountry().getLibelle()
                    : msg("pdf.notProvided", null, locale);
            addLabelValueRow(demandeurTable, msg("pdf.label.birthCountry", null, locale), birthCountryLibelle);
            demandeurDiv.add(demandeurTable);
            document.add(demandeurDiv);

            Div adresseDiv = newSectionDiv();
            adresseDiv.add(newSectionHeader(msg("pdf.section.address", null, locale)));
            Table adresseTable = newLabelValueTable();
            if (demande.getAdresse() != null) {
                var adresse = demande.getAdresse();
                addLabelValueRow(adresseTable, msg("pdf.label.streetNumber", null, locale),
                        adresse.getStreetNumber() != null ? adresse.getStreetNumber() : msg("pdf.notProvided", null, locale));
                addLabelValueRow(adresseTable, msg("pdf.label.streetName", null, locale),
                        adresse.getStreetName() != null ? adresse.getStreetName() : msg("pdf.notProvided", null, locale));
                addLabelValueRow(adresseTable, msg("pdf.label.boxNumber", null, locale),
                        adresse.getBoxNumber() != null && !adresse.getBoxNumber().isBlank() ? adresse.getBoxNumber() : "—");
                addLabelValueRow(adresseTable, msg("pdf.label.postalCode", null, locale),
                        adresse.getPostalCode() != null ? adresse.getPostalCode() : msg("pdf.notProvided", null, locale));
                addLabelValueRow(adresseTable, msg("pdf.label.city", null, locale),
                        adresse.getCity() != null ? adresse.getCity() : msg("pdf.notProvidedF", null, locale));
                String countryLibelle = adresse.getCountry() != null && adresse.getCountry().getLibelle() != null
                        ? adresse.getCountry().getLibelle()
                        : msg("pdf.notProvided", null, locale);
                addLabelValueRow(adresseTable, msg("pdf.label.country", null, locale), countryLibelle);
            } else {
                addLabelValueRow(adresseTable, msg("pdf.label.address", null, locale), msg("pdf.notProvidedF", null, locale));
            }
            adresseDiv.add(adresseTable);
            document.add(adresseDiv);

            if (isFiliationSectionRequired(documentType)) {
                Div filiationDiv = newSectionDiv();
                filiationDiv.add(newSectionHeader(msg("pdf.section.filiation", null, locale)));
                filiationDiv.add(newSubSectionLabel(msg("pdf.father", null, locale)));
                Table pereTable = newLabelValueTable();
                addLabelValueRow(pereTable, msg("pdf.label.firstName", null, locale), demande.getFatherFirstName() != null ? demande.getFatherFirstName() : msg("pdf.notProvided", null, locale));
                addLabelValueRow(pereTable, msg("pdf.label.lastName", null, locale), demande.getFatherLastName() != null ? demande.getFatherLastName() : msg("pdf.notProvided", null, locale));
                addLabelValueRow(pereTable, msg("pdf.label.birthDate", null, locale),
                        demande.getFatherBirthDate() != null ? demande.getFatherBirthDate().toString() : msg("pdf.notProvidedF", null, locale));
                addLabelValueRow(pereTable, msg("pdf.label.birthPlace", null, locale),
                        demande.getFatherBirthPlace() != null ? demande.getFatherBirthPlace() : msg("pdf.notProvided", null, locale));
                String fatherCountryLibelle = demande.getFatherBirthCountry() != null && demande.getFatherBirthCountry().getLibelle() != null
                        ? demande.getFatherBirthCountry().getLibelle() : msg("pdf.notProvided", null, locale);
                addLabelValueRow(pereTable, msg("pdf.label.birthCountry", null, locale), fatherCountryLibelle);
                filiationDiv.add(pereTable);
                filiationDiv.add(newSubSectionLabel(msg("pdf.mother", null, locale)));
                Table mereTable = newLabelValueTable();
                addLabelValueRow(mereTable, msg("pdf.label.firstName", null, locale), demande.getMotherFirstName() != null ? demande.getMotherFirstName() : msg("pdf.notProvidedF", null, locale));
                addLabelValueRow(mereTable, msg("pdf.label.lastName", null, locale), demande.getMotherLastName() != null ? demande.getMotherLastName() : msg("pdf.notProvided", null, locale));
                addLabelValueRow(mereTable, msg("pdf.label.birthDate", null, locale),
                        demande.getMotherBirthDate() != null ? demande.getMotherBirthDate().toString() : msg("pdf.notProvidedF", null, locale));
                addLabelValueRow(mereTable, msg("pdf.label.birthPlace", null, locale),
                        demande.getMotherBirthPlace() != null ? demande.getMotherBirthPlace() : msg("pdf.notProvided", null, locale));
                String motherCountryLibelle = demande.getMotherBirthCountry() != null && demande.getMotherBirthCountry().getLibelle() != null
                        ? demande.getMotherBirthCountry().getLibelle() : msg("pdf.notProvided", null, locale);
                addLabelValueRow(mereTable, msg("pdf.label.birthCountry", null, locale), motherCountryLibelle);
                filiationDiv.add(mereTable);
                document.add(filiationDiv);
            }

            String generatedOnText = msg("pdf.generatedOn", new Object[]{
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm"))
            }, locale);
            Paragraph generation = new Paragraph(generatedOnText);
            generation.setFontSize(PdfStyleConfig.FONT_SIZE_META);
            generation.setFontColor(PdfStyleConfig.COLOR_META);
            generation.setItalic();
            generation.setMarginTop(PdfStyleConfig.SPACING_BEFORE_SECTION);
            document.add(generation);
        }

        log.debug("PDF créé: {}", pdfPath);
    }

    /**
     * Renseigne les métadonnées du document PDF (titre, auteur, créateur) pour l'accessibilité et l'indexation.
     * Les valeurs null ou vides sont remplacées par un libellé par défaut (iText n'accepte pas null).
     */
    private void setPdfMetadata(PdfDocument pdf, String typeLabel, Locale locale) {
        PdfDocumentInfo info = pdf.getDocumentInfo();
        String title = messageSource.getMessage("pdf.metadata.title", new Object[]{typeLabel}, "eConsulat — " + typeLabel, locale);
        String author = messageSource.getMessage("pdf.metadata.author", null, "eConsulat", locale);
        String creator = messageSource.getMessage("pdf.metadata.creator", null, "eConsulat", locale);
        info.setTitle(title != null && !title.isEmpty() ? title : "eConsulat");
        info.setAuthor(author != null && !author.isEmpty() ? author : "eConsulat");
        info.setCreator(creator != null && !creator.isEmpty() ? creator : "eConsulat");
        info.setSubject(typeLabel != null && !typeLabel.isEmpty() ? typeLabel : "Document eConsulat");
    }

    private String msg(String code, Object[] args, Locale locale) {
        return messageSource.getMessage(code, args, code, locale);
    }

    /**
     * Indique si la section Filiation doit être affichée selon le type de document
     * (acte de naissance, certificat de mariage ; pas pour passeport, certificats d'hébergement/résidence/célibat).
     */
    private boolean isFiliationSectionRequired(DocumentType documentType) {
        if (documentType == null || documentType.getLibelle() == null) {
            return false;
        }
        String libelle = documentType.getLibelle().trim().toLowerCase();
        return DOCUMENT_TYPES_WITH_FILIATION.contains(libelle);
    }

    /**
     * Crée un paragraphe de corps de texte avec la charte (taille, couleur, espacement).
     */
    private Paragraph newBodyLine(String text) {
        Paragraph p = new Paragraph(text);
        p.setFontSize(PdfStyleConfig.FONT_SIZE_BODY);
        p.setFontColor(PdfStyleConfig.COLOR_BODY);
        p.setMarginBottom(PdfStyleConfig.SPACING_BETWEEN_LINES);
        return p;
    }

    /**
     * Titre de section avec la charte (couleur, espacements, filet horizontal sous le titre).
     */
    private Paragraph newSectionHeader(String text) {
        Paragraph p = new Paragraph(text);
        p.setFontSize(PdfStyleConfig.FONT_SIZE_SECTION);
        p.setBold();
        p.setFontColor(PdfStyleConfig.COLOR_SECTION_HEADER);
        p.setMarginTop(PdfStyleConfig.SPACING_BEFORE_SECTION);
        p.setMarginBottom(PdfStyleConfig.SPACING_AFTER_SECTION);
        p.setBorderBottom(new SolidBorder(PdfStyleConfig.COLOR_SECTION_BORDER, PdfStyleConfig.SECTION_HEADER_BORDER_BOTTOM_WIDTH));
        return p;
    }

    /**
     * Sous-titre de bloc (ex. "Père", "Mère") pour la filiation — alignement avec le modèle.
     */
    private Paragraph newSubSectionLabel(String text) {
        Paragraph p = new Paragraph(text);
        p.setFontSize(PdfStyleConfig.FONT_SIZE_BODY);
        p.setBold();
        p.setFontColor(PdfStyleConfig.COLOR_SECTION_HEADER);
        p.setMarginTop(10f);
        p.setMarginBottom(4f);
        return p;
    }

    /**
     * Bloc de section : bordure gauche colorée, fond gris très léger, padding.
     */
    private Div newSectionDiv() {
        Div div = new Div();
        div.setBorderLeft(new SolidBorder(PdfStyleConfig.COLOR_SECTION_BORDER, PdfStyleConfig.SECTION_LEFT_BORDER_WIDTH));
        div.setBackgroundColor(PdfStyleConfig.COLOR_SECTION_BACKGROUND);
        div.setPadding(PdfStyleConfig.SECTION_PADDING);
        return div;
    }

    /**
     * Tableau 2 colonnes (libellé / valeur), sans bordure.
     */
    private Table newLabelValueTable() {
        Table table = new Table(new UnitValue[]{
                UnitValue.createPercentValue(PdfStyleConfig.TABLE_LABEL_WIDTH_PERCENT),
                UnitValue.createPercentValue(PdfStyleConfig.TABLE_VALUE_WIDTH_PERCENT)
        });
        table.setWidth(UnitValue.createPercentValue(100));
        table.setBorder(Border.NO_BORDER);
        return table;
    }

    /**
     * Ajoute une ligne libellé / valeur au tableau (filet léger sous la ligne pour la lisibilité).
     */
    private void addLabelValueRow(Table table, String label, String value) {
        Cell labelCell = new Cell();
        labelCell.add(new Paragraph(label).setFontSize(PdfStyleConfig.FONT_SIZE_BODY).setFontColor(PdfStyleConfig.COLOR_BODY));
        labelCell.setBorder(Border.NO_BORDER);
        labelCell.setBorderBottom(new SolidBorder(PdfStyleConfig.TABLE_ROW_BORDER_COLOR, PdfStyleConfig.TABLE_ROW_BORDER_WIDTH));
        labelCell.setPadding(PdfStyleConfig.TABLE_CELL_PADDING);
        labelCell.setPaddingBottom(PdfStyleConfig.SPACING_BETWEEN_LINES);
        table.addCell(labelCell);

        Cell valueCell = new Cell();
        valueCell.add(new Paragraph(value != null ? value : "").setFontSize(PdfStyleConfig.FONT_SIZE_BODY).setFontColor(PdfStyleConfig.COLOR_BODY));
        valueCell.setBorder(Border.NO_BORDER);
        valueCell.setBorderBottom(new SolidBorder(PdfStyleConfig.TABLE_ROW_BORDER_COLOR, PdfStyleConfig.TABLE_ROW_BORDER_WIDTH));
        valueCell.setPadding(PdfStyleConfig.TABLE_CELL_PADDING);
        valueCell.setPaddingBottom(PdfStyleConfig.SPACING_BETWEEN_LINES);
        table.addCell(valueCell);
    }

    /**
     * Ajoute une ligne avec une cellule valeur multi-lignes (ex. filiation), filet sous la ligne.
     */
    private void addLabelValueRowMultiLine(Table table, String label, String... valueLines) {
        Cell labelCell = new Cell();
        labelCell.add(new Paragraph(label).setFontSize(PdfStyleConfig.FONT_SIZE_BODY).setFontColor(PdfStyleConfig.COLOR_BODY));
        labelCell.setBorder(Border.NO_BORDER);
        labelCell.setBorderBottom(new SolidBorder(PdfStyleConfig.TABLE_ROW_BORDER_COLOR, PdfStyleConfig.TABLE_ROW_BORDER_WIDTH));
        labelCell.setPadding(PdfStyleConfig.TABLE_CELL_PADDING);
        labelCell.setPaddingBottom(PdfStyleConfig.SPACING_BETWEEN_LINES);
        table.addCell(labelCell);

        Cell valueCell = new Cell();
        for (String line : valueLines) {
            if (line != null && !line.isEmpty()) {
                valueCell.add(new Paragraph(line).setFontSize(PdfStyleConfig.FONT_SIZE_BODY).setFontColor(PdfStyleConfig.COLOR_BODY));
            }
        }
        valueCell.setBorder(Border.NO_BORDER);
        valueCell.setBorderBottom(new SolidBorder(PdfStyleConfig.TABLE_ROW_BORDER_COLOR, PdfStyleConfig.TABLE_ROW_BORDER_WIDTH));
        valueCell.setPadding(PdfStyleConfig.TABLE_CELL_PADDING);
        valueCell.setPaddingBottom(PdfStyleConfig.SPACING_BETWEEN_LINES);
        table.addCell(valueCell);
    }

    // Méthode de filigrane supprimée pour éviter la corruption des PDFs

    // Méthode de filigrane de page supprimée pour éviter la corruption des PDFs

    /**
     * Valide les données de la demande
     */
    private void validateDemandeData(Demande demande) {
        if (demande.getFirstName() == null || demande.getFirstName().trim().isEmpty()) {
            throw new RuntimeException("Le prénom est requis");
        }
        if (demande.getLastName() == null || demande.getLastName().trim().isEmpty()) {
            throw new RuntimeException("Le nom de famille est requis");
        }
        if (demande.getBirthDate() == null) {
            throw new RuntimeException("La date de naissance est requise");
        }
        if (demande.getBirthPlace() == null || demande.getBirthPlace().trim().isEmpty()) {
            throw new RuntimeException("Le lieu de naissance est requis");
        }
        if (demande.getBirthCountry() == null) {
            throw new RuntimeException("Le pays de naissance est requis");
        }
        if (demande.getAdresse() == null) {
            throw new RuntimeException("L'adresse est requise");
        }
        // Les champs de filiation sont optionnels, on ne les valide pas strictement
    }

    /**
     * Génère le nom du fichier PDF
     */
    private String generatePdfFileName(Demande demande, DocumentType documentType) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String userInfo = (demande.getFirstName() != null ? demande.getFirstName() : "Unknown") + "_" +
                (demande.getLastName() != null ? demande.getLastName() : "Unknown");
        String docType = documentType != null && documentType.getLibelle() != null
                ? documentType.getLibelle().replaceAll("[^a-zA-Z0-9]", "_")
                : "Document";

        // Ajouter un préfixe "PDF_" pour éviter les conflits avec
        // DocumentGenerationService
        return String.format("PDF_%s_%s_%s_%s.pdf",
                docType, userInfo, timestamp, UUID.randomUUID().toString().substring(0, 8));
    }

    /**
     * Télécharge un document PDF généré
     */
    public byte[] downloadPdfDocument(Long documentId) throws IOException {
        GeneratedDocument doc = generatedDocumentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document non trouvé"));

        Path filePath = Paths.get(doc.getFilePath());
        if (!Files.exists(filePath)) {
            throw new RuntimeException("Fichier PDF non trouvé: " + doc.getFilePath());
        }

        log.debug("Téléchargement PDF id: {}, taille: {} bytes", documentId, Files.size(filePath));

        // Marquer comme téléchargé
        doc.setStatus("DOWNLOADED");
        doc.setDownloadedAt(LocalDateTime.now());
        generatedDocumentRepository.save(doc);

        return Files.readAllBytes(filePath);
    }

    /**
     * Récupère l'extension d'un fichier
     */
    private String getFileExtension(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return "";
        }
        int lastDotIndex = filePath.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        }
        return filePath.substring(lastDotIndex).toLowerCase();
    }
}