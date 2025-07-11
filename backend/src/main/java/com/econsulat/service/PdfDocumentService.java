package com.econsulat.service;

import com.econsulat.model.Demande;
import com.econsulat.model.DocumentType;
import com.econsulat.model.GeneratedDocument;
import com.econsulat.model.User;
import com.econsulat.repository.DemandeRepository;
import com.econsulat.repository.DocumentTypeRepository;
import com.econsulat.repository.GeneratedDocumentRepository;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

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
import java.util.Map;
import java.util.UUID;

@Service
public class PdfDocumentService {

    @Autowired
    private DemandeRepository demandeRepository;

    @Autowired
    private DocumentTypeRepository documentTypeRepository;

    @Autowired
    private GeneratedDocumentRepository generatedDocumentRepository;

    @Value("${app.documents.dir:documents}")
    private String documentsDir;

    @Value("${app.templates.dir:templates}")
    private String templatesDir;

    /**
     * Génère un document PDF avec filigrane pour une demande
     */
    public GeneratedDocument generatePdfDocumentWithWatermark(Long demandeId, Long documentTypeId, User currentUser) {
        Demande demande = demandeRepository.findById(demandeId)
                .orElseThrow(() -> new RuntimeException("Demande non trouvée"));

        DocumentType documentType = documentTypeRepository.findById(documentTypeId)
                .orElseThrow(() -> new RuntimeException("Type de document non trouvé"));

        // Validation des données de la demande
        validateDemandeData(demande);

        // Vérifier si le document a déjà été généré
        GeneratedDocument existingDoc = generatedDocumentRepository
                .findByDemandeAndDocumentType(demandeId, documentTypeId)
                .orElse(null);

        if (existingDoc != null) {
            return existingDoc;
        }

        try {
            // Créer le dossier de documents s'il n'existe pas
            Path documentsPath = Paths.get(documentsDir);
            if (!Files.exists(documentsPath)) {
                Files.createDirectories(documentsPath);
            }

            // Générer le nom du fichier
            String fileName = generatePdfFileName(demande, documentType);
            String filePath = documentsPath.resolve(fileName).toString();

            System.out.println("Génération PDF vers: " + filePath);

            // Générer le document PDF avec filigrane
            generatePdfFromTemplate(demande, documentType, filePath);

            // Vérifier que le fichier PDF a été créé
            if (!Files.exists(Paths.get(filePath))) {
                throw new RuntimeException("Le fichier PDF n'a pas été généré correctement: " + filePath);
            }

            System.out.println("Fichier PDF créé avec succès: " + filePath);

            // Créer l'enregistrement en base
            GeneratedDocument generatedDocument = new GeneratedDocument();
            generatedDocument.setDemande(demande);
            generatedDocument.setDocumentType(documentType);
            generatedDocument.setFileName(fileName);
            generatedDocument.setFilePath(filePath);
            generatedDocument.setCreatedBy(currentUser);
            generatedDocument.setStatus("GENERATED");
            generatedDocument.setExpiresAt(LocalDateTime.now().plusDays(30));
            generatedDocument.setCreatedAt(LocalDateTime.now());

            return generatedDocumentRepository.save(generatedDocument);

        } catch (Exception e) {
            System.err.println("Erreur détaillée lors de la génération PDF: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la génération du document PDF: " + e.getMessage(), e);
        }
    }

    /**
     * Génère un PDF directement avec iText (sans conversion DOCX)
     */
    private void generatePdfFromTemplate(Demande demande, DocumentType documentType, String outputPath)
            throws IOException {

        System.out.println("Début de la génération PDF pour: " + outputPath);

        try {
            // Créer le PDF directement avec iText
            createPdfWithData(demande, documentType, outputPath);

            System.out.println("PDF créé avec succès: " + outputPath);

            // Ajouter le filigrane diagonal au PDF
            addDiagonalWatermarkToPdf(outputPath);

            System.out.println("Filigrane ajouté au PDF: " + outputPath);

            // Vérifier que le fichier PDF a été créé
            if (!Files.exists(Paths.get(outputPath))) {
                throw new RuntimeException("Le fichier PDF n'a pas été généré correctement: " + outputPath);
            }

            long fileSize = Files.size(Paths.get(outputPath));
            System.out.println("Fichier PDF créé avec succès, taille: " + fileSize + " bytes");

        } catch (Exception e) {
            System.err.println("Erreur lors de la génération PDF: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Crée un PDF directement avec iText en utilisant les données de la demande
     */
    private void createPdfWithData(Demande demande, DocumentType documentType, String pdfPath) throws IOException {
        System.out.println("Création du PDF: " + pdfPath);

        try (PdfWriter writer = new PdfWriter(pdfPath);
                PdfDocument pdf = new PdfDocument(writer);
                Document document = new Document(pdf, PageSize.A4)) {

            // Titre principal
            Paragraph title = new Paragraph("DOCUMENT OFFICIEL - eCONSULAT");
            title.setFontSize(18);
            title.setBold();
            document.add(title);

            // Type de document
            Paragraph docType = new Paragraph("Type: " + documentType.getLibelle());
            docType.setFontSize(14);
            document.add(docType);

            // Espacement
            document.add(new Paragraph(" "));

            // Informations du demandeur
            Paragraph demandeur = new Paragraph("INFORMATIONS DU DEMANDEUR");
            demandeur.setFontSize(12);
            demandeur.setBold();
            document.add(demandeur);

            document.add(new Paragraph("Nom: " + demande.getLastName()));
            document.add(new Paragraph("Prénom: " + demande.getFirstName()));
            document.add(new Paragraph("Date de naissance: " + demande.getBirthDate()));
            document.add(new Paragraph("Lieu de naissance: " + demande.getBirthPlace() + ", " +
                    demande.getBirthCountry().getLibelle()));

            // Espacement
            document.add(new Paragraph(" "));

            // Adresse
            Paragraph adresse = new Paragraph("ADRESSE");
            adresse.setFontSize(12);
            adresse.setBold();
            document.add(adresse);

            document.add(new Paragraph(demande.getAdresse().getStreetNumber() + " " +
                    demande.getAdresse().getStreetName()));
            document.add(new Paragraph(demande.getAdresse().getCity() + ", " +
                    demande.getAdresse().getCountry().getLibelle()));

            // Espacement
            document.add(new Paragraph(" "));

            // Filiation
            Paragraph filiation = new Paragraph("FILIATION");
            filiation.setFontSize(12);
            filiation.setBold();
            document.add(filiation);

            document.add(new Paragraph("Père: " + demande.getFatherFirstName() + " " + demande.getFatherLastName()));
            document.add(new Paragraph("Mère: " + demande.getMotherFirstName() + " " + demande.getMotherLastName()));

            // Espacement
            document.add(new Paragraph(" "));

            // Date de génération
            Paragraph generation = new Paragraph("Document généré le " +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm")));
            generation.setFontSize(10);
            generation.setItalic();
            document.add(generation);

            // Espacement
            document.add(new Paragraph(" "));

            // Footer
            Paragraph footer = new Paragraph("Ce document a été généré automatiquement par le système eConsulat");
            footer.setFontSize(8);
            footer.setItalic();
            document.add(footer);
        }

        System.out.println("PDF créé avec succès: " + pdfPath);
    }

    /**
     * Ajoute un filigrane diagonal professionnel au PDF
     */
    private void addDiagonalWatermarkToPdf(String pdfPath) throws IOException {
        System.out.println("Ajout du filigrane au PDF: " + pdfPath);

        try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(pdfPath), new PdfWriter(pdfPath + "_temp"))) {

            // Ajouter le filigrane sur chaque page
            for (int i = 1; i <= pdfDoc.getNumberOfPages(); i++) {
                addDiagonalWatermarkToPage(pdfDoc, i);
            }
        }

        // Remplacer le fichier original
        Files.move(Paths.get(pdfPath + "_temp"), Paths.get(pdfPath),
                java.nio.file.StandardCopyOption.REPLACE_EXISTING);

        System.out.println("Filigrane ajouté avec succès");
    }

    /**
     * Ajoute un filigrane diagonal à une page spécifique
     */
    private void addDiagonalWatermarkToPage(PdfDocument pdfDoc, int pageNumber) throws IOException {
        PdfPage page = pdfDoc.getPage(pageNumber);
        Rectangle pageSize = page.getPageSize();
        PdfCanvas canvas = new PdfCanvas(page);

        // Configuration du texte
        String watermark = "DOCUMENT OFFICIEL - eCONSULAT";
        float fontSize = 48;
        float x = (pageSize.getLeft() + pageSize.getRight()) / 2;
        float y = (pageSize.getTop() + pageSize.getBottom()) / 2;

        // Appliquer une rotation de 45°
        canvas.saveState();
        canvas.setFillColor(ColorConstants.LIGHT_GRAY);
        canvas.setFontAndSize(PdfFontFactory.createFont(), fontSize);
        canvas.beginText();
        canvas.setTextMatrix((float) Math.cos(Math.PI / 4), (float) Math.sin(Math.PI / 4),
                (float) -Math.sin(Math.PI / 4), (float) Math.cos(Math.PI / 4), x, y);
        canvas.showText(watermark);
        canvas.endText();
        canvas.restoreState();
    }

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
        if (demande.getBirthDate() == null || demande.getBirthDate().toString().trim().isEmpty()) {
            throw new RuntimeException("La date de naissance est requise");
        }
        if (demande.getBirthPlace() == null || demande.getBirthPlace().trim().isEmpty()) {
            throw new RuntimeException("Le lieu de naissance est requis");
        }
        if (demande.getAdresse() == null) {
            throw new RuntimeException("L'adresse est requise");
        }
        if (demande.getFatherFirstName() == null || demande.getFatherFirstName().trim().isEmpty()) {
            throw new RuntimeException("Le prénom du père est requis");
        }
        if (demande.getFatherLastName() == null || demande.getFatherLastName().trim().isEmpty()) {
            throw new RuntimeException("Le nom de famille du père est requis");
        }
        if (demande.getMotherFirstName() == null || demande.getMotherFirstName().trim().isEmpty()) {
            throw new RuntimeException("Le prénom de la mère est requis");
        }
        if (demande.getMotherLastName() == null || demande.getMotherLastName().trim().isEmpty()) {
            throw new RuntimeException("Le nom de famille de la mère est requis");
        }
    }

    /**
     * Génère le nom du fichier PDF
     */
    private String generatePdfFileName(Demande demande, DocumentType documentType) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String userInfo = demande.getFirstName() + "_" + demande.getLastName();
        String docType = documentType.getLibelle().replaceAll("[^a-zA-Z0-9]", "_");

        return String.format("%s_%s_%s_%s.pdf",
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

        System.out.println(
                "Téléchargement du fichier: " + doc.getFilePath() + " (taille: " + Files.size(filePath) + " bytes)");

        // Marquer comme téléchargé
        doc.setStatus("DOWNLOADED");
        doc.setDownloadedAt(LocalDateTime.now());
        generatedDocumentRepository.save(doc);

        return Files.readAllBytes(filePath);
    }
}