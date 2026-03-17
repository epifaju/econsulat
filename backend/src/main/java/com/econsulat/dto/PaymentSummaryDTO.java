package com.econsulat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Résumé d'un paiement pour l'historique / dossier citoyen.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentSummaryDTO {
    private Long id;
    private Integer amountCents;
    private String amountEuros;
    private LocalDateTime paidAt;
    private String status;
}
