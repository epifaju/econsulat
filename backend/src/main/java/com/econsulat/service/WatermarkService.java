package com.econsulat.service;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.element.Paragraph;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class WatermarkService {

    private static final String WATERMARK_TEXT = "eConsulat - Document officiel";
    private static final String TIMESTAMP_FORMAT = "dd/MM/yyyy HH:mm:ss";

    /**
     * Ajoute un watermark simple au document PDF (version simplifiée)
     */
    public byte[] addSimpleWatermarkToPdf(byte[] pdfBytes, String customText) throws IOException {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(pdfBytes);
             PdfReader reader = new PdfReader(inputStream);
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             PdfWriter writer = new PdfWriter(outputStream);
             PdfDocument pdfDoc = new PdfDocument(reader, writer)) {

            // Créer la police pour le watermark
            PdfFont font = PdfFontFactory.createFont("Helvetica");

            // Parcourir toutes les pages
            for (int i = 1; i <= pdfDoc.getNumberOfPages(); i++) {
                PdfPage page = pdfDoc.getPage(i);
                Rectangle pageSize = page.getPageSize();

                // Créer le canvas pour dessiner sur la page
                Canvas canvas = new Canvas(page, pageSize);

                // Créer le texte du watermark
                String watermarkText = customText != null ? customText : WATERMARK_TEXT;
                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern(TIMESTAMP_FORMAT));
                String fullWatermark = watermarkText + " - " + timestamp;

                // Créer le paragraphe du watermark
                Paragraph watermark = new Paragraph(fullWatermark)
                        .setFont(font)
                        .setFontSize(14)
                        .setFontColor(ColorConstants.LIGHT_GRAY);

                // Positionner le watermark en bas de page
                float x = pageSize.getWidth() / 2;
                float y = 30; // 30 points du bas

                // Ajouter le watermark
                canvas.showTextAligned(watermark, x, y, com.itextpdf.layout.properties.TextAlignment.CENTER);

                canvas.close();
            }

            pdfDoc.close();
            return outputStream.toByteArray();
        }
    }

    /**
     * Ajoute un watermark simple au document Word
     */
    public byte[] addWatermarkToWord(byte[] docxBytes, String customText) throws IOException {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(docxBytes);
             XWPFDocument document = new XWPFDocument(inputStream);
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            // Créer le texte du watermark
            String watermarkText = customText != null ? customText : WATERMARK_TEXT;
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern(TIMESTAMP_FORMAT));
            String fullWatermark = watermarkText + " - " + timestamp;

            // Ajouter le watermark comme premier paragraphe
            XWPFParagraph watermarkParagraph = document.createParagraph();
            watermarkParagraph.setAlignment(org.apache.poi.xwpf.usermodel.ParagraphAlignment.CENTER);

            // Créer le run avec le style du watermark
            XWPFRun watermarkRun = watermarkParagraph.createRun();
            watermarkRun.setText(fullWatermark);
            watermarkRun.setFontSize(12);
            watermarkRun.setColor("808080"); // Gris clair
            watermarkRun.setBold(true);

            // Sauvegarder le document
            document.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    /**
     * Génère un texte de watermark personnalisé
     */
    public String generateCustomWatermark(String documentType, String userName) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern(TIMESTAMP_FORMAT));
        return String.format("eConsulat - %s - %s - %s", documentType, userName, timestamp);
    }
}
