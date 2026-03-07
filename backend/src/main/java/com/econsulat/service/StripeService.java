package com.econsulat.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.param.checkout.SessionCreateParams;
import com.stripe.model.checkout.Session;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StripeService {

    @Value("${stripe.secret-key}")
    private String secretKey;

    @Value("${stripe.currency:eur}")
    private String currency;

    @PostConstruct
    public void init() {
        if (secretKey != null && !secretKey.isBlank() && !secretKey.startsWith("sk_test_placeholder")) {
            Stripe.apiKey = secretKey;
        }
    }

    /**
     * Crée une session Stripe Checkout pour un montant donné et retourne la session (id + url).
     */
    public Session createCheckoutSessionAndReturnSession(int amountCents, String successUrl, String cancelUrl, Long demandeId) throws StripeException {
        if (secretKey == null || secretKey.isBlank() || secretKey.startsWith("sk_test_placeholder")) {
            throw new IllegalStateException("Stripe n'est pas configuré (stripe.secret-key manquant ou invalide)");
        }

        Stripe.apiKey = secretKey;

        SessionCreateParams.LineItem.PriceData.ProductData productData = SessionCreateParams.LineItem.PriceData.ProductData.builder()
                .setName("Demande de document - eConsulat")
                .build();

        SessionCreateParams.LineItem.PriceData priceData = SessionCreateParams.LineItem.PriceData.builder()
                .setCurrency(currency)
                .setUnitAmount((long) amountCents)
                .setProductData(productData)
                .build();

        SessionCreateParams.LineItem lineItem = SessionCreateParams.LineItem.builder()
                .setQuantity(1L)
                .setPriceData(priceData)
                .build();

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .addLineItem(lineItem)
                .setSuccessUrl(successUrl + "?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl(cancelUrl)
                .putMetadata("demande_id", String.valueOf(demandeId))
                .build();

        return Session.create(params);
    }
}
