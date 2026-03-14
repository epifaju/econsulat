package com.econsulat.controller;

import com.econsulat.service.JwtService;
import com.econsulat.service.PaymentService;
import com.econsulat.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = PaymentController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("PaymentController")
class PaymentControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PaymentService paymentService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserService userService;

    @Nested
    @DisplayName("POST /api/payment/create-session")
    class CreateSession {

        @Test
        @WithMockUser
        void retourne_400_quand_demandeId_manquant() throws Exception {
            mvc.perform(post("/api/payment/create-session")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("demandeId requis"));
        }

        @Test
        @WithMockUser
        void retourne_erreur_quand_body_absent() throws Exception {
            mvc.perform(post("/api/payment/create-session")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().is5xxServerError());
            verify(paymentService, never()).createSessionForDemande(anyLong());
        }

        @Test
        @WithMockUser
        void retourne_200_et_url_quand_succes() throws Exception {
            when(paymentService.createSessionForDemande(1L)).thenReturn("https://checkout.stripe.com/session/xxx");

            mvc.perform(post("/api/payment/create-session")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(Map.of("demandeId", 1L))))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.url").value("https://checkout.stripe.com/session/xxx"));
            verify(paymentService).createSessionForDemande(1L);
        }

        @Test
        @WithMockUser
        void retourne_400_quand_demande_invalide() throws Exception {
            when(paymentService.createSessionForDemande(999L))
                    .thenThrow(new IllegalArgumentException("Demande non trouvée"));

            mvc.perform(post("/api/payment/create-session")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(Map.of("demandeId", 999L))))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("Demande non trouvée"));
        }
    }

    @Nested
    @DisplayName("GET /api/payment/confirm-session")
    class ConfirmSession {

        @Test
        void retourne_400_quand_session_id_manquant() throws Exception {
            // session_id vide : le contrôleur renvoie 400 (param absent → 500 via GlobalExceptionHandler)
            mvc.perform(get("/api/payment/confirm-session").param("session_id", ""))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("session_id requis"));
        }

        @Test
        void retourne_200_et_confirmed_quand_succes() throws Exception {
            when(paymentService.confirmSessionBySessionId("cs_xxx")).thenReturn(true);

            mvc.perform(get("/api/payment/confirm-session").param("session_id", "cs_xxx"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.confirmed").value(true));
            verify(paymentService).confirmSessionBySessionId("cs_xxx");
        }
    }

    @Nested
    @DisplayName("POST /api/payment/webhook")
    class Webhook {

        @Test
        void retourne_400_quand_stripe_signature_manquant() throws Exception {
            mvc.perform(post("/api/payment/webhook")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(org.hamcrest.Matchers.containsString("Stripe-Signature")));
        }

        @Test
        void retourne_200_quand_succes() throws Exception {
            mvc.perform(post("/api/payment/webhook")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Stripe-Signature", "v1,signature")
                            .content("{\"type\":\"checkout.session.completed\"}"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("OK"));
            verify(paymentService).handleWebhook(anyString(), anyString());
        }
    }
}
