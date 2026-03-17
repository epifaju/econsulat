package com.econsulat.service;

import com.econsulat.model.Demande;
import com.econsulat.model.Notification;
import com.econsulat.model.Payment;
import com.econsulat.model.User;
import com.econsulat.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailNotificationService {

    private final JavaMailSender mailSender;
    private final NotificationRepository notificationRepository;
    private final MessageSource messageSource;

    @Value("${app.url}")
    private String appUrl;

    @Value("${spring.mail.username:}")
    private String fromEmail;

    @PostConstruct
    public void logMailConfig() {
        if (fromEmail == null || fromEmail.isBlank()) {
            log.info("Mail SMTP: non configuré (MAIL_USERNAME vide) - définir dans .env ou application-local.properties");
        } else {
            log.info("Mail SMTP: configuré (expéditeur: {})", fromEmail);
        }
    }

    /** Vérifie que l'envoi d'email est configuré (évite des erreurs SMTP opaques). */
    private void ensureMailConfigured() {
        if (fromEmail == null || fromEmail.isBlank()) {
            throw new IllegalStateException(
                "L'envoi d'email n'est pas configuré. Définissez MAIL_USERNAME et MAIL_PASSWORD dans le .env (Docker) ou application-local.properties."
            );
        }
    }

    private static Locale toLocale(String preferredLocale) {
        if (preferredLocale != null && "pt".equalsIgnoreCase(preferredLocale.trim())) {
            return new Locale("pt");
        }
        return Locale.FRENCH;
    }

    /** Récupère le message puis applique MessageFormat pour remplacer {0}, {1}, {2}, ... */
    private String getFormattedMessage(String code, Object[] args, Locale locale) {
        String pattern = messageSource.getMessage(code, null, locale);
        return (args != null && args.length > 0)
                ? MessageFormat.format(pattern, args)
                : pattern;
    }

    /**
     * Envoie une notification par email lors du changement de statut d'une demande.
     * La langue utilisée est celle préférée de l'utilisateur (ou français par défaut).
     */
    @Transactional
    public void sendStatusChangeNotification(Demande demande, User user, Demande.Status newStatus) {
        ensureMailConfigured();
        Locale locale = toLocale(user.getPreferredLocale());
        try {
            String objet = messageSource.getMessage("email.notification.subject", null, locale);
            String contenu = buildEmailContent(user, demande, newStatus, locale);

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(user.getEmail());
            message.setSubject(objet);
            message.setText(contenu);

            mailSender.send(message);

            Notification notification = new Notification();
            notification.setDemande(demande);
            notification.setUtilisateur(user);
            notification.setEmailDestinataire(user.getEmail());
            notification.setObjet(objet);
            notification.setContenu(contenu);
            notification.setNewStatus(newStatus.name());
            notification.setStatut(Notification.Statut.ENVOYE);
            notification.setDateEnvoi(LocalDateTime.now());

            notificationRepository.save(notification);

            log.info("Notification de changement de statut envoyée avec succès à {} pour la demande {}",
                    user.getEmail(), demande.getId());

        } catch (Exception e) {
            log.error("Erreur lors de l'envoi de la notification de changement de statut à {} : {}",
                    user.getEmail(), e.getMessage(), e);

            try {
                String objet = messageSource.getMessage("email.notification.subject", null, locale);
                String contenu = buildEmailContent(user, demande, newStatus, locale);
                Notification notification = new Notification();
                notification.setDemande(demande);
                notification.setUtilisateur(user);
                notification.setEmailDestinataire(user.getEmail());
                notification.setObjet(objet);
                notification.setContenu(contenu);
                notification.setNewStatus(newStatus.name());
                notification.setStatut(Notification.Statut.ECHEC);
                notification.setDateEnvoi(LocalDateTime.now());

                notificationRepository.save(notification);
            } catch (Exception saveException) {
                log.error("Erreur lors de la sauvegarde de l'échec de notification : {}", saveException.getMessage());
            }
        }
    }

    /**
     * Construit le contenu de l'email selon la locale (FR/PT).
     */
    private String buildEmailContent(User user, Demande demande, Demande.Status newStatus, Locale locale) {
        String greeting = messageSource.getMessage("email.notification.greeting",
                new Object[]{user.getFirstName(), user.getLastName()}, locale);
        String intro = messageSource.getMessage("email.notification.body.intro", new Object[]{demande.getId()}, locale);
        String statusKey = "demand.status." + newStatus.name();
        String statusLabel = messageSource.getMessage(statusKey, null, newStatus.getDisplayName(), locale);
        String newStatusLine = messageSource.getMessage("email.notification.body.newStatus", new Object[]{statusLabel}, locale);
        String moreInfo = messageSource.getMessage("email.notification.body.moreInfo", null, locale);
        String signature = messageSource.getMessage("email.notification.signature", null, locale);

        return greeting + "\n\n" + intro + "\n" + newStatusLine + "\n\n" + moreInfo + "\n" + appUrl + "/espace-citoyen\n\n" + signature;
    }

    /**
     * Envoie un email de confirmation lorsque une demande a été créée.
     */
    @Transactional
    public void sendDemandCreatedNotification(Demande demande, User user) {
        ensureMailConfigured();
        Locale locale = toLocale(user.getPreferredLocale());
        try {
            String typeLabel = demande.getDocumentType() != null ? demande.getDocumentType().getLibelle() : "";
            Object[] demandCreatedArgs = new Object[]{user.getFirstName(), user.getLastName(), demande.getId(), typeLabel};
            String objet = messageSource.getMessage("email.demand.created.subject", null, locale);
            String greeting = getFormattedMessage("email.demand.created.greeting", demandCreatedArgs, locale);
            String intro = getFormattedMessage("email.demand.created.body.intro", demandCreatedArgs, locale);
            String next = messageSource.getMessage("email.demand.created.body.next", null, locale);
            String linkLabel = messageSource.getMessage("email.demand.created.body.link", null, locale);
            String signature = messageSource.getMessage("email.notification.signature", null, locale);
            String contenu = greeting + "\n\n" + intro + "\n" + next + "\n\n" + linkLabel + "\n" + appUrl + "\n\n" + signature;

            sendAndSaveNotification(user.getEmail(), objet, contenu, demande, user, demande.getStatus().name());
            log.info("Notification demande créée envoyée à {} pour la demande {}", user.getEmail(), demande.getId());
        } catch (Exception e) {
            log.warn("Erreur envoi notification demande créée à {} : {}", user.getEmail(), e.getMessage());
            saveNotificationFailure(user.getEmail(), demande, user, demande.getStatus().name(), locale, "email.demand.created.subject", e);
        }
    }

    /**
     * Envoie un email de confirmation lorsque le paiement a été reçu.
     */
    @Transactional
    public void sendPaymentSuccessNotification(Payment payment) {
        ensureMailConfigured();
        Demande demande = payment.getDemande();
        User user = demande.getUser();
        if (user == null) {
            log.warn("Pas d'utilisateur sur la demande {}, notification paiement non envoyée", demande.getId());
            return;
        }
        Locale locale = toLocale(user.getPreferredLocale());
        try {
            String typeLabel = demande.getDocumentType() != null ? demande.getDocumentType().getLibelle() : "";
            String amountEuros = payment.getAmountCents() != null ? String.format(locale, "%.2f", payment.getAmountCents() / 100.0) : "0.00";
            Object[] paymentSuccessArgs = new Object[]{user.getFirstName(), user.getLastName(), demande.getId(), amountEuros, typeLabel};
            String objet = messageSource.getMessage("email.payment.success.subject", null, locale);
            String greeting = getFormattedMessage("email.payment.success.greeting", paymentSuccessArgs, locale);
            String intro = getFormattedMessage("email.payment.success.body.intro", paymentSuccessArgs, locale);
            String amountLine = getFormattedMessage("email.payment.success.body.amount", paymentSuccessArgs, locale);
            String typeLine = getFormattedMessage("email.payment.success.body.type", paymentSuccessArgs, locale);
            String linkLabel = messageSource.getMessage("email.payment.success.body.link", null, locale);
            String signature = messageSource.getMessage("email.notification.signature", null, locale);
            String contenu = greeting + "\n\n" + intro + "\n" + amountLine + "\n" + typeLine + "\n\n" + linkLabel + "\n" + appUrl + "\n\n" + signature;

            sendAndSaveNotification(user.getEmail(), objet, contenu, demande, user, demande.getStatus().name());
            log.info("Notification paiement réussi envoyée à {} pour la demande {}", user.getEmail(), demande.getId());
        } catch (Exception e) {
            log.warn("Erreur envoi notification paiement réussi à {} : {}", user.getEmail(), e.getMessage());
            saveNotificationFailure(user.getEmail(), demande, user, demande.getStatus().name(), locale, "email.payment.success.subject", e);
        }
    }

    /**
     * Envoie un email lorsque le document généré est disponible.
     */
    @Transactional
    public void sendDocumentReadyNotification(Demande demande, User user) {
        ensureMailConfigured();
        Locale locale = toLocale(user.getPreferredLocale());
        try {
            String typeLabel = demande.getDocumentType() != null ? demande.getDocumentType().getLibelle() : "";
            Object[] documentReadyArgs = new Object[]{user.getFirstName(), user.getLastName(), demande.getId(), typeLabel};
            String objet = messageSource.getMessage("email.document.ready.subject", null, locale);
            String greeting = getFormattedMessage("email.document.ready.greeting", documentReadyArgs, locale);
            String intro = getFormattedMessage("email.document.ready.body.intro", documentReadyArgs, locale);
            String download = messageSource.getMessage("email.document.ready.body.download", null, locale);
            String signature = messageSource.getMessage("email.notification.signature", null, locale);
            String contenu = greeting + "\n\n" + intro + "\n" + download + "\n\n" + signature;

            sendAndSaveNotification(user.getEmail(), objet, contenu, demande, user, demande.getStatus().name());
            log.info("Notification document prêt envoyée à {} pour la demande {}", user.getEmail(), demande.getId());
        } catch (Exception e) {
            log.warn("Erreur envoi notification document prêt à {} : {}", user.getEmail(), e.getMessage());
            saveNotificationFailure(user.getEmail(), demande, user, demande.getStatus().name(), locale, "email.document.ready.subject", e);
        }
    }

    private void sendAndSaveNotification(String toEmail, String objet, String contenu, Demande demande, User user, String newStatusValue) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject(objet);
        message.setText(contenu);
        mailSender.send(message);

        Notification notification = new Notification();
        notification.setDemande(demande);
        notification.setUtilisateur(user);
        notification.setEmailDestinataire(toEmail);
        notification.setObjet(objet);
        notification.setContenu(contenu);
        notification.setNewStatus(newStatusValue);
        notification.setStatut(Notification.Statut.ENVOYE);
        notification.setDateEnvoi(LocalDateTime.now());
        notificationRepository.save(notification);
    }

    private void saveNotificationFailure(String toEmail, Demande demande, User user, String newStatusValue, Locale locale, String subjectKey, Exception e) {
        try {
            String objet = messageSource.getMessage(subjectKey, null, locale);
            Notification notification = new Notification();
            notification.setDemande(demande);
            notification.setUtilisateur(user);
            notification.setEmailDestinataire(toEmail);
            notification.setObjet(objet);
            notification.setContenu(e.getMessage() != null ? e.getMessage() : "Erreur envoi");
            notification.setNewStatus(newStatusValue);
            notification.setStatut(Notification.Statut.ECHEC);
            notification.setDateEnvoi(LocalDateTime.now());
            notificationRepository.save(notification);
        } catch (Exception ex) {
            log.error("Erreur sauvegarde échec notification : {}", ex.getMessage());
        }
    }

    /**
     * Renvoie manuellement une notification (fonctionnalité bonus pour les admins)
     */
    @Transactional
    public void resendNotification(Long notificationId) {
        ensureMailConfigured();
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
            String msg = buildUserFriendlyMailError(e);
            throw new RuntimeException(msg, e);
        }
    }

    private static String getRootCauseMessage(Throwable e) {
        Throwable cause = e;
        while (cause.getCause() != null) cause = cause.getCause();
        return cause.getMessage() != null ? cause.getMessage() : cause.getClass().getSimpleName();
    }

    /** Message lisible pour l'utilisateur en cas d'échec d'envoi (SMTP, auth, etc.). */
    private String buildUserFriendlyMailError(Exception e) {
        String detail = e.getMessage();
        Throwable cause = e.getCause();
        while (cause != null) {
            if (cause.getMessage() != null) detail = cause.getMessage();
            cause = cause.getCause();
        }
        if (detail != null) {
            String lower = detail.toLowerCase();
            if (lower.contains("auth") || lower.contains("535") || lower.contains("password") || lower.contains("username") || lower.contains("invalid login")) {
                return "Envoi d'email impossible : identifiants SMTP invalides. Vérifiez MAIL_USERNAME et MAIL_PASSWORD (mot de passe d'application Gmail, 16 caractères SANS espaces).";
            }
        }
        return "Erreur lors du renvoi de la notification. " + (detail != null ? detail : "Vérifiez la configuration SMTP.");
    }
}
