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
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

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
import java.util.Map;
import java.util.UUID;

@Service
public class PdfDocumentService {

    private static final Logger log = LoggerFactory.getLogger(PdfDocumentService.class);

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

    @Value("${app.templates.dir:templates}")
    private String templatesDir;

    /**
     * Génère un document PDF simple et robuste pour une demande
     */
    public GeneratedDocument generatePdfDocument(Long demandeId, Long documentTypeId, User currentUser) {
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
                generateSimplePdf(demande, documentType, tempPath.toString());

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

            return savedDoc;

        } catch (Exception e) {
            log.error("Erreur génération PDF - demandeId: {}", demandeId, e);
            throw new RuntimeException("Erreur lors de la génération du document PDF: " + e.getMessage(), e);
        }
    }

    /**
     * Génère un PDF simple et robuste sans filigrane
     */
    private void generateSimplePdf(Demande demande, DocumentType documentType, String outputPath)
            throws IOException {

        log.debug("Génération PDF simple: {}", outputPath);

        try {
            createPdfWithData(demande, documentType, outputPath);

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
                        demande.getFirstName() + " " + demande.getLastName());
                byte[] watermarkedPdf = watermarkService.addSimpleWatermarkToPdf(pdfBytes, watermarkText);
                Files.write(Paths.get(outputPath), watermarkedPdf);
                log.debug("Watermark ajouté au PDF");
            } catch (Exception watermarkError) {
                log.warn("Erreur ajout watermark PDF, document généré sans watermark: {}", watermarkError.getMessage());
            }

        } catch (Exception e) {
            log.error("Erreur génération PDF simple", e);
            throw e;
        }
    }

    /**
     * Crée un PDF directement avec iText en utilisant les données de la demande
     */
    private void createPdfWithData(Demande demande, DocumentType documentType, String pdfPath) throws IOException {
        log.debug("Création PDF: {}", pdfPath);

        try (PdfWriter writer = new PdfWriter(pdfPath);
                PdfDocument pdf = new PdfDocument(writer);
                Document document = new Document(pdf, PageSize.A4)) {

            // Titre principal
            Paragraph title = new Paragraph("DOCUMENT OFFICIEL - eCONSULAT");
            title.setFontSize(18);
            title.setBold();
            document.add(title);

            // Type de document
            Paragraph docType = new Paragraph(
                    "Type: " + (documentType != null ? documentType.getLibelle() : "Non spécifié"));
            docType.setFontSize(14);
            document.add(docType);

            // Espacement
            document.add(new Paragraph(" "));

            // Informations du demandeur
            Paragraph demandeur = new Paragraph("INFORMATIONS DU DEMANDEUR");
            demandeur.setFontSize(12);
            demandeur.setBold();
            document.add(demandeur);

            document.add(
                    new Paragraph("Nom: " + (demande.getLastName() != null ? demande.getLastName() : "Non renseigné")));
            document.add(new Paragraph(
                    "Prénom: " + (demande.getFirstName() != null ? demande.getFirstName() : "Non renseigné")));
            document.add(new Paragraph("Date de naissance: "
                    + (demande.getBirthDate() != null ? demande.getBirthDate().toString() : "Non renseignée")));

            // Lieu de naissance
            String birthPlace = demande.getBirthPlace() != null ? demande.getBirthPlace() : "Non renseigné";
            String birthCountry = demande.getBirthCountry() != null && demande.getBirthCountry().getLibelle() != null
                    ? demande.getBirthCountry().getLibelle()
                    : "Non renseigné";
            document.add(new Paragraph("Lieu de naissance: " + birthPlace + ", " + birthCountry));

            // Espacement
            document.add(new Paragraph(" "));

            // Adresse
            if (demande.getAdresse() != null) {
                Paragraph adresse = new Paragraph("ADRESSE");
                adresse.setFontSize(12);
                adresse.setBold();
                document.add(adresse);

                String streetNumber = demande.getAdresse().getStreetNumber() != null
                        ? demande.getAdresse().getStreetNumber()
                        : "";
                String streetName = demande.getAdresse().getStreetName() != null ? demande.getAdresse().getStreetName()
                        : "";
                document.add(new Paragraph(streetNumber + " " + streetName));

                String city = demande.getAdresse().getCity() != null ? demande.getAdresse().getCity()
                        : "Non renseignée";
                String country = demande.getAdresse().getCountry() != null
                        && demande.getAdresse().getCountry().getLibelle() != null
                                ? demande.getAdresse().getCountry().getLibelle()
                                : "Non renseigné";
                document.add(new Paragraph(city + ", " + country));
            } else {
                document.add(new Paragraph("ADRESSE: Non renseignée"));
            }

            // Espacement
            document.add(new Paragraph(" "));

            // Filiation
            Paragraph filiation = new Paragraph("FILIATION");
            filiation.setFontSize(12);
            filiation.setBold();
            document.add(filiation);

            // Père (si disponible)
            if (demande.getFatherFirstName() != null && demande.getFatherLastName() != null) {
                document.add(
                        new Paragraph("Père: " + demande.getFatherFirstName() + " " + demande.getFatherLastName()));
            } else {
                document.add(new Paragraph("Père: Non renseigné"));
            }

            // Mère (si disponible)
            if (demande.getMotherFirstName() != null && demande.getMotherLastName() != null) {
                document.add(
                        new Paragraph("Mère: " + demande.getMotherFirstName() + " " + demande.getMotherLastName()));
            } else {
                document.add(new Paragraph("Mère: Non renseignée"));
            }

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

        log.debug("PDF créé: {}", pdfPath);
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