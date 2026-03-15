package com.econsulat.service;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;

import java.io.IOException;

/**
 * Dessine le filigrane diagonal sur chaque page du PDF lors de la génération (sous le contenu, -45°).
 * Garantit que le filigrane figure sur toutes les pages sans post-traitement.
 */
public class PdfWatermarkHandler implements IEventHandler {

    private static final double WATERMARK_ANGLE_RAD = -Math.PI / 4;
    private static final float WATERMARK_FONT_SIZE = 12f;

    private final String fullWatermarkText;
    private final PdfDocument pdfDocument;
    private final PdfFont font;

    public PdfWatermarkHandler(String fullWatermarkText, PdfDocument pdfDocument) {
        this.fullWatermarkText = fullWatermarkText != null ? fullWatermarkText : "eConsulat";
        this.pdfDocument = pdfDocument;
        try {
            this.font = PdfFontFactory.createFont();
        } catch (IOException e) {
            throw new RuntimeException("Impossible de créer la police pour le filigrane", e);
        }
    }

    @Override
    public void handleEvent(Event event) {
        PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
        PdfPage page = docEvent.getPage();
        Rectangle pageSize = page.getPageSize();
        float w = pageSize.getWidth();
        float h = pageSize.getHeight();

        PdfCanvas canvas = new PdfCanvas(page.newContentStreamBefore(), page.getResources(), pdfDocument);
        canvas.saveState();
        canvas.setFillColor(ColorConstants.LIGHT_GRAY);

        canvas.concatMatrix(1, 0, 0, 1, w / 2, h / 2);
        double c = Math.cos(WATERMARK_ANGLE_RAD);
        double s = Math.sin(WATERMARK_ANGLE_RAD);
        canvas.concatMatrix(c, s, -s, c, 0, 0);

        float textWidth = font.getWidth(fullWatermarkText) * WATERMARK_FONT_SIZE / 1000f;
        canvas.beginText();
        canvas.setFontAndSize(font, WATERMARK_FONT_SIZE);
        canvas.moveText(-textWidth / 2, 0);
        canvas.showText(fullWatermarkText);
        canvas.endText();

        canvas.restoreState();
    }
}
