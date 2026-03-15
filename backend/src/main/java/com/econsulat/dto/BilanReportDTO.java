package com.econsulat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Rapport de bilan comptable : demandes approuvées, paiements payés, agrégés par période et type de document.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BilanReportDTO {

    /** Granularité : DAY, MONTH, YEAR. */
    private String groupBy;

    private LocalDateTime dateFrom;
    private LocalDateTime dateTo;

    private LocalDateTime generatedAt;
    private String currency;

    private List<BilanReportRowDTO> rows;

    /** Total général (tous types) en centimes. */
    private long totalAmountCents;

    /** Total en euros (affichage). */
    private String totalAmountEuros;
}
