package com.econsulat.service;

import com.econsulat.model.Citizen;
import com.econsulat.storage.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class PassportDocumentService {

    private static final String TEMPLATE_PATH = "FORMULARIO DE PEDIDO DE PASSAPORTE.docx";
    private final StorageService storageService;

    /**
     * Génère un document de passeport pour un citoyen
     */
    public String generatePassportDocument(Citizen citizen) throws IOException {
        log.info("Génération du document de passeport pour le citoyen: {}", citizen.getId());

        ClassPathResource resource = new ClassPathResource(TEMPLATE_PATH);
        if (!resource.exists()) {
            throw new IOException("Template de passeport non trouvé: " + TEMPLATE_PATH);
        }

        String filename = String.format("passport_%d_%s.docx",
                citizen.getId(), UUID.randomUUID().toString());

        try (FileInputStream fis = new FileInputStream(resource.getFile());
                XWPFDocument document = new XWPFDocument(fis);
                OutputStream out = storageService.openPassportOutputStream(filename)) {

            for (XWPFParagraph paragraph : document.getParagraphs()) {
                replacePlaceholdersInParagraph(paragraph, citizen);
            }
            document.getTables().forEach(
                    table -> table.getRows().forEach(row -> row.getTableCells().forEach(cell -> cell.getParagraphs()
                            .forEach(paragraph -> replacePlaceholdersInParagraph(paragraph, citizen)))));

            document.write(out);
            log.info("Document de passeport généré: {}", filename);
            return filename;
        } catch (Exception e) {
            log.error("Erreur lors de la génération du document de passeport", e);
            throw new IOException("Erreur lors de la génération du document: " + e.getMessage());
        }
    }

    /**
     * Remplace les placeholders dans un paragraphe avec mise en forme
     */
    private void replacePlaceholdersInParagraph(XWPFParagraph paragraph, Citizen citizen) {
        String originalText = paragraph.getText();

        // Vérifier s'il y a des placeholders à remplacer
        if (!containsPlaceholders(originalText)) {
            return;
        }

        // Supprimer tous les runs existants
        List<XWPFRun> runs = paragraph.getRuns();
        for (int i = runs.size() - 1; i >= 0; i--) {
            paragraph.removeRun(i);
        }

        // Créer le nouveau texte avec mise en forme
        String processedText = originalText;

        // Remplacer les placeholders avec mise en forme
        processedText = replacePlaceholderWithBold(processedText, "{{Prénom}}",
                citizen.getFirstName() != null ? citizen.getFirstName() : "");
        processedText = replacePlaceholderWithBold(processedText, "{{Nom de famille}}",
                citizen.getLastName() != null ? citizen.getLastName() : "");
        processedText = replacePlaceholderWithBold(processedText, "{{Date de naissance}}",
                citizen.getBirthDate() != null
                        ? citizen.getBirthDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                        : "");
        processedText = replacePlaceholderWithBold(processedText, "{{Lieu de naissance}}",
                citizen.getBirthPlace() != null ? citizen.getBirthPlace() : "");
        processedText = replacePlaceholderWithBold(processedText, "{{Local de nascimento}}",
                citizen.getBirthPlace() != null ? citizen.getBirthPlace() : "");
        processedText = replacePlaceholderWithBold(processedText, "{{Nome}}",
                citizen.getFirstName() != null ? citizen.getFirstName() : "");
        processedText = replacePlaceholderWithBold(processedText, "{{Apelido}}",
                citizen.getLastName() != null ? citizen.getLastName() : "");
        processedText = replacePlaceholderWithBold(processedText, "{{Data de nascimento}}",
                citizen.getBirthDate() != null
                        ? citizen.getBirthDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                        : "");

        // Créer les runs avec mise en forme
        createFormattedRuns(paragraph, processedText);
    }

    /**
     * Vérifie si le texte contient des placeholders
     */
    private boolean containsPlaceholders(String text) {
        return text.contains("{{Prénom}}") || text.contains("{{Nom de famille}}") ||
                text.contains("{{Date de naissance}}") || text.contains("{{Lieu de naissance}}") ||
                text.contains("{{Local de nascimento}}") || text.contains("{{Nome}}") ||
                text.contains("{{Apelido}}") || text.contains("{{Data de nascimento}}");
    }

    /**
     * Remplace un placeholder par sa valeur en gras
     */
    private String replacePlaceholderWithBold(String text, String placeholder, String value) {
        if (text.contains(placeholder)) {
            return text.replace(placeholder, "**" + value + "**");
        }
        return text;
    }

    /**
     * Crée les runs avec mise en forme (gras pour les données)
     */
    private void createFormattedRuns(XWPFParagraph paragraph, String text) {
        // Pattern pour détecter le texte en gras (**texte**)
        Pattern boldPattern = Pattern.compile("\\*\\*(.*?)\\*\\*");
        Matcher matcher = boldPattern.matcher(text);

        int lastEnd = 0;

        while (matcher.find()) {
            // Ajouter le texte avant le placeholder
            if (matcher.start() > lastEnd) {
                String beforeText = text.substring(lastEnd, matcher.start());
                if (!beforeText.isEmpty()) {
                    XWPFRun run = paragraph.createRun();
                    run.setText(beforeText);
                }
            }

            // Ajouter le texte en gras (données du citoyen)
            String boldText = matcher.group(1);
            XWPFRun boldRun = paragraph.createRun();
            boldRun.setText(boldText);
            boldRun.setBold(true);

            lastEnd = matcher.end();
        }

        // Ajouter le texte restant après le dernier placeholder
        if (lastEnd < text.length()) {
            String remainingText = text.substring(lastEnd);
            if (!remainingText.isEmpty()) {
                XWPFRun run = paragraph.createRun();
                run.setText(remainingText);
            }
        }
    }

    /**
     * Récupère un document généré
     */
    public byte[] getDocument(String filename) throws IOException {
        return storageService.readPassport(filename);
    }

    /**
     * Supprime un document généré
     */
    public void deleteDocument(String filename) throws IOException {
        if (storageService.passportExists(filename)) {
            storageService.deletePassport(filename);
            log.info("Document supprimé: {}", filename);
        }
    }
}