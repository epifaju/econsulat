package com.econsulat.service;

import com.econsulat.model.DocumentType;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;
import org.springframework.context.MessageSource;

import java.util.Locale;

/**
 * Dessine l'en-tête et le pied de page sur chaque page du PDF (eConsulat), avec i18n.
 */
public class PdfHeaderFooterHandler implements IEventHandler {

    private final DocumentType documentType;
    private final PdfDocument pdfDocument;
    private final MessageSource messageSource;
    private final Locale locale;

    public PdfHeaderFooterHandler(DocumentType documentType, PdfDocument pdfDocument,
                                 MessageSource messageSource, Locale locale) {
        this.documentType = documentType;
        this.pdfDocument = pdfDocument;
        this.messageSource = messageSource;
        this.locale = locale != null ? locale : Locale.FRENCH;
    }

    @Override
    public void handleEvent(Event event) {
        PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
        PdfPage page = docEvent.getPage();
        Rectangle pageSize = page.getPageSize();
        float width = pageSize.getWidth();
        float height = pageSize.getHeight();

        String typeLibelle = documentType != null && documentType.getLibelle() != null
                ? documentType.getLibelle()
                : messageSource.getMessage("pdf.typeNotSpecified", null, locale);
        String headerText = messageSource.getMessage("pdf.header", new Object[]{typeLibelle}, locale);

        try (Canvas canvas = new Canvas(page, pageSize)) {
            Paragraph header = new Paragraph(headerText)
                    .setFontSize(PdfStyleConfig.FONT_SIZE_HEADER)
                    .setFontColor(PdfStyleConfig.COLOR_SECTION_HEADER);
            float headerY = height - PdfStyleConfig.HEADER_TOP_OFFSET;
            canvas.showTextAligned(header, width / 2, headerY, TextAlignment.CENTER);

            int pageNumber = pdfDocument.getPageNumber(page);
            String footerText = messageSource.getMessage("pdf.footer", new Object[]{pageNumber}, locale);
            Paragraph footer = new Paragraph(footerText)
                    .setFontSize(PdfStyleConfig.FONT_SIZE_FOOTER)
                    .setFontColor(PdfStyleConfig.COLOR_META);
            float footerY = PdfStyleConfig.FOOTER_BOTTOM_OFFSET;
            canvas.showTextAligned(footer, width / 2, footerY, TextAlignment.CENTER);
        }
    }
}
