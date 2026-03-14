package com.econsulat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final MessageSource messageSource;

    @Value("${app.url}")
    private String appUrl;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.contact.to:contact@econsulat.com}")
    private String contactToEmail;

    /**
     * Envoie l'email de vérification de compte dans la langue demandée (fr/pt).
     */
    public void sendEmailVerification(String toEmail, String token, Locale locale) {
        if (locale == null) {
            locale = Locale.FRENCH;
        }
        if (!"pt".equals(locale.getLanguage())) {
            locale = Locale.FRENCH;
        }
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject(messageSource.getMessage("email.verification.subject", null, locale));

            String verificationUrl = appUrl + "/verify-email?token=" + token;
            String greeting = messageSource.getMessage("email.verification.greeting", null, locale);
            String intro = messageSource.getMessage("email.verification.body.intro", null, locale);
            String activate = messageSource.getMessage("email.verification.body.activate", null, locale);
            String expiry = messageSource.getMessage("email.verification.body.expiry", null, locale);
            String ignore = messageSource.getMessage("email.verification.body.ignore", null, locale);
            String signature = messageSource.getMessage("email.verification.signature", null, locale);

            message.setText(
                    greeting + "\n\n" +
                            intro + "\n\n" +
                            activate + "\n" +
                            verificationUrl + "\n\n" +
                            expiry + "\n\n" +
                            ignore + "\n\n" +
                            signature);

            mailSender.send(message);
            log.info("Email de validation envoyé à : {}", toEmail);
        } catch (Exception e) {
            log.error("Erreur lors de l'envoi de l'email de validation à {} : {}", toEmail, e.getMessage());
            throw new RuntimeException("Erreur lors de l'envoi de l'email de validation", e);
        }
    }

    /**
     * Envoie l'email de bienvenue après activation du compte (fr/pt).
     */
    public void sendWelcomeEmail(String toEmail, String firstName, Locale locale) {
        if (locale == null) {
            locale = Locale.FRENCH;
        }
        if (!"pt".equals(locale.getLanguage())) {
            locale = Locale.FRENCH;
        }
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject(messageSource.getMessage("email.welcome.subject", null, locale));

            String greeting = messageSource.getMessage("email.welcome.greeting", new Object[]{firstName}, locale);
            String intro = messageSource.getMessage("email.welcome.body.intro", null, locale);
            String login = messageSource.getMessage("email.welcome.body.login", null, locale);
            String signature = messageSource.getMessage("email.welcome.signature", null, locale);

            message.setText(
                    greeting + "\n\n" +
                            intro + "\n\n" +
                            login + "\n\n" +
                            signature);

            mailSender.send(message);
            log.info("Email de bienvenue envoyé à : {}", toEmail);
        } catch (Exception e) {
            log.error("Erreur lors de l'envoi de l'email de bienvenue à {} : {}", toEmail, e.getMessage());
        }
    }

    /**
     * Envoie le message du formulaire de contact vers la boîte configurée (app.contact.to).
     * Sujet et corps construits à partir du nom, email, sujet optionnel et message.
     */
    public void sendContactMessage(String name, String email, String subject, String message) {
        try {
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setFrom(fromEmail);
            mail.setTo(contactToEmail);
            String subjectLine = (subject != null && !subject.isBlank())
                    ? "[eConsulat Contact] " + subject
                    : "[eConsulat Contact] Message depuis le formulaire";
            mail.setSubject(subjectLine);
            String body = String.format(
                    "Message reçu depuis le formulaire de contact eConsulat%n%n"
                            + "Nom : %s%n"
                            + "Email : %s%n"
                            + "Sujet : %s%n%n"
                            + "Message :%n%s",
                    name, email, subject != null ? subject : "(non précisé)", message);
            mail.setText(body);
            mailSender.send(mail);
            log.info("Message de contact envoyé de {} vers {}", email, contactToEmail);
        } catch (Exception e) {
            log.error("Erreur envoi message contact de {} : {}", email, e.getMessage());
            throw new RuntimeException("Erreur lors de l'envoi du message de contact", e);
        }
    }

    /**
     * Envoie l'email de réinitialisation de mot de passe (lien avec token) en FR/PT.
     */
    public void sendPasswordResetEmail(String toEmail, String token, Locale locale) {
        if (locale == null || !"pt".equals(locale.getLanguage())) {
            locale = Locale.FRENCH;
        }
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject(messageSource.getMessage("email.passwordReset.subject", null, locale));
            String resetUrl = appUrl + "/reset-password?token=" + token;
            String greeting = messageSource.getMessage("email.passwordReset.greeting", null, locale);
            String intro = messageSource.getMessage("email.passwordReset.body.intro", null, locale);
            String link = messageSource.getMessage("email.passwordReset.body.link", null, locale);
            String expiry = messageSource.getMessage("email.passwordReset.body.expiry", null, locale);
            String ignore = messageSource.getMessage("email.passwordReset.body.ignore", null, locale);
            String signature = messageSource.getMessage("email.verification.signature", null, locale);
            message.setText(
                    greeting + "\n\n" +
                            intro + "\n\n" +
                            link + "\n" +
                            resetUrl + "\n\n" +
                            expiry + "\n\n" +
                            ignore + "\n\n" +
                            signature);
            mailSender.send(message);
            log.info("Email de réinitialisation mot de passe envoyé à : {}", toEmail);
        } catch (Exception e) {
            log.error("Erreur envoi email réinitialisation à {} : {}", toEmail, e.getMessage());
            throw new RuntimeException("Erreur lors de l'envoi de l'email de réinitialisation", e);
        }
    }
}