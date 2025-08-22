package com.econsulat.service;

import com.econsulat.model.Demande;
import com.econsulat.model.DocumentType;
import com.econsulat.model.GeneratedDocument;
import com.econsulat.model.User;
import com.econsulat.repository.DemandeRepository;
import com.econsulat.repository.DocumentTypeRepository;
import com.econsulat.repository.GeneratedDocumentRepository;
import com.econsulat.service.WatermarkService;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
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

    @Autowired
    private WatermarkService watermarkService;

    @Value("${app.documents.dir:documents}")
    private String documentsDir;

    @Value("${app.templates.dir:templates}")
    private String templatesDir;

    /**
     * Génère un document PDF simple et robuste pour une demande
     */
    public GeneratedDocument generatePdfDocument(Long demandeId, Long documentTypeId, User currentUser) {
        String filePath = null;

        try {
            System.out.println("=== DÉBUT GÉNÉRATION PDF ===");
            System.out.println("Demande: " + demandeId + ", Type: " + documentTypeId);

            Demande demande = demandeRepository.findById(demandeId)
                    .orElseThrow(() -> new RuntimeException("Demande non trouvée avec l'ID: " + demandeId));

            // Utiliser le type de document de la demande (enum) et le convertir en entité
            // JPA
            Demande.DocumentType demandeDocumentType = demande.getDocumentType();
            if (demandeDocumentType == null) {
                throw new RuntimeException("Le type de document n'est pas défini pour cette demande");
            }

            // Convertir l'enum en entité JPA DocumentType
            DocumentType documentType = documentTypeRepository.findByLibelle(demandeDocumentType.getDisplayName())
                    .orElseThrow(() -> new RuntimeException(
                            "Type de document JPA non trouvé pour: " + demandeDocumentType.getDisplayName()));

            System.out.println("Demande trouvée: " + demande.getFirstName() + " " + demande.getLastName());
            System.out.println("Type de document de la demande: " + demandeDocumentType.getDisplayName());
            System.out.println("Type de document JPA: " + documentType.getLibelle());
            System.out.println("Type de document demandé: " + documentTypeId);

            // Validation des données de la demande
            validateDemandeData(demande);

            // Vérifier si le document a déjà été généré pour ce type
            GeneratedDocument existingDoc = generatedDocumentRepository
                    .findPdfDocumentByDemandeAndType(demandeId, documentTypeId)
                    .orElse(null);

            if (existingDoc != null) {
                System.out.println("Document déjà généré, retour du document existant");
                System.out.println("Nom existant: " + existingDoc.getFileName());
                System.out.println("Chemin existant: " + existingDoc.getFilePath());
                return existingDoc;
            }

            // Créer le dossier de documents s'il n'existe pas
            Path documentsPath = Paths.get(documentsDir);
            if (!Files.exists(documentsPath)) {
                System.out.println("Création du dossier documents: " + documentsPath);
                Files.createDirectories(documentsPath);
            }

            // Générer le nom du fichier
            String fileName = generatePdfFileName(demande, documentType);
            filePath = documentsPath.resolve(fileName).toString();

            System.out.println("=== GÉNÉRATION PDF ===");
            System.out.println("Nom de fichier généré: " + fileName);
            System.out.println("Chemin complet: " + filePath);
            System.out.println("Extension attendue: .pdf");

            // Générer le document PDF simple (sans filigrane pour l'instant)
            generateSimplePdf(demande, documentType, filePath);

            // Vérifier que le fichier PDF a été créé
            if (!Files.exists(Paths.get(filePath))) {
                throw new RuntimeException("Le fichier PDF n'a pas été généré correctement: " + filePath);
            }

            long fileSize = Files.size(Paths.get(filePath));
            System.out.println("=== VÉRIFICATION FICHIER ===");
            System.out.println("Fichier créé: " + filePath);
            System.out.println("Taille: " + fileSize + " bytes");
            System.out.println("Existe: " + Files.exists(Paths.get(filePath)));

            // Vérifier l'extension du fichier créé
            String actualExtension = getFileExtension(filePath);
            System.out.println("Extension réelle du fichier: " + actualExtension);
            if (!".pdf".equals(actualExtension)) {
                System.err.println("⚠️ ATTENTION: Le fichier créé n'a pas l'extension .pdf !");
                System.err.println("Extension attendue: .pdf");
                System.err.println("Extension réelle: " + actualExtension);
            }

            // Vérification d'intégrité : s'assurer que le fichier est bien un PDF
            try (FileInputStream fis = new FileInputStream(filePath)) {
                byte[] header = new byte[4];
                fis.read(header);
                String fileSignature = new String(header);
                System.out.println("Signature du fichier: " + fileSignature);

                if (!fileSignature.startsWith("%PDF")) {
                    System.err.println("❌ ERREUR CRITIQUE: Le fichier créé n'est pas un PDF valide !");
                    System.err.println("Signature attendue: %PDF");
                    System.err.println("Signature réelle: " + fileSignature);
                    throw new RuntimeException(
                            "Le fichier généré n'est pas un PDF valide. Signature: " + fileSignature);
                }
                System.out.println("✅ Signature PDF valide confirmée");
            } catch (IOException e) {
                System.err.println("❌ Erreur lors de la vérification de la signature: " + e.getMessage());
                throw new RuntimeException("Impossible de vérifier l'intégrité du PDF", e);
            }

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

            // Vérification finale : s'assurer que le nom de fichier est bien .pdf
            if (!fileName.toLowerCase().endsWith(".pdf")) {
                System.err.println("❌ ERREUR CRITIQUE: Le nom de fichier n'a pas l'extension .pdf !");
                System.err.println("Nom de fichier: " + fileName);
                throw new RuntimeException("Le nom de fichier doit avoir l'extension .pdf");
            }

            GeneratedDocument savedDoc = generatedDocumentRepository.save(generatedDocument);
            System.out.println("=== SAUVEGARDE EN BASE ===");
            System.out.println("Document enregistré avec l'ID: " + savedDoc.getId());
            System.out.println("Nom en base: " + savedDoc.getFileName());
            System.out.println("Chemin en base: " + savedDoc.getFilePath());
            System.out.println("=== FIN GÉNÉRATION PDF ===");

            return savedDoc;

        } catch (Exception e) {
            System.err.println("❌ ERREUR lors de la génération PDF: " + e.getMessage());
            e.printStackTrace();

            // Nettoyer le fichier temporaire s'il existe
            if (filePath != null) {
                try {
                    Path tempPath = Paths.get(filePath + "_temp");
                    if (Files.exists(tempPath)) {
                        Files.deleteIfExists(tempPath);
                    }
                    // Nettoyer aussi le fichier principal s'il est corrompu
                    if (Files.exists(Paths.get(filePath))) {
                        Files.deleteIfExists(tempPath);
                    }
                } catch (Exception cleanupError) {
                    System.err.println("Erreur lors du nettoyage: " + cleanupError.getMessage());
                }
            }

            throw new RuntimeException("Erreur lors de la génération du document PDF: " + e.getMessage(), e);
        }
    }

    /**
     * Génère un PDF simple et robuste sans filigrane
     */
    private void generateSimplePdf(Demande demande, DocumentType documentType, String outputPath)
            throws IOException {

        System.out.println("Début de la génération PDF simple pour: " + outputPath);

        try {
            // Créer le PDF directement avec iText
            createPdfWithData(demande, documentType, outputPath);

            System.out.println("PDF créé avec succès: " + outputPath);

            // Vérifier que le fichier PDF a été créé
            if (!Files.exists(Paths.get(outputPath))) {
                throw new RuntimeException("Le fichier PDF n'a pas été généré correctement: " + outputPath);
            }

            long fileSize = Files.size(Paths.get(outputPath));
            System.out.println("Fichier PDF créé avec succès, taille: " + fileSize + " bytes");

            // Vérification supplémentaire : essayer de lire le PDF pour s'assurer qu'il est
            // valide
            try (PdfReader reader = new PdfReader(outputPath);
                    PdfDocument pdfDoc = new PdfDocument(reader)) {
                int pages = pdfDoc.getNumberOfPages();
                System.out.println("PDF validé - Nombre de pages: " + pages);
            } catch (Exception e) {
                System.err.println("Erreur lors de la validation du PDF: " + e.getMessage());
                throw new IOException("Le PDF généré n'est pas valide: " + e.getMessage(), e);
            }

            // Ajouter le watermark au PDF
            try {
                System.out.println("Ajout du watermark au PDF...");
                byte[] pdfBytes = Files.readAllBytes(Paths.get(outputPath));

                // Générer le texte du watermark personnalisé
                String watermarkText = watermarkService.generateCustomWatermark(
                        documentType != null ? documentType.getLibelle() : "Document",
                        demande.getFirstName() + " " + demande.getLastName());

                // Ajouter le watermark simple (en bas de page)
                byte[] watermarkedPdf = watermarkService.addSimpleWatermarkToPdf(pdfBytes, watermarkText);

                // Remplacer le fichier original par la version avec watermark
                Files.write(Paths.get(outputPath), watermarkedPdf);

                System.out.println("Watermark ajouté avec succès au PDF");
            } catch (Exception watermarkError) {
                System.err.println("⚠️ Erreur lors de l'ajout du watermark: " + watermarkError.getMessage());
                System.err.println("Le PDF sera généré sans watermark");
                // Ne pas faire échouer la génération si le watermark échoue
            }

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

        System.out.println("PDF créé avec succès: " + pdfPath);
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

        System.out.println(
                "Téléchargement du fichier: " + doc.getFilePath() + " (taille: " + Files.size(filePath) + " bytes)");

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