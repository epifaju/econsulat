package com.econsulat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Une demande dans l'historique / dossier citoyen (résumé + paiements + documents).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DemandeHistoryDTO {
    private Long id;
    private Long documentTypeId;
    private String documentTypeLibelle;
    private LocalDateTime createdAt;
    private String status;
    private Long totalPaidCents;
    private String totalPaidEuros;
    private List<PaymentSummaryDTO> payments = new ArrayList<>();
    private List<DocumentSummaryDTO> documents = new ArrayList<>();
}
