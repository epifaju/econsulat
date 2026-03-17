package com.econsulat.service;

import com.econsulat.model.Demande;
import com.econsulat.model.DocumentType;
import com.econsulat.model.Notification;
import com.econsulat.model.Payment;
import com.econsulat.model.User;
import com.econsulat.repository.NotificationRepository;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("EmailNotificationService")
class EmailNotificationServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private EmailNotificationService emailNotificationService;

    private Demande demande;
    private User user;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(emailNotificationService, "appUrl", "http://localhost:5173");
        ReflectionTestUtils.setField(emailNotificationService, "fromEmail", "noreply@test.com");

        user = new User();
        user.setId(1L);
        user.setFirstName("Jean");
        user.setLastName("Dupont");
        user.setEmail("jean@test.com");
        user.setPreferredLocale("fr");

        DocumentType docType = new DocumentType();
        docType.setLibelle("Acte de naissance");
        demande = new Demande();
        demande.setId(10L);
        demande.setUser(user);
        demande.setDocumentType(docType);
        demande.setStatus(Demande.Status.PENDING_PAYMENT);

        when(messageSource.getMessage(anyString(), any(), anyString(), any(Locale.class))).thenReturn("Message");
        when(messageSource.getMessage(anyString(), any(), any(Locale.class))).thenReturn("Message");
    }

    @Nested
    @DisplayName("sendStatusChangeNotification")
    class SendStatusChangeNotification {

        @Test
        void throw_quand_mail_non_configure() {
            ReflectionTestUtils.setField(emailNotificationService, "fromEmail", "");

            assertThatThrownBy(() -> emailNotificationService.sendStatusChangeNotification(
                    demande, user, Demande.Status.APPROVED))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("envoi d'email n'est pas configuré");
            verify(mailSender, never()).send(any(SimpleMailMessage.class));
        }

        @Test
        void envoie_email_et_sauvegarde_notification_quand_config_ok() {
            when(messageSource.getMessage(eq("email.notification.subject"), isNull(), any(Locale.class)))
                    .thenReturn("Votre demande");
            when(messageSource.getMessage(eq("email.notification.greeting"), any(), any(Locale.class)))
                    .thenReturn("Bonjour Jean Dupont");
            when(messageSource.getMessage(anyString(), any(), any(Locale.class))).thenReturn("Texte");

            emailNotificationService.sendStatusChangeNotification(demande, user, Demande.Status.APPROVED);

            ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
            verify(mailSender).send(messageCaptor.capture());
            SimpleMailMessage sent = messageCaptor.getValue();
            assertThat(sent.getFrom()).isEqualTo("noreply@test.com");
            assertThat(sent.getTo()).containsExactly("jean@test.com");

            verify(notificationRepository).save(argThat(n ->
                    n.getEmailDestinataire().equals("jean@test.com")
                            && n.getNewStatus().equals("APPROVED")
                            && n.getStatut() == Notification.Statut.ENVOYE
            ));
        }
    }

    @Nested
    @DisplayName("resendNotification")
    class ResendNotification {

        @Test
        void throw_quand_notification_inexistante() {
            when(notificationRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> emailNotificationService.resendNotification(99L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Notification non trouvée");
        }

        @Test
        void throw_quand_mail_non_configure() {
            ReflectionTestUtils.setField(emailNotificationService, "fromEmail", "");
            when(notificationRepository.findById(1L)).thenReturn(Optional.of(new Notification()));

            assertThatThrownBy(() -> emailNotificationService.resendNotification(1L))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("envoi d'email n'est pas configuré");
        }

        @Test
        void renvoie_email_et_met_a_jour_statut_quand_ok() {
            Notification notification = new Notification();
            notification.setId(1L);
            notification.setEmailDestinataire("jean@test.com");
            notification.setObjet("Sujet");
            notification.setContenu("Contenu");
            notification.setStatut(Notification.Statut.ECHEC);

            when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));
            when(notificationRepository.save(any(Notification.class))).thenAnswer(inv -> inv.getArgument(0));

            emailNotificationService.resendNotification(1L);

            ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
            verify(mailSender).send(messageCaptor.capture());
            assertThat(messageCaptor.getValue().getTo()).containsExactly("jean@test.com");

            verify(notificationRepository).save(argThat(n -> n.getStatut() == Notification.Statut.ENVOYE));
        }
    }

    @Nested
    @DisplayName("sendDemandCreatedNotification")
    class SendDemandCreatedNotification {

        @Test
        void envoie_email_et_sauvegarde_notification() {
            when(messageSource.getMessage(eq("email.demand.created.subject"), isNull(), any(Locale.class)))
                    .thenReturn("Votre demande enregistrée");
            when(messageSource.getMessage(eq("email.demand.created.greeting"), any(), any(Locale.class)))
                    .thenReturn("Bonjour Jean Dupont");
            when(messageSource.getMessage(anyString(), any(), any(Locale.class))).thenReturn("Texte");

            emailNotificationService.sendDemandCreatedNotification(demande, user);

            ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
            verify(mailSender).send(messageCaptor.capture());
            assertThat(messageCaptor.getValue().getTo()).containsExactly("jean@test.com");
            verify(notificationRepository).save(argThat(n ->
                    n.getEmailDestinataire().equals("jean@test.com")
                            && n.getNewStatus().equals("PENDING_PAYMENT")
                            && n.getStatut() == Notification.Statut.ENVOYE
            ));
        }

        @Test
        void ne_plante_pas_et_sauvegarde_echec_quand_mail_send_echoue() {
            when(messageSource.getMessage(anyString(), any(), any(Locale.class))).thenReturn("Sujet");
            when(messageSource.getMessage(anyString(), any(), any())).thenReturn("Contenu");
            doThrow(new RuntimeException("SMTP error")).when(mailSender).send(any(SimpleMailMessage.class));

            emailNotificationService.sendDemandCreatedNotification(demande, user);

            verify(notificationRepository).save(argThat(n -> n.getStatut() == Notification.Statut.ECHEC));
        }
    }

    @Nested
    @DisplayName("sendPaymentSuccessNotification")
    class SendPaymentSuccessNotification {

        @Test
        void envoie_email_et_sauvegarde_notification() {
            Payment payment = new Payment();
            payment.setId(100L);
            payment.setAmountCents(2500);
            payment.setDemande(demande);
            demande.setStatus(Demande.Status.PENDING);

            when(messageSource.getMessage(eq("email.payment.success.subject"), isNull(), any(Locale.class)))
                    .thenReturn("Paiement confirmé");
            when(messageSource.getMessage(anyString(), any(), any(Locale.class))).thenReturn("Texte");

            emailNotificationService.sendPaymentSuccessNotification(payment);

            ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
            verify(mailSender).send(messageCaptor.capture());
            assertThat(messageCaptor.getValue().getTo()).containsExactly("jean@test.com");
            verify(notificationRepository).save(argThat(n ->
                    n.getEmailDestinataire().equals("jean@test.com") && n.getStatut() == Notification.Statut.ENVOYE
            ));
        }

        @Test
        void ne_fait_rien_quand_demande_sans_utilisateur() {
            Demande demandeSansUser = new Demande();
            demandeSansUser.setId(20L);
            demandeSansUser.setUser(null);
            Payment payment = new Payment();
            payment.setDemande(demandeSansUser);

            emailNotificationService.sendPaymentSuccessNotification(payment);

            verify(mailSender, never()).send(any(SimpleMailMessage.class));
            verify(notificationRepository, never()).save(any(Notification.class));
        }
    }

    @Nested
    @DisplayName("sendDocumentReadyNotification")
    class SendDocumentReadyNotification {

        @Test
        void envoie_email_et_sauvegarde_notification() {
            demande.setStatus(Demande.Status.APPROVED);
            when(messageSource.getMessage(eq("email.document.ready.subject"), isNull(), any(Locale.class)))
                    .thenReturn("Votre document est disponible");
            when(messageSource.getMessage(anyString(), any(), any(Locale.class))).thenReturn("Texte");

            emailNotificationService.sendDocumentReadyNotification(demande, user);

            ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
            verify(mailSender).send(messageCaptor.capture());
            assertThat(messageCaptor.getValue().getTo()).containsExactly("jean@test.com");
            verify(notificationRepository).save(argThat(n ->
                    n.getEmailDestinataire().equals("jean@test.com") && n.getStatut() == Notification.Statut.ENVOYE
            ));
        }
    }
}
