package com.econsulat.service;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.stereotype.Service;

import org.springframework.context.MessageSource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
public class WatermarkService {

    private static final String TIMESTAMP_FORMAT = "dd/MM/yyyy HH:mm:ss";
    /** Angle diagonal du filigrane (radians), effet moins intrusif. */
    private static final double WATERMARK_ANGLE_RAD = -Math.PI / 4;
    private static final float WATERMARK_FONT_SIZE = 12f;

    private final MessageSource messageSource;

    public WatermarkService(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    /**
     * Ajoute un filigrane diagonal au PDF (locale par défaut : français).
     */
    public byte[] addSimpleWatermarkToPdf(byte[] pdfBytes, String customText) throws IOException {
        return addSimpleWatermarkToPdf(pdfBytes, customText, Locale.FRENCH);
    }

    /**
     * Ajoute un filigrane diagonal au document PDF (sous le contenu, angle -45°, i18n).
     * Si customText est null, utilise la clé pdf.watermark.default selon la locale.
     */
    public byte[] addSimpleWatermarkToPdf(byte[] pdfBytes, String customText, Locale locale) throws IOException {
        Locale docLocale = locale != null ? locale : Locale.FRENCH;
        String defaultText = messageSource.getMessage("pdf.watermark.default", null, docLocale);
        String watermarkText = customText != null ? customText : defaultText;

        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(pdfBytes);
             PdfReader reader = new PdfReader(inputStream);
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             PdfWriter writer = new PdfWriter(outputStream);
             PdfDocument pdfDoc = new PdfDocument(reader, writer)) {

            PdfFont font = PdfFontFactory.createFont();
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern(TIMESTAMP_FORMAT));
            String fullWatermark = watermarkText + " - " + timestamp;

            for (int i = 1; i <= pdfDoc.getNumberOfPages(); i++) {
                PdfPage page = pdfDoc.getPage(i);
                Rectangle pageSize = page.getPageSize();
                float w = pageSize.getWidth();
                float h = pageSize.getHeight();

                // Dessiner sous le contenu de la page (effet filigrane)
                PdfCanvas canvas = new PdfCanvas(page.newContentStreamBefore(), page.getResources(), pdfDoc);
                canvas.saveState();
                canvas.setFillColor(ColorConstants.LIGHT_GRAY);

                // Centre de la page puis rotation diagonale (-45°)
                canvas.concatMatrix(1, 0, 0, 1, w / 2, h / 2);
                double c = Math.cos(WATERMARK_ANGLE_RAD);
                double s = Math.sin(WATERMARK_ANGLE_RAD);
                canvas.concatMatrix(c, s, -s, c, 0, 0);

                // Largeur du texte pour centrage (unité 1000 = 1 em)
                float textWidth = font.getWidth(fullWatermark) * WATERMARK_FONT_SIZE / 1000f;
                canvas.beginText();
                canvas.setFontAndSize(font, WATERMARK_FONT_SIZE);
                canvas.moveText(-textWidth / 2, 0);
                canvas.showText(fullWatermark);
                canvas.endText();

                canvas.restoreState();
            }

            pdfDoc.close();
            return outputStream.toByteArray();
        }
    }

    /**
     * Ajoute un watermark simple au document Word (locale par défaut : français).
     */
    public byte[] addWatermarkToWord(byte[] docxBytes, String customText) throws IOException {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(docxBytes);
             XWPFDocument document = new XWPFDocument(inputStream);
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            String watermarkText = customText != null ? customText : messageSource.getMessage("pdf.watermark.default", null, Locale.FRENCH);
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
