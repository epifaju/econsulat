package com.econsulat.service;

import com.econsulat.dto.BilanReportDTO;
import com.econsulat.dto.BilanReportRowDTO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Locale;

/**
 * Génération du rapport de bilan comptable au format Excel (.xlsx).
 */
@Service
public class BilanReportExcelService {

    private final MessageSource messageSource;

    public BilanReportExcelService(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public byte[] generateExcel(BilanReportDTO report, Locale locale) throws IOException {
        Locale loc = locale != null ? locale : Locale.FRENCH;
        String colPeriod = messageSource.getMessage("report.bilan.col.period", null, "Période", loc);
        String colDocumentType = messageSource.getMessage("report.bilan.col.documentType", null, "Type de document", loc);
        String colCount = messageSource.getMessage("report.bilan.col.count", null, "Nombre", loc);
        String colAmount = messageSource.getMessage("report.bilan.col.amount", null, "Montant (EUR)", loc);
        String totalLabel = messageSource.getMessage("report.bilan.total", null, "Total", loc);

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Bilan");
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle totalStyle = createBoldStyle(workbook);

            int rowNum = 0;

            Row headerRow = sheet.createRow(rowNum++);
            headerRow.createCell(0).setCellValue(colPeriod);
            headerRow.createCell(1).setCellValue(colDocumentType);
            headerRow.createCell(2).setCellValue(colCount);
            headerRow.createCell(3).setCellValue(colAmount);
            for (Cell cell : headerRow) {
                cell.setCellStyle(headerStyle);
            }

            for (BilanReportRowDTO row : report.getRows()) {
                Row dataRow = sheet.createRow(rowNum++);
                dataRow.createCell(0).setCellValue(row.getPeriodLabel() != null ? row.getPeriodLabel() : "");
                dataRow.createCell(1).setCellValue(row.getDocumentTypeLibelle() != null ? row.getDocumentTypeLibelle() : "");
                dataRow.createCell(2).setCellValue(row.getCount());
                dataRow.createCell(3).setCellValue(row.getAmountEuros() != null ? row.getAmountEuros() : "0.00");
            }

            Row totalRow = sheet.createRow(rowNum);
            totalRow.createCell(0).setCellValue(totalLabel);
            totalRow.createCell(1).setCellValue("");
            totalRow.createCell(2).setCellValue("");
            totalRow.createCell(3).setCellValue(report.getTotalAmountEuros() != null ? report.getTotalAmountEuros() : "0.00");
            for (Cell cell : totalRow) {
                cell.setCellStyle(totalStyle);
            }

            for (int i = 0; i < 4; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(baos);
            return baos.toByteArray();
        }
    }

    private static CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        return style;
    }

    private static CellStyle createBoldStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        return style;
    }
}
