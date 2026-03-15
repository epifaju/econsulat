package com.econsulat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Une ligne du rapport de bilan comptable (période + type de document + montant).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BilanReportRowDTO {

    /** Libellé de la période (ex. "2025-01", "Janvier 2025", "15/01/2025"). */
    private String periodLabel;

    private Long documentTypeId;
    private String documentTypeLibelle;

    /** Nombre de demandes (paiements) dans cette ligne. */
    private long count;

    /** Montant total en centimes. */
    private long amountCents;

    /** Montant en euros (affichage). */
    private String amountEuros;
}
