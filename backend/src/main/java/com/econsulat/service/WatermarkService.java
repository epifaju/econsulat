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
import com.itextpdf.kernel.pdf.extgstate.PdfExtGState;
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
    /** Taille du filigrane (points) pour une bonne visibilité sur toutes les pages. */
    private static final float WATERMARK_FONT_SIZE = 22f;
    /** Opacité du filigrane (0–1) en premier plan : visible sur tout le document tout en restant lisible. */
    private static final float WATERMARK_OPACITY = 0.35f;

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

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern(TIMESTAMP_FORMAT));
            String fullWatermark = watermarkText + " - " + timestamp;

            // Zone de contenu = page moins les marges (alignée sur PdfStyleConfig / document)
            float marginL = PdfStyleConfig.MARGIN_LEFT_RIGHT;
            float marginB = PdfStyleConfig.MARGIN_TOP_BOTTOM;

            for (int i = 1; i <= pdfDoc.getNumberOfPages(); i++) {
                PdfPage page = pdfDoc.getPage(i);
                Rectangle pageSize = page.getPageSize();
                float w = pageSize.getWidth();
                float h = pageSize.getHeight();

                float contentW = w - 2 * marginL;
                float contentH = h - 2 * marginB;
                Rectangle contentArea = new Rectangle(marginL, marginB, contentW, contentH);

                PdfFont font = PdfFontFactory.createFont();
                // Premier plan (newContentStreamAfter) + opacité pour rester lisible
                PdfCanvas canvas = new PdfCanvas(page.newContentStreamAfter(), page.getResources(), pdfDoc);
                canvas.saveState();

                // Option A : limiter le filigrane à la zone de contenu (mêmes marges que le document)
                canvas.rectangle(contentArea).clip().endPath();

                PdfExtGState extGState = new PdfExtGState().setFillOpacity(WATERMARK_OPACITY);
                canvas.setExtGState(extGState);
                canvas.setFillColor(ColorConstants.LIGHT_GRAY);

                canvas.concatMatrix(1, 0, 0, 1, w / 2, h / 2);
                double c = Math.cos(WATERMARK_ANGLE_RAD);
                double s = Math.sin(WATERMARK_ANGLE_RAD);
                canvas.concatMatrix(c, s, -s, c, 0, 0);

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
