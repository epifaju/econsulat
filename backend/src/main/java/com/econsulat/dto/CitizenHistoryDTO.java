package com.econsulat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Dossier citoyen / historique complet : profil résumé + liste des demandes.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CitizenHistoryDTO {
    private String userFirstName;
    private String userLastName;
    private String userEmail;
    private int totalDemandes;
    private long totalPaidCents;
    private String totalPaidEuros;
    private List<DemandeHistoryDTO> demandes = new ArrayList<>();
}
