package com.econsulat.service;

import com.econsulat.model.Demande;
import com.econsulat.model.Payment;
import com.econsulat.repository.DemandeRepository;
import com.econsulat.repository.PaymentRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class PaymentService {

    private final StripeService stripeService;
    private final PaymentRepository paymentRepository;
    private final DemandeRepository demandeRepository;

    @Value("${stripe.secret-key}")
    private String secretKey;

    @Value("${stripe.webhook-secret}")
    private String webhookSecret;

    @Value("${stripe.default-amount-cents:1000}")
    private int defaultAmountCents;

    @Value("${app.payment-success-url:http://localhost:5173/payment/success}")
    private String successUrl;

    @Value("${app.payment-cancel-url:http://localhost:5173/payment/cancel}")
    private String cancelUrl;

    private final EmailNotificationService emailNotificationService;

    public PaymentService(StripeService stripeService,
                          PaymentRepository paymentRepository,
                          DemandeRepository demandeRepository,
                          EmailNotificationService emailNotificationService) {
        this.stripeService = stripeService;
        this.paymentRepository = paymentRepository;
        this.demandeRepository = demandeRepository;
        this.emailNotificationService = emailNotificationService;
    }

    /**
     * Crée une session Stripe Checkout pour une demande en PENDING_PAYMENT.
     * Enregistre un Payment en PENDING puis redirige vers Stripe.
     */
    @Transactional
    public String createSessionForDemande(Long demandeId) {
        Demande demande = demandeRepository.findById(demandeId)
                .orElseThrow(() -> new IllegalArgumentException("Demande non trouvée: " + demandeId));

        if (demande.getStatus() != Demande.Status.PENDING_PAYMENT) {
            throw new IllegalStateException("Cette demande n'est pas en attente de paiement (statut: " + demande.getStatus() + ")");
        }

        Optional<Payment> existing = paymentRepository.findByDemandeId(demandeId);

        int amountCents = defaultAmountCents;
        if (demande.getDocumentType() != null && demande.getDocumentType().getPriceCents() != null) {
            amountCents = demande.getDocumentType().getPriceCents();
        }

        Session session;
        try {
            session = stripeService.createCheckoutSessionAndReturnSession(amountCents, successUrl, cancelUrl, demandeId);
        } catch (Exception e) {
            throw new RuntimeException("Erreur Stripe: " + e.getMessage(), e);
        }
        if (session == null || session.getUrl() == null) {
            throw new IllegalStateException("Impossible de créer la session Stripe");
        }

        Payment payment = existing.orElse(new Payment());
        payment.setDemande(demande);
        payment.setStripeSessionId(session.getId());
        payment.setAmountCents(amountCents);
        payment.setCurrency("eur");
        payment.setStatus(Payment.PaymentStatus.PENDING);
        payment.setCreatedAt(LocalDateTime.now());
        paymentRepository.save(payment);

        return session.getUrl();
    }

    /**
     * Traite le webhook Stripe (checkout.session.completed).
     * Vérifie la signature, puis met à jour Payment et Demande.
     */
    @Transactional
    public void handleWebhook(String payload, String stripeSignature) {
        if (webhookSecret == null || webhookSecret.isBlank() || webhookSecret.equals("whsec_placeholder")) {
            throw new IllegalStateException("Stripe webhook secret non configuré");
        }

        Event event;
        try {
            event = Webhook.constructEvent(payload, stripeSignature, webhookSecret);
        } catch (SignatureVerificationException e) {
            throw new IllegalArgumentException("Signature webhook Stripe invalide", e);
        } catch (Exception e) {
            throw new IllegalArgumentException("Payload webhook invalide", e);
        }

        if (!"checkout.session.completed".equals(event.getType())) {
            return;
        }

        var deserializer = event.getDataObjectDeserializer();
        Object obj = deserializer.getObject().orElse(null);
        if (!(obj instanceof Session)) {
            return;
        }
        Session session = (Session) obj;

        String stripeSessionId = session.getId();
        Payment payment = paymentRepository.findByStripeSessionId(stripeSessionId)
                .orElseThrow(() -> new IllegalArgumentException("Payment non trouvé pour session: " + stripeSessionId));

        if (payment.getStatus() == Payment.PaymentStatus.PAID) {
            return; // Déjà traité (idempotence)
        }

        payment.setStatus(Payment.PaymentStatus.PAID);
        payment.setPaidAt(LocalDateTime.now());
        paymentRepository.save(payment);

        Demande demande = payment.getDemande();
        demande.setStatus(Demande.Status.PENDING);
        demandeRepository.save(demande);

        try {
            emailNotificationService.sendPaymentSuccessNotification(payment);
        } catch (Exception e) {
            org.slf4j.LoggerFactory.getLogger(PaymentService.class).warn("Notification paiement non envoyée pour demande {} : {}", demande.getId(), e.getMessage());
        }
    }

    /**
     * Confirme le paiement à partir de l'ID de session Stripe (appelé depuis la page de succès).
     * Utilisé si le webhook n'a pas été reçu (ex. Stripe CLI arrêtée). Vérifie le statut
     * auprès de Stripe puis met à jour Payment et Demande.
     */
    @Transactional
    public boolean confirmSessionBySessionId(String stripeSessionId) {
        if (stripeSessionId == null || stripeSessionId.isBlank()) {
            return false;
        }
        Optional<Payment> paymentOpt = paymentRepository.findByStripeSessionId(stripeSessionId);
        if (paymentOpt.isEmpty()) {
            return false;
        }
        Payment payment = paymentOpt.get();
        if (payment.getStatus() == Payment.PaymentStatus.PAID) {
            return true; // Déjà traité
        }
        if (secretKey == null || secretKey.isBlank() || secretKey.startsWith("sk_test_placeholder")) {
            return false;
        }
        try {
            Stripe.apiKey = secretKey;
            Session session = Session.retrieve(stripeSessionId);
            if (session == null || !"paid".equalsIgnoreCase(session.getPaymentStatus())) {
                return false;
            }
            payment.setStatus(Payment.PaymentStatus.PAID);
            payment.setPaidAt(LocalDateTime.now());
            paymentRepository.save(payment);
            Demande demande = payment.getDemande();
            demande.setStatus(Demande.Status.PENDING);
            demandeRepository.save(demande);
            try {
                emailNotificationService.sendPaymentSuccessNotification(payment);
            } catch (Exception notifEx) {
                org.slf4j.LoggerFactory.getLogger(PaymentService.class).warn("Notification paiement non envoyée pour demande {} : {}", demande.getId(), notifEx.getMessage());
            }
            return true;
        } catch (StripeException e) {
            return false;
        }
    }
}
