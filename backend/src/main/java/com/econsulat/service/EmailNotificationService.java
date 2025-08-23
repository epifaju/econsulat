package com.econsulat.service;

import com.econsulat.model.Demande;
import com.econsulat.model.Notification;
import com.econsulat.model.User;
import com.econsulat.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailNotificationService {

    private final JavaMailSender mailSender;
    private final NotificationRepository notificationRepository;

    @Value("${app.url}")
    private String appUrl;

    @Value("${spring.mail.username}")
    private String fromEmail;

    /**
     * Envoie une notification par email lors du changement de statut d'une demande
     */
    @Transactional
    public void sendStatusChangeNotification(Demande demande, User user, Demande.Status newStatus) {
        try {
            // Créer le contenu de l'email
            String objet = "[eConsulat] Mise à jour de votre demande";
            String contenu = buildEmailContent(user, demande, newStatus);

            // Envoyer l'email
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(user.getEmail());
            message.setSubject(objet);
            message.setText(contenu);

            mailSender.send(message);

            // Enregistrer la notification dans la base de données
            Notification notification = new Notification();
            notification.setDemande(demande);
            notification.setUtilisateur(user);
            notification.setEmailDestinataire(user.getEmail());
            notification.setObjet(objet);
            notification.setContenu(contenu);
            notification.setStatut(Notification.Statut.ENVOYE);
            notification.setDateEnvoi(LocalDateTime.now());

            notificationRepository.save(notification);

            log.info("Notification de changement de statut envoyée avec succès à {} pour la demande {}",
                    user.getEmail(), demande.getId());

        } catch (Exception e) {
            log.error("Erreur lors de l'envoi de la notification de changement de statut à {} : {}",
                    user.getEmail(), e.getMessage());

            // Enregistrer l'échec dans la base de données
            try {
                Notification notification = new Notification();
                notification.setDemande(demande);
                notification.setUtilisateur(user);
                notification.setEmailDestinataire(user.getEmail());
                notification.setObjet("[eConsulat] Mise à jour de votre demande");
                notification.setContenu(buildEmailContent(user, demande, newStatus));
                notification.setStatut(Notification.Statut.ECHEC);
                notification.setDateEnvoi(LocalDateTime.now());

                notificationRepository.save(notification);
            } catch (Exception saveException) {
                log.error("Erreur lors de la sauvegarde de l'échec de notification : {}", saveException.getMessage());
            }

            // Ne plus lancer d'exception pour éviter d'affecter la transaction principale
            // throw new RuntimeException("Erreur lors de l'envoi de la notification par
            // email", e);
        }
    }

    /**
     * Construit le contenu de l'email selon le template spécifié
     */
    private String buildEmailContent(User user, Demande demande, Demande.Status newStatus) {
        return String.format(
                "Bonjour %s %s,\n\n" +
                        "Nous vous informons que le statut de votre demande n°%d a été mis à jour :\n" +
                        "Nouveau statut : %s.\n\n" +
                        "Pour plus d'informations, connectez-vous à votre espace citoyen :\n" +
                        "%s/espace-citoyen\n\n" +
                        "Cordialement,\n" +
                        "L'équipe eConsulat",
                user.getFirstName(),
                user.getLastName(),
                demande.getId(),
                newStatus.getDisplayName(),
                appUrl);
    }

    /**
     * Renvoie manuellement une notification (fonctionnalité bonus pour les admins)
     */
    @Transactional
    public void resendNotification(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification non trouvée"));

        try {
            // Envoyer l'email
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(notification.getEmailDestinataire());
            message.setSubject(notification.getObjet());
            message.setText(notification.getContenu());

            mailSender.send(message);

            // Mettre à jour le statut
            notification.setStatut(Notification.Statut.ENVOYE);
            notification.setDateEnvoi(LocalDateTime.now());
            notificationRepository.save(notification);

            log.info("Notification renvoyée avec succès : {}", notificationId);

        } catch (Exception e) {
            log.error("Erreur lors du renvoi de la notification {} : {}", notificationId, e.getMessage());
            notification.setStatut(Notification.Statut.ECHEC);
            notificationRepository.save(notification);
            throw new RuntimeException("Erreur lors du renvoi de la notification", e);
        }
    }
}
