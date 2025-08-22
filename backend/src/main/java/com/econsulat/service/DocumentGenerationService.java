package com.econsulat.service;

import com.econsulat.model.Demande;
import com.econsulat.model.DocumentType;
import com.econsulat.model.GeneratedDocument;
import com.econsulat.model.User;
import com.econsulat.repository.DemandeRepository;
import com.econsulat.repository.DocumentTypeRepository;
import com.econsulat.repository.GeneratedDocumentRepository;
import com.econsulat.service.WatermarkService;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
public class DocumentGenerationService {

    @Autowired
    private DemandeRepository demandeRepository;

    @Autowired
    private DocumentTypeRepository documentTypeRepository;

    @Autowired
    private GeneratedDocumentRepository generatedDocumentRepository;

    @Autowired
    private WatermarkService watermarkService;

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    @Value("${app.documents.dir:documents}")
    private String documentsDir;

    public GeneratedDocument generateDocument(Long demandeId, Long documentTypeId, User currentUser) {
        Demande demande = demandeRepository.findById(demandeId)
                .orElseThrow(() -> new RuntimeException("Demande non trouvée"));

        // Récupérer le type de document
        DocumentType documentType = documentTypeRepository.findById(documentTypeId)
                .orElseThrow(() -> new RuntimeException("Type de document non trouvé"));

        // Validation des données de la demande
        validateDemandeData(demande);

        // Vérifier si le document a déjà été généré pour ce type
        GeneratedDocument existingDoc = generatedDocumentRepository
                .findWordDocumentByDemandeAndType(demandeId, documentTypeId)
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
            String fileName = generateFileName(demande, documentType);
            String filePath = documentsPath.resolve(fileName).toString();

            // Générer le document
            generateDocumentFromTemplate(demande, filePath);

            // Créer l'enregistrement en base avec le DocumentType
            GeneratedDocument generatedDocument = new GeneratedDocument();
            generatedDocument.setDemande(demande);
            generatedDocument.setDocumentType(documentType);
            generatedDocument.setFileName(fileName);
            generatedDocument.setFilePath(filePath);
            generatedDocument.setCreatedBy(currentUser);
            generatedDocument.setStatus("GENERATED");
            generatedDocument.setExpiresAt(LocalDateTime.now().plusDays(30));

            return generatedDocumentRepository.save(generatedDocument);

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération du document: " + e.getMessage(), e);
        }
    }

    private void validateDemandeData(Demande demande) {
        if (demande.getFirstName() == null || demande.getFirstName().trim().isEmpty()) {
            throw new RuntimeException("Le prénom du demandeur est requis");
        }
        if (demande.getLastName() == null || demande.getLastName().trim().isEmpty()) {
            throw new RuntimeException("Le nom de famille du demandeur est requis");
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

    private String generateFileName(Demande demande, DocumentType documentType) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String userInfo = demande.getFirstName() + "_" + demande.getLastName();
        String docType = documentType.getLibelle().replaceAll("[^a-zA-Z0-9]", "_");

        // Ajouter un préfixe "DOCX_" pour éviter les conflits avec PdfDocumentService
        return String.format("DOCX_%s_%s_%s_%s.docx",
                docType, userInfo, timestamp, UUID.randomUUID().toString().substring(0, 8));
    }

    private void generateDocumentFromTemplate(Demande demande, String outputPath)
            throws IOException {

        // Utiliser un template par défaut
        String templatePath = "templates/default_template.docx";

        try (InputStream is = new FileInputStream(templatePath);
                XWPFDocument document = new XWPFDocument(is)) {

            // Remplacer les placeholders dans le document
            replacePlaceholders(document, demande);

            // Sauvegarder le document
            try (FileOutputStream out = new FileOutputStream(outputPath)) {
                document.write(out);
            }

            // Ajouter le watermark au document Word
            try {
                System.out.println("Ajout du watermark au document Word...");
                byte[] docxBytes = Files.readAllBytes(Paths.get(outputPath));

                // Générer le texte du watermark personnalisé
                String watermarkText = watermarkService.generateCustomWatermark(
                        "Document Word",
                        demande.getFirstName() + " " + demande.getLastName());

                // Ajouter le watermark en en-tête
                byte[] watermarkedDocx = watermarkService.addWatermarkToWord(docxBytes, watermarkText);

                // Remplacer le fichier original par la version avec watermark
                Files.write(Paths.get(outputPath), watermarkedDocx);

                System.out.println("Watermark ajouté avec succès au document Word");
            } catch (Exception watermarkError) {
                System.err.println("⚠️ Erreur lors de l'ajout du watermark: " + watermarkError.getMessage());
                System.err.println("Le document Word sera généré sans watermark");
                // Ne pas faire échouer la génération si le watermark échoue
            }

        } catch (FileNotFoundException e) {
            // Si le template n'existe pas, créer un document simple
            createSimpleDocument(demande, outputPath);
        }
    }

    private void replacePlaceholders(XWPFDocument document, Demande demande) {
        Map<String, String> placeholders = createPlaceholdersMap(demande);

        for (XWPFParagraph paragraph : document.getParagraphs()) {
            String text = paragraph.getText();
            for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                text = text.replace("{{" + entry.getKey() + "}}", entry.getValue());
            }
            if (!text.equals(paragraph.getText())) {
                // Supprimer tous les runs existants
                for (int i = paragraph.getRuns().size() - 1; i >= 0; i--) {
                    paragraph.removeRun(i);
                }
                // Ajouter le nouveau texte
                XWPFRun run = paragraph.createRun();
                run.setText(text);
            }
        }
    }

    private Map<String, String> createPlaceholdersMap(Demande demande) {
        Map<String, String> placeholders = new HashMap<>();

        // Informations personnelles
        placeholders.put("FIRST_NAME", demande.getFirstName());
        placeholders.put("LAST_NAME", demande.getLastName());
        placeholders.put("BIRTH_DATE", demande.getBirthDate().toString());
        placeholders.put("BIRTH_PLACE", demande.getBirthPlace());
        placeholders.put("BIRTH_COUNTRY", demande.getBirthCountry().getLibelle());

        // Adresse
        placeholders.put("ADDRESS",
                demande.getAdresse().getStreetNumber() + " " + demande.getAdresse().getStreetName());
        placeholders.put("CITY", demande.getAdresse().getCity());
        placeholders.put("POSTAL_CODE", demande.getAdresse().getPostalCode());
        placeholders.put("COUNTRY", demande.getAdresse().getCountry().getLibelle());

        // Filiation - Père
        placeholders.put("FATHER_FIRST_NAME", demande.getFatherFirstName());
        placeholders.put("FATHER_LAST_NAME", demande.getFatherLastName());
        placeholders.put("FATHER_BIRTH_DATE", demande.getFatherBirthDate().toString());
        placeholders.put("FATHER_BIRTH_PLACE", demande.getFatherBirthPlace());
        placeholders.put("FATHER_BIRTH_COUNTRY", demande.getFatherBirthCountry().getLibelle());

        // Filiation - Mère
        placeholders.put("MOTHER_FIRST_NAME", demande.getMotherFirstName());
        placeholders.put("MOTHER_LAST_NAME", demande.getMotherLastName());
        placeholders.put("MOTHER_BIRTH_DATE", demande.getMotherBirthDate().toString());
        placeholders.put("MOTHER_BIRTH_PLACE", demande.getMotherBirthPlace());
        placeholders.put("MOTHER_BIRTH_COUNTRY", demande.getMotherBirthCountry().getLibelle());

        // Date de génération
        placeholders.put("GENERATION_DATE", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        placeholders.put("GENERATION_TIME", LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));

        return placeholders;
    }

    private void createSimpleDocument(Demande demande, String outputPath)
            throws IOException {

        try (XWPFDocument document = new XWPFDocument()) {

            // Titre
            XWPFParagraph title = document.createParagraph();
            XWPFRun titleRun = title.createRun();
            titleRun.setText(demande.getDocumentType().name());
            titleRun.setBold(true);
            titleRun.setFontSize(16);

            // Informations du demandeur
            XWPFParagraph info = document.createParagraph();
            XWPFRun infoRun = info.createRun();
            infoRun.setText("Demandeur: " + demande.getFirstName() + " " + demande.getLastName());
            infoRun.setFontSize(12);

            // Date de naissance
            XWPFParagraph birth = document.createParagraph();
            XWPFRun birthRun = birth.createRun();
            birthRun.setText("Date de naissance: " + demande.getBirthDate());
            birthRun.setFontSize(12);

            // Lieu de naissance
            XWPFParagraph birthPlace = document.createParagraph();
            XWPFRun birthPlaceRun = birthPlace.createRun();
            birthPlaceRun.setText("Lieu de naissance: " + demande.getBirthPlace() + ", " +
                    demande.getBirthCountry().getLibelle());
            birthPlaceRun.setFontSize(12);

            // Sauvegarder
            try (FileOutputStream out = new FileOutputStream(outputPath)) {
                document.write(out);
            }
        }
    }

    public byte[] downloadDocument(Long documentId) throws IOException {
        GeneratedDocument doc = generatedDocumentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document non trouvé"));

        Path filePath = Paths.get(doc.getFilePath());
        if (!Files.exists(filePath)) {
            throw new RuntimeException("Fichier non trouvé");
        }

        // Marquer comme téléchargé
        doc.setStatus("DOWNLOADED");
        doc.setDownloadedAt(LocalDateTime.now());
        generatedDocumentRepository.save(doc);

        return Files.readAllBytes(filePath);
    }
}