import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;

public class CreateTemplate {

    public static void main(String[] args) {
        System.out.println("🛂 Création d'un template Word simple");
        System.out.println("========================================");

        try {
            // Créer un nouveau document Word
            XWPFDocument document = new XWPFDocument();

            // Titre principal
            XWPFParagraph title = document.createParagraph();
            title.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun titleRun = title.createRun();
            titleRun.setText("FORMULARIO DE PEDIDO DE PASSAPORTE");
            titleRun.setBold(true);
            titleRun.setFontSize(16);

            // Espace
            document.createParagraph();

            // Section données personnelles
            XWPFParagraph section1 = document.createParagraph();
            XWPFRun section1Run = section1.createRun();
            section1Run.setText("Dados pessoais:");
            section1Run.setBold(true);

            // Tableau pour les données
            XWPFTable table = document.createTable(4, 2);

            // Remplir le tableau
            String[][] data = {
                    { "Nome:", "{{Prénom}}" },
                    { "Apelido:", "{{Nom de famille}}" },
                    { "Data de nascimento:", "{{Date de naissance}}" },
                    { "Local de nascimento:", "{{Local de nascimento}}" }
            };

            for (int i = 0; i < data.length; i++) {
                table.getRow(i).getCell(0).setText(data[i][0]);
                table.getRow(i).getCell(1).setText(data[i][1]);
            }

            // Espace
            document.createParagraph();

            // Section informations supplémentaires
            XWPFParagraph section2 = document.createParagraph();
            XWPFRun section2Run = section2.createRun();
            section2Run.setText("Informações adicionais:");
            section2Run.setBold(true);

            document.createParagraph().createRun().setText("Data de pedido: _________________");
            document.createParagraph().createRun().setText("Assinatura: _____________________");

            // Espace
            document.createParagraph();

            // Section observations
            XWPFParagraph section3 = document.createParagraph();
            XWPFRun section3Run = section3.createRun();
            section3Run.setText("Observações:");
            section3Run.setBold(true);

            for (int i = 0; i < 3; i++) {
                document.createParagraph().createRun().setText("_________________________________");
            }

            // Espace
            document.createParagraph();

            // Note de bas de page
            XWPFParagraph note = document.createParagraph();
            note.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun noteRun = note.createRun();
            noteRun.setText("Este documento foi gerado automaticamente pelo sistema eConsulat.");
            noteRun.setItalic(true);

            // Sauvegarder le template
            Path templatePath = Paths.get("backend/src/main/resources/FORMULARIO DE PEDIDO DE PASSAPORTE.docx");
            Files.createDirectories(templatePath.getParent());

            try (FileOutputStream outputStream = new FileOutputStream(templatePath.toFile())) {
                document.write(outputStream);
            }

            document.close();

            System.out.println("✅ Template créé: " + templatePath);
            System.out.println("📋 Contenu:");
            System.out.println("- Titre: FORMULARIO DE PEDIDO DE PASSAPORTE");
            System.out.println(
                    "- Placeholders: {{Prénom}}, {{Nom de famille}}, {{Date de naissance}}, {{Local de nascimento}}");
            System.out.println("- Format: Document Word (.docx)");

        } catch (Exception e) {
            System.err.println("❌ Erreur: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}