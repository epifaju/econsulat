package com.econsulat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.url}")
    private String appUrl;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendEmailVerification(String toEmail, String token) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Validation de votre compte eConsulat");

            String verificationUrl = appUrl + "/verify-email?token=" + token;

            message.setText(
                    "Bonjour,\n\n" +
                            "Votre compte eConsulat a été créé avec succès.\n\n" +
                            "Pour activer votre compte, veuillez cliquer sur le lien suivant :\n" +
                            verificationUrl + "\n\n" +
                            "Ce lien expire dans 24 heures.\n\n" +
                            "Si vous n'avez pas créé ce compte, vous pouvez ignorer cet email.\n\n" +
                            "Cordialement,\n" +
                            "L'équipe eConsulat");

            mailSender.send(message);
            log.info("Email de validation envoyé à : {}", toEmail);
        } catch (Exception e) {
            log.error("Erreur lors de l'envoi de l'email de validation à {} : {}", toEmail, e.getMessage());
            throw new RuntimeException("Erreur lors de l'envoi de l'email de validation", e);
        }
    }

    public void sendWelcomeEmail(String toEmail, String firstName) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Bienvenue sur eConsulat");

            message.setText(
                    "Bonjour " + firstName + ",\n\n" +
                            "Votre compte eConsulat a été activé avec succès.\n\n" +
                            "Vous pouvez maintenant vous connecter à l'application.\n\n" +
                            "Cordialement,\n" +
                            "L'équipe eConsulat");

            mailSender.send(message);
            log.info("Email de bienvenue envoyé à : {}", toEmail);
        } catch (Exception e) {
            log.error("Erreur lors de l'envoi de l'email de bienvenue à {} : {}", toEmail, e.getMessage());
        }
    }
}