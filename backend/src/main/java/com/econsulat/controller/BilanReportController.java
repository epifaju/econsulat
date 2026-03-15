package com.econsulat.controller;

import com.econsulat.dto.BilanReportDTO;
import com.econsulat.service.BilanReportExcelService;
import com.econsulat.service.BilanReportPdfService;
import com.econsulat.service.BilanReportService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Rapport de bilan comptable (demandes approuvées, paiements payés).
 * Réservé aux administrateurs (ADMIN).
 */
@RestController
@RequestMapping("/api/admin/reports/bilan")
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin(origins = "http://localhost:5173")
public class BilanReportController {

    private final BilanReportService bilanReportService;
    private final BilanReportPdfService bilanReportPdfService;
    private final BilanReportExcelService bilanReportExcelService;

    public BilanReportController(BilanReportService bilanReportService,
                                BilanReportPdfService bilanReportPdfService,
                                BilanReportExcelService bilanReportExcelService) {
        this.bilanReportService = bilanReportService;
        this.bilanReportPdfService = bilanReportPdfService;
        this.bilanReportExcelService = bilanReportExcelService;
    }

    /**
     * Retourne le rapport en JSON (même données que les exports).
     *
     * @param groupBy  DAY, MONTH ou YEAR (défaut: MONTH)
     * @param dateFrom date de début optionnelle (yyyy-MM-dd)
     * @param dateTo   date de fin optionnelle (yyyy-MM-dd)
     */
    @GetMapping
    public ResponseEntity<BilanReportDTO> getBilan(
            @RequestParam(defaultValue = "MONTH") String groupBy,
            @RequestParam(required = false) String dateFrom,
            @RequestParam(required = false) String dateTo,
            @RequestHeader(value = "Accept-Language", required = false) String acceptLanguage) {
        Locale locale = parseLocale(acceptLanguage);
        LocalDateTime from = parseDate(dateFrom, true);
        LocalDateTime to = parseDate(dateTo, false);
        BilanReportDTO report = bilanReportService.buildReport(groupBy, from, to, locale);
        return ResponseEntity.ok(report);
    }

    /**
     * Export PDF du rapport.
     */
    @GetMapping(value = "/export/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> exportPdf(
            @RequestParam(defaultValue = "MONTH") String groupBy,
            @RequestParam(required = false) String dateFrom,
            @RequestParam(required = false) String dateTo,
            @RequestHeader(value = "Accept-Language", required = false) String acceptLanguage) throws IOException {
        Locale locale = parseLocale(acceptLanguage);
        LocalDateTime from = parseDate(dateFrom, true);
        LocalDateTime to = parseDate(dateTo, false);
        BilanReportDTO report = bilanReportService.buildReport(groupBy, from, to, locale);
        byte[] pdf = bilanReportPdfService.generatePdf(report, locale);
        String filename = "bilan-comptable_" + LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE) + ".pdf";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(pdf);
    }

    /**
     * Export Excel du rapport.
     */
    @GetMapping(value = "/export/excel", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public ResponseEntity<byte[]> exportExcel(
            @RequestParam(defaultValue = "MONTH") String groupBy,
            @RequestParam(required = false) String dateFrom,
            @RequestParam(required = false) String dateTo,
            @RequestHeader(value = "Accept-Language", required = false) String acceptLanguage) throws IOException {
        Locale locale = parseLocale(acceptLanguage);
        LocalDateTime from = parseDate(dateFrom, true);
        LocalDateTime to = parseDate(dateTo, false);
        BilanReportDTO report = bilanReportService.buildReport(groupBy, from, to, locale);
        byte[] excel = bilanReportExcelService.generateExcel(report, locale);
        String filename = "bilan-comptable_" + LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE) + ".xlsx";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(excel);
    }

    private static Locale parseLocale(String acceptLanguage) {
        if (acceptLanguage == null || acceptLanguage.isBlank()) return Locale.FRENCH;
        String[] parts = acceptLanguage.split("[,-]");
        if (parts.length >= 2) return new Locale(parts[0].trim(), parts[1].trim());
        if (parts.length == 1) return new Locale(parts[0].trim());
        return Locale.FRENCH;
    }

    private static LocalDateTime parseDate(String dateStr, boolean startOfDay) {
        if (dateStr == null || dateStr.isBlank()) return null;
        try {
            LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE);
            return startOfDay ? date.atStartOfDay() : date.atTime(LocalTime.MAX);
        } catch (Exception e) {
            return null;
        }
    }
}
