package com.econsulat.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.context.MessageSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("EmailService")
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(emailService, "appUrl", "http://localhost:5173");
        ReflectionTestUtils.setField(emailService, "fromEmail", "noreply@test.com");
        when(messageSource.getMessage(anyString(), any(), any(Locale.class))).thenReturn("Message");
        when(messageSource.getMessage(anyString(), isNull(), any(Locale.class))).thenReturn("Message");
    }

    @Nested
    @DisplayName("sendEmailVerification")
    class SendEmailVerification {

        @Test
        void envoie_email_avec_url_verification_et_appelle_send() {
            when(messageSource.getMessage(eq("email.verification.subject"), isNull(), any(Locale.class)))
                    .thenReturn("Vérifiez votre email");
            when(messageSource.getMessage(eq("email.verification.body.activate"), isNull(), any(Locale.class)))
                    .thenReturn("Cliquez sur le lien");

            emailService.sendEmailVerification("user@test.com", "token123", Locale.FRENCH);

            ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
            verify(mailSender).send(captor.capture());
            SimpleMailMessage msg = captor.getValue();
            assertThat(msg.getFrom()).isEqualTo("noreply@test.com");
            assertThat(msg.getTo()).containsExactly("user@test.com");
            assertThat(msg.getText()).contains("token123");
            assertThat(msg.getText()).contains("http://localhost:5173/verify-email?token=token123");
        }
    }

    @Nested
    @DisplayName("sendWelcomeEmail")
    class SendWelcomeEmail {

        @Test
        void envoie_email_bienvenue_avec_prenom() {
            when(messageSource.getMessage(eq("email.welcome.subject"), isNull(), any(Locale.class)))
                    .thenReturn("Bienvenue");
            when(messageSource.getMessage(eq("email.welcome.greeting"), any(Object[].class), any(Locale.class)))
                    .thenReturn("Bonjour Jean");

            emailService.sendWelcomeEmail("jean@test.com", "Jean", Locale.FRENCH);

            ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
            verify(mailSender).send(captor.capture());
            assertThat(captor.getValue().getTo()).containsExactly("jean@test.com");
            assertThat(captor.getValue().getText()).contains("Jean");
        }
    }
}
