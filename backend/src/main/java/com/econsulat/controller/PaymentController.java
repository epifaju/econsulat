package com.econsulat.controller;

import com.econsulat.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin(origins = "*")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    /**
     * Crée une session Stripe Checkout pour une demande.
     * Retourne l'URL de redirection vers Stripe.
     * Authentification requise.
     */
    @PostMapping("/create-session")
    public ResponseEntity<?> createSession(@RequestBody Map<String, Long> body) {
        Long demandeId = body != null ? body.get("demandeId") : null;
        if (demandeId == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "demandeId requis"));
        }
        try {
            String url = paymentService.createSessionForDemande(demandeId);
            return ResponseEntity.ok(Map.of("url", url));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Erreur lors de la création de la session: " + e.getMessage()));
        }
    }

    /**
     * Confirme le paiement à partir du session_id (page de succès).
     * Appelé par le frontend au chargement de /payment/success?session_id=xxx pour mettre à jour
     * la demande si le webhook n'a pas été reçu. Pas d'auth requise (session_id est un token unique).
     */
    @GetMapping("/confirm-session")
    public ResponseEntity<?> confirmSession(@RequestParam("session_id") String sessionId) {
        if (sessionId == null || sessionId.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "session_id requis"));
        }
        try {
            boolean confirmed = paymentService.confirmSessionBySessionId(sessionId);
            return ResponseEntity.ok(Map.of("confirmed", confirmed));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Webhook Stripe - doit recevoir le body brut (non parsé).
     * Pas d'authentification JWT (Stripe envoie la requête).
     */
    @PostMapping("/webhook")
    public ResponseEntity<String> webhook(
            @RequestBody String payload,
            @RequestHeader(value = "Stripe-Signature", required = false) String stripeSignature) {
        if (stripeSignature == null || stripeSignature.isBlank()) {
            return ResponseEntity.badRequest().body("Stripe-Signature manquant");
        }
        try {
            paymentService.handleWebhook(payload, stripeSignature);
            return ResponseEntity.ok("OK");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(500).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erreur: " + e.getMessage());
        }
    }
}
