package com.econsulat.service;

import com.econsulat.dto.BilanReportDTO;
import com.econsulat.dto.BilanReportRowDTO;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Génération du rapport de bilan comptable au format PDF.
 */
@Service
public class BilanReportPdfService {

    private static final float FONT_SIZE_TITLE = 16f;
    private static final float FONT_SIZE_HEADER = 10f;
    private static final float FONT_SIZE_BODY = 9f;
    private static final DeviceRgb COLOR_HEADER_BG = new DeviceRgb(0x15, 0x65, 0xC0);
    private static final DeviceRgb COLOR_BORDER = new DeviceRgb(0xE0, 0xE0, 0xE0);

    private final MessageSource messageSource;

    public BilanReportPdfService(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public byte[] generatePdf(BilanReportDTO report, Locale locale) throws IOException {
        Locale loc = locale != null ? locale : Locale.FRENCH;
        String title = messageSource.getMessage("report.bilan.title", null, "Bilan comptable - Demandes approuvées", loc);
        String colPeriod = messageSource.getMessage("report.bilan.col.period", null, "Période", loc);
        String colDocumentType = messageSource.getMessage("report.bilan.col.documentType", null, "Type de document", loc);
        String colCount = messageSource.getMessage("report.bilan.col.count", null, "Nombre", loc);
        String colAmount = messageSource.getMessage("report.bilan.col.amount", null, "Montant (EUR)", loc);
        String totalLabel = messageSource.getMessage("report.bilan.total", null, "Total", loc);
        String generatedOn = messageSource.getMessage("report.bilan.generatedOn", null, "Généré le", loc);
        String groupByLabel = messageSource.getMessage("report.bilan.groupBy." + report.getGroupBy(), null, report.getGroupBy(), loc);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             PdfWriter writer = new PdfWriter(baos);
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf, PageSize.A4)) {

            document.setMargins(PdfStyleConfig.MARGIN_TOP_BOTTOM, PdfStyleConfig.MARGIN_LEFT_RIGHT,
                    PdfStyleConfig.MARGIN_TOP_BOTTOM, PdfStyleConfig.MARGIN_LEFT_RIGHT);

            document.add(new Paragraph(title).setFontSize(FONT_SIZE_TITLE).setBold().setFontColor(PdfStyleConfig.COLOR_TITLE));
            document.add(new Paragraph(groupByLabel).setFontSize(PdfStyleConfig.FONT_SIZE_META).setFontColor(PdfStyleConfig.COLOR_META));
            if (report.getDateFrom() != null || report.getDateTo() != null) {
                String fromStr = report.getDateFrom() != null ? report.getDateFrom().format(DateTimeFormatter.ISO_LOCAL_DATE) : "—";
                String toStr = report.getDateTo() != null ? report.getDateTo().format(DateTimeFormatter.ISO_LOCAL_DATE) : "—";
                document.add(new Paragraph(fromStr + " — " + toStr).setFontSize(PdfStyleConfig.FONT_SIZE_META).setFontColor(PdfStyleConfig.COLOR_META));
            }
            document.add(new Paragraph(" "));

            UnitValue[] colWidths = {UnitValue.createPercentValue(20f), UnitValue.createPercentValue(40f), UnitValue.createPercentValue(15f), UnitValue.createPercentValue(25f)};
            Table table = new Table(colWidths);
            table.addHeaderCell(new Cell().add(new Paragraph(colPeriod).setFontSize(FONT_SIZE_HEADER).setBold().setFontColor(ColorConstants.WHITE))
                    .setBackgroundColor(COLOR_HEADER_BG).setBorder(new SolidBorder(COLOR_BORDER, 0.5f)).setPadding(6));
            table.addHeaderCell(new Cell().add(new Paragraph(colDocumentType).setFontSize(FONT_SIZE_HEADER).setBold().setFontColor(ColorConstants.WHITE))
                    .setBackgroundColor(COLOR_HEADER_BG).setBorder(new SolidBorder(COLOR_BORDER, 0.5f)).setPadding(6));
            table.addHeaderCell(new Cell().add(new Paragraph(colCount).setFontSize(FONT_SIZE_HEADER).setBold().setFontColor(ColorConstants.WHITE))
                    .setBackgroundColor(COLOR_HEADER_BG).setBorder(new SolidBorder(COLOR_BORDER, 0.5f)).setPadding(6));
            table.addHeaderCell(new Cell().add(new Paragraph(colAmount).setFontSize(FONT_SIZE_HEADER).setBold().setFontColor(ColorConstants.WHITE))
                    .setBackgroundColor(COLOR_HEADER_BG).setBorder(new SolidBorder(COLOR_BORDER, 0.5f)).setPadding(6));

            for (BilanReportRowDTO row : report.getRows()) {
                table.addCell(new Cell().add(new Paragraph(row.getPeriodLabel()).setFontSize(FONT_SIZE_BODY))
                        .setBorder(new SolidBorder(COLOR_BORDER, 0.5f)).setPadding(4));
                table.addCell(new Cell().add(new Paragraph(row.getDocumentTypeLibelle() != null ? row.getDocumentTypeLibelle() : "").setFontSize(FONT_SIZE_BODY))
                        .setBorder(new SolidBorder(COLOR_BORDER, 0.5f)).setPadding(4));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(row.getCount())).setFontSize(FONT_SIZE_BODY))
                        .setBorder(new SolidBorder(COLOR_BORDER, 0.5f)).setPadding(4));
                table.addCell(new Cell().add(new Paragraph(row.getAmountEuros() != null ? row.getAmountEuros() : "0.00").setFontSize(FONT_SIZE_BODY))
                        .setBorder(new SolidBorder(COLOR_BORDER, 0.5f)).setPadding(4));
            }

            table.addCell(new Cell(1, 2).add(new Paragraph(totalLabel).setFontSize(FONT_SIZE_BODY).setBold())
                    .setBorder(new SolidBorder(COLOR_BORDER, 0.5f)).setPadding(6));
            table.addCell(new Cell().add(new Paragraph("").setFontSize(FONT_SIZE_BODY)).setBorder(new SolidBorder(COLOR_BORDER, 0.5f)).setPadding(6));
            table.addCell(new Cell().add(new Paragraph(report.getTotalAmountEuros() != null ? report.getTotalAmountEuros() : "0.00").setFontSize(FONT_SIZE_BODY).setBold())
                    .setBorder(new SolidBorder(COLOR_BORDER, 0.5f)).setPadding(6));

            document.add(table);
            document.add(new Paragraph(" "));
            if (report.getGeneratedAt() != null) {
                String genStr = generatedOn + " " + report.getGeneratedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm", loc));
                document.add(new Paragraph(genStr).setFontSize(PdfStyleConfig.FONT_SIZE_FOOTER).setFontColor(PdfStyleConfig.COLOR_META));
            }
            document.close();
            return baos.toByteArray();
        }
    }
}
