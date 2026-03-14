package com.econsulat.service;

import com.econsulat.model.Demande;
import com.econsulat.model.DocumentType;
import com.econsulat.model.Payment;
import com.econsulat.model.User;
import com.econsulat.repository.DemandeRepository;
import com.econsulat.repository.PaymentRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("PaymentService")
class PaymentServiceTest {

    @Mock
    private StripeService stripeService;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private DemandeRepository demandeRepository;

    @InjectMocks
    private PaymentService paymentService;

    private Demande demande;
    private Session stripeSession;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(paymentService, "successUrl", "http://localhost:5173/success");
        ReflectionTestUtils.setField(paymentService, "cancelUrl", "http://localhost:5173/cancel");
        ReflectionTestUtils.setField(paymentService, "defaultAmountCents", 1000);

        User user = new User();
        user.setId(1L);
        user.setEmail("user@test.com");

        demande = new Demande();
        demande.setId(10L);
        demande.setStatus(Demande.Status.PENDING_PAYMENT);
        demande.setUser(user);

        DocumentType docType = new DocumentType();
        docType.setId(1L);
        docType.setPriceCents(1500);
        demande.setDocumentType(docType);

        stripeSession = mock(Session.class);
        when(stripeSession.getId()).thenReturn("cs_test_123");
        when(stripeSession.getUrl()).thenReturn("https://checkout.stripe.com/pay/cs_test_123");
    }

    @Nested
    @DisplayName("createSessionForDemande")
    class CreateSessionForDemande {

        @Test
        void throw_quand_demande_inexistante() throws StripeException {
            when(demandeRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> paymentService.createSessionForDemande(99L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Demande non trouvée");
            verify(stripeService, never()).createCheckoutSessionAndReturnSession(anyInt(), anyString(), anyString(), anyLong());
        }

        @Test
        void throw_quand_demande_pas_en_attente_paiement() throws StripeException {
            demande.setStatus(Demande.Status.APPROVED);
            when(demandeRepository.findById(10L)).thenReturn(Optional.of(demande));

            assertThatThrownBy(() -> paymentService.createSessionForDemande(10L))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("pas en attente de paiement");
        }

        @Test
        void cree_session_sauvegarde_payment_et_retourne_url() throws StripeException {
            when(demandeRepository.findById(10L)).thenReturn(Optional.of(demande));
            when(paymentRepository.findByDemandeId(10L)).thenReturn(Optional.empty());
            when(stripeService.createCheckoutSessionAndReturnSession(eq(1500), anyString(), anyString(), eq(10L)))
                    .thenReturn(stripeSession);
            when(paymentRepository.save(any(Payment.class))).thenAnswer(inv -> inv.getArgument(0));

            String url = paymentService.createSessionForDemande(10L);

            assertThat(url).isEqualTo("https://checkout.stripe.com/pay/cs_test_123");
            verify(paymentRepository).save(argThat(p ->
                    p.getStripeSessionId().equals("cs_test_123")
                            && p.getAmountCents() == 1500
                            && p.getDemande().getId() == 10L
                            && p.getStatus() == Payment.PaymentStatus.PENDING
            ));
        }

        @Test
        void utilise_prix_du_document_type_quand_present() throws StripeException {
            when(demandeRepository.findById(10L)).thenReturn(Optional.of(demande));
            when(paymentRepository.findByDemandeId(10L)).thenReturn(Optional.empty());
            when(stripeService.createCheckoutSessionAndReturnSession(eq(1500), anyString(), anyString(), eq(10L)))
                    .thenReturn(stripeSession);
            when(paymentRepository.save(any(Payment.class))).thenAnswer(inv -> inv.getArgument(0));

            paymentService.createSessionForDemande(10L);

            verify(stripeService).createCheckoutSessionAndReturnSession(1500, "http://localhost:5173/success", "http://localhost:5173/cancel", 10L);
        }
    }

    @Nested
    @DisplayName("confirmSessionBySessionId")
    class ConfirmSessionBySessionId {

        @Test
        void retourne_false_quand_sessionId_null() {
            assertThat(paymentService.confirmSessionBySessionId(null)).isFalse();
        }

        @Test
        void retourne_false_quand_sessionId_vide() {
            assertThat(paymentService.confirmSessionBySessionId("")).isFalse();
        }

        @Test
        void retourne_false_quand_payment_inexistant() {
            when(paymentRepository.findByStripeSessionId("cs_unknown")).thenReturn(Optional.empty());
            assertThat(paymentService.confirmSessionBySessionId("cs_unknown")).isFalse();
        }

        @Test
        void retourne_true_quand_payment_deja_paye() {
            Payment payment = new Payment();
            payment.setStripeSessionId("cs_test_123");
            payment.setStatus(Payment.PaymentStatus.PAID);
            when(paymentRepository.findByStripeSessionId("cs_test_123")).thenReturn(Optional.of(payment));

            assertThat(paymentService.confirmSessionBySessionId("cs_test_123")).isTrue();
            verify(demandeRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("handleWebhook")
    class HandleWebhook {

        @BeforeEach
        void setWebhookSecret() {
            ReflectionTestUtils.setField(paymentService, "webhookSecret", "");
        }

        @Test
        void throw_quand_webhook_secret_non_configure() {
            assertThatThrownBy(() -> paymentService.handleWebhook("{}", "sig"))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("webhook secret non configuré");
        }

        @Test
        void throw_quand_signature_invalide() {
            ReflectionTestUtils.setField(paymentService, "webhookSecret", "whsec_testSecret");

            assertThatThrownBy(() -> paymentService.handleWebhook("{}", "invalid_signature"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Signature");
        }

        @Test
        void evenement_inconnu_retourne_sans_erreur_ni_save() {
            ReflectionTestUtils.setField(paymentService, "webhookSecret", "whsec_ok");
            Event event = mock(Event.class);
            when(event.getType()).thenReturn("customer.created");

            try (MockedStatic<Webhook> mockedWebhook = mockStatic(Webhook.class)) {
                mockedWebhook.when(() -> Webhook.constructEvent(anyString(), anyString(), anyString())).thenReturn(event);

                paymentService.handleWebhook("{}", "sig");

                verify(paymentRepository, never()).save(any());
                verify(demandeRepository, never()).save(any());
            }
        }

        @Test
        void deja_traite_idempotence_ne_sauvegarde_pas() {
            ReflectionTestUtils.setField(paymentService, "webhookSecret", "whsec_ok");
            Event event = mock(Event.class);
            when(event.getType()).thenReturn("checkout.session.completed");
            EventDataObjectDeserializer deserializer = mock(EventDataObjectDeserializer.class);
            when(event.getDataObjectDeserializer()).thenReturn(deserializer);
            Session session = mock(Session.class);
            when(session.getId()).thenReturn("cs_test_123");
            when(deserializer.getObject()).thenReturn(Optional.of(session));

            Payment payment = new Payment();
            payment.setId(1L);
            payment.setStripeSessionId("cs_test_123");
            payment.setStatus(Payment.PaymentStatus.PAID);
            payment.setDemande(demande);
            when(paymentRepository.findByStripeSessionId("cs_test_123")).thenReturn(Optional.of(payment));

            try (MockedStatic<Webhook> mockedWebhook = mockStatic(Webhook.class)) {
                mockedWebhook.when(() -> Webhook.constructEvent(anyString(), anyString(), anyString())).thenReturn(event);

                paymentService.handleWebhook("{}", "sig");

                verify(paymentRepository, never()).save(any());
                verify(demandeRepository, never()).save(any());
            }
        }

        @Test
        void throw_quand_payment_non_trouve_pour_session() {
            ReflectionTestUtils.setField(paymentService, "webhookSecret", "whsec_ok");
            Event event = mock(Event.class);
            when(event.getType()).thenReturn("checkout.session.completed");
            EventDataObjectDeserializer deserializer = mock(EventDataObjectDeserializer.class);
            when(event.getDataObjectDeserializer()).thenReturn(deserializer);
            Session session = mock(Session.class);
            when(session.getId()).thenReturn("cs_unknown");
            when(deserializer.getObject()).thenReturn(Optional.of(session));
            when(paymentRepository.findByStripeSessionId("cs_unknown")).thenReturn(Optional.empty());

            try (MockedStatic<Webhook> mockedWebhook = mockStatic(Webhook.class)) {
                mockedWebhook.when(() -> Webhook.constructEvent(anyString(), anyString(), anyString())).thenReturn(event);

                assertThatThrownBy(() -> paymentService.handleWebhook("{}", "sig"))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("Payment non trouvé");
            }
        }

        @Test
        void met_a_jour_payment_et_demande_quand_session_completed() {
            ReflectionTestUtils.setField(paymentService, "webhookSecret", "whsec_ok");
            Event event = mock(Event.class);
            when(event.getType()).thenReturn("checkout.session.completed");
            EventDataObjectDeserializer deserializer = mock(EventDataObjectDeserializer.class);
            when(event.getDataObjectDeserializer()).thenReturn(deserializer);
            Session session = mock(Session.class);
            when(session.getId()).thenReturn("cs_test_123");
            when(deserializer.getObject()).thenReturn(Optional.of(session));

            Payment payment = new Payment();
            payment.setId(1L);
            payment.setStripeSessionId("cs_test_123");
            payment.setStatus(Payment.PaymentStatus.PENDING);
            payment.setDemande(demande);
            when(paymentRepository.findByStripeSessionId("cs_test_123")).thenReturn(Optional.of(payment));
            when(paymentRepository.save(any(Payment.class))).thenAnswer(inv -> inv.getArgument(0));
            when(demandeRepository.save(any(Demande.class))).thenAnswer(inv -> inv.getArgument(0));

            try (MockedStatic<Webhook> mockedWebhook = mockStatic(Webhook.class)) {
                mockedWebhook.when(() -> Webhook.constructEvent(anyString(), anyString(), anyString())).thenReturn(event);

                paymentService.handleWebhook("{}", "sig");

                verify(paymentRepository).save(argThat(p -> p.getStatus() == Payment.PaymentStatus.PAID));
                verify(demandeRepository).save(argThat(d -> d.getStatus() == Demande.Status.PENDING));
            }
        }
    }
}
