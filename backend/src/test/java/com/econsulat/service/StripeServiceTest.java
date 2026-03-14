package com.econsulat.service;

import com.stripe.exception.StripeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
@DisplayName("StripeService")
class StripeServiceTest {

    private StripeService stripeService;

    @BeforeEach
    void setUp() {
        stripeService = new StripeService();
    }

    @Nested
    @DisplayName("init")
    class Init {

        @Test
        void ne_throw_pas_quand_secret_placeholder() {
            ReflectionTestUtils.setField(stripeService, "secretKey", "sk_test_placeholder");
            stripeService.init();
        }

        @Test
        void ne_throw_pas_quand_secret_vide() {
            ReflectionTestUtils.setField(stripeService, "secretKey", "");
            stripeService.init();
        }
    }

    @Nested
    @DisplayName("createCheckoutSessionAndReturnSession")
    class CreateCheckoutSession {

        @Test
        void throw_quand_secret_key_non_configure() throws StripeException {
            ReflectionTestUtils.setField(stripeService, "secretKey", "");

            assertThatThrownBy(() -> stripeService.createCheckoutSessionAndReturnSession(
                    1000, "http://success", "http://cancel", 1L))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Stripe n'est pas configuré");
        }

        @Test
        void throw_quand_secret_key_placeholder() throws StripeException {
            ReflectionTestUtils.setField(stripeService, "secretKey", "sk_test_placeholder");

            assertThatThrownBy(() -> stripeService.createCheckoutSessionAndReturnSession(
                    1000, "http://success", "http://cancel", 1L))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Stripe n'est pas configuré");
        }
    }
}
