package com.econsulat.controller;

import com.econsulat.dto.CitizenHistoryDTO;
import com.econsulat.service.CitizenHistoryService;
import com.econsulat.service.JwtService;
import com.econsulat.service.UserService;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = MeController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("MeController")
class MeControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private CitizenHistoryService citizenHistoryService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserService userService;

    @Nested
    @DisplayName("GET /api/me/history")
    class GetMyHistory {

        @Test
        @WithMockUser(username = "jean@test.com")
        @DisplayName("retourne 200 et le dossier citoyen de l'utilisateur connecté")
        void retourne_200_et_historique() throws Exception {
            CitizenHistoryDTO dto = new CitizenHistoryDTO();
            dto.setUserFirstName("Jean");
            dto.setUserLastName("Dupont");
            dto.setUserEmail("jean@test.com");
            dto.setTotalDemandes(2);
            dto.setTotalPaidCents(5000L);
            dto.setTotalPaidEuros("50.00");
            dto.setDemandes(List.of());

            when(citizenHistoryService.getHistoryByEmail(eq("jean@test.com"))).thenReturn(dto);

            mvc.perform(get("/api/me/history"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.userEmail").value("jean@test.com"))
                    .andExpect(jsonPath("$.userFirstName").value("Jean"))
                    .andExpect(jsonPath("$.userLastName").value("Dupont"))
                    .andExpect(jsonPath("$.totalDemandes").value(2))
                    .andExpect(jsonPath("$.totalPaidCents").value(5000))
                    .andExpect(jsonPath("$.totalPaidEuros").value("50.00"))
                    .andExpect(jsonPath("$.demandes").isArray());
        }

        @Test
        @WithMockUser(username = "autre@test.com")
        @DisplayName("retourne le DTO pour un autre utilisateur connecté")
        void retourne_historique_autre_utilisateur() throws Exception {
            CitizenHistoryDTO dto = new CitizenHistoryDTO();
            dto.setUserEmail("autre@test.com");
            dto.setTotalDemandes(0);
            dto.setDemandes(List.of());
            when(citizenHistoryService.getHistoryByEmail(eq("autre@test.com"))).thenReturn(dto);

            mvc.perform(get("/api/me/history"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.userEmail").value("autre@test.com"));
        }
    }
}
