package com.econsulat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Résumé d'un document généré pour l'historique / dossier citoyen.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentSummaryDTO {
    private Long id;
    private String fileName;
}
