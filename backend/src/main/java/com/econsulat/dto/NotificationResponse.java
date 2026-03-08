package com.econsulat.dto;

import com.econsulat.model.Notification;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO pour l'affichage des notifications avec données permettant
 * de reconstruire le sujet et le contenu dans la langue de l'interface.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {

    private Long id;
    private String emailDestinataire;
    private String objet;
    private String contenu;
    private String statut;
    private LocalDateTime dateEnvoi;

    /** ID de la demande concernée (pour réaffichage traduit). */
    private Long demandeId;
    /** Statut de la demande au moment de l'envoi (ex: APPROVED). */
    private String newStatus;
    private String recipientFirstName;
    private String recipientLastName;

    public static NotificationResponse from(Notification n) {
        if (n == null) return null;
        NotificationResponse r = new NotificationResponse();
        r.setId(n.getId());
        r.setEmailDestinataire(n.getEmailDestinataire());
        r.setObjet(n.getObjet());
        r.setContenu(n.getContenu());
        r.setStatut(n.getStatut() != null ? n.getStatut().name() : null);
        r.setDateEnvoi(n.getDateEnvoi());
        r.setNewStatus(n.getNewStatus());
        if (n.getDemande() != null) {
            r.setDemandeId(n.getDemande().getId());
        }
        if (n.getUtilisateur() != null) {
            r.setRecipientFirstName(n.getUtilisateur().getFirstName());
            r.setRecipientLastName(n.getUtilisateur().getLastName());
        }
        return r;
    }
}
