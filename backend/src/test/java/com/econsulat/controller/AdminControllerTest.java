package com.econsulat.controller;

import com.econsulat.dto.DemandeAdminResponse;
import com.econsulat.dto.DemandeStatusResponse;
import com.econsulat.dto.UserAdminResponse;
import com.econsulat.model.Demande;
import com.econsulat.model.DocumentType;
import com.econsulat.model.User;
import com.econsulat.service.AdminService;
import com.econsulat.service.JwtService;
import com.econsulat.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AdminController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("AdminController")
class AdminControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AdminService adminService;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtService jwtService;

    @Nested
    @DisplayName("GET /api/admin/demandes")
    class GetAllDemandes {

        @Test
        @WithMockUser(roles = "ADMIN")
        void retourne_200_et_page() throws Exception {
            DemandeAdminResponse d = new DemandeAdminResponse();
            d.setId(1L);
            when(adminService.getAllDemandes(any())).thenReturn(new PageImpl<>(List.of(d), PageRequest.of(0, 5), 1));

            mvc.perform(get("/api/admin/demandes").param("page", "0").param("size", "5"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content.length()").value(1))
                    .andExpect(jsonPath("$.content[0].id").value(1));
        }
    }

    @Nested
    @DisplayName("GET /api/admin/demandes/{id}")
    class GetDemandeById {

        @Test
        @WithMockUser(roles = "ADMIN")
        void retourne_200_et_demande() throws Exception {
            DemandeAdminResponse d = new DemandeAdminResponse();
            d.setId(1L);
            when(adminService.getDemandeById(1L)).thenReturn(d);

            mvc.perform(get("/api/admin/demandes/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1));
        }
    }

    @Nested
    @DisplayName("GET /api/admin/demandes/search")
    class SearchDemandes {

        @Test
        @WithMockUser(roles = "ADMIN")
        void retourne_200_et_page() throws Exception {
            DemandeAdminResponse d = new DemandeAdminResponse();
            d.setId(1L);
            when(adminService.searchDemandes(eq("Dupont"), any())).thenReturn(new PageImpl<>(List.of(d), PageRequest.of(0, 5), 1));

            mvc.perform(get("/api/admin/demandes/search").param("q", "Dupont").param("page", "0").param("size", "5"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content.length()").value(1));
            verify(adminService).searchDemandes(eq("Dupont"), any());
        }
    }

    @Nested
    @DisplayName("PUT /api/admin/demandes/{id}")
    class UpdateDemande {

        @Test
        @WithMockUser(roles = "ADMIN")
        void retourne_200_et_demande_mise_a_jour() throws Exception {
            DemandeAdminResponse d = new DemandeAdminResponse();
            d.setId(1L);
            d.setFirstName("Jean");
            when(adminService.updateDemande(eq(1L), any(com.econsulat.dto.DemandeRequest.class))).thenReturn(d);

            String body = "{\"civiliteId\":1,\"firstName\":\"Jean\",\"lastName\":\"Dupont\",\"birthDate\":\"1990-01-01\","
                    + "\"birthPlace\":\"Paris\",\"birthCountryId\":1,\"countryId\":1,\"streetName\":\"Rue\",\"streetNumber\":\"1\","
                    + "\"postalCode\":\"75000\",\"city\":\"Paris\",\"documentTypeId\":1,\"fatherBirthCountryId\":1,"
                    + "\"motherBirthCountryId\":1,\"documentFiles\":[]}";
            mvc.perform(put("/api/admin/demandes/1").with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.firstName").value("Jean"));
        }
    }

    @Nested
    @DisplayName("GET /api/admin/users/search")
    class SearchUsers {

        @Test
        @WithMockUser(roles = "ADMIN")
        void retourne_200_et_page() throws Exception {
            UserAdminResponse u = new UserAdminResponse();
            u.setId(1L);
            u.setEmail("user@test.com");
            when(adminService.searchUsers(eq("user"), any())).thenReturn(new PageImpl<>(List.of(u), PageRequest.of(0, 10), 1));

            mvc.perform(get("/api/admin/users/search").param("q", "user").param("page", "0").param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content.length()").value(1))
                    .andExpect(jsonPath("$.content[0].email").value("user@test.com"));
        }
    }

    @Nested
    @DisplayName("PUT /api/admin/demandes/{id}/status")
    class UpdateDemandeStatus {

        @Test
        @WithMockUser(roles = "ADMIN")
        void retourne_200_et_reponse_statut() throws Exception {
            Demande demande = new Demande();
            demande.setId(1L);
            demande.setFirstName("Jean");
            demande.setLastName("Dupont");
            demande.setStatus(Demande.Status.APPROVED);
            demande.setUpdatedAt(LocalDateTime.now());
            DocumentType dt = new DocumentType();
            dt.setLibelle("Acte de naissance");
            demande.setDocumentType(dt);
            when(adminService.updateDemandeStatus(1L, "APPROVED")).thenReturn(demande);

            mvc.perform(put("/api/admin/demandes/1/status").param("status", "APPROVED").with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.status").value("APPROVED"));
        }
    }

    @Nested
    @DisplayName("DELETE /api/admin/demandes/{id}")
    class DeleteDemande {

        @Test
        @WithMockUser(roles = "ADMIN")
        void retourne_200_et_message() throws Exception {
            mvc.perform(delete("/api/admin/demandes/1").with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Demande supprimée avec succès"));
            verify(adminService).deleteDemande(1L);
        }
    }

    @Nested
    @DisplayName("GET /api/admin/users")
    class GetAllUsers {

        @Test
        @WithMockUser(roles = "ADMIN")
        void retourne_200_et_page() throws Exception {
            UserAdminResponse u = new UserAdminResponse();
            u.setId(1L);
            u.setEmail("admin@test.com");
            when(adminService.getAllUsers(any())).thenReturn(new PageImpl<>(List.of(u), PageRequest.of(0, 10), 1));

            mvc.perform(get("/api/admin/users").param("page", "0").param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content.length()").value(1))
                    .andExpect(jsonPath("$.content[0].email").value("admin@test.com"));
        }
    }

    @Nested
    @DisplayName("GET /api/admin/users/{id}")
    class GetUserById {

        @Test
        @WithMockUser(roles = "ADMIN")
        void retourne_200_et_utilisateur() throws Exception {
            UserAdminResponse u = new UserAdminResponse();
            u.setId(1L);
            u.setEmail("user@test.com");
            when(adminService.getUserById(1L)).thenReturn(u);

            mvc.perform(get("/api/admin/users/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1));
        }
    }

    @Nested
    @DisplayName("GET /api/admin/stats")
    class GetStats {

        @Test
        @WithMockUser(roles = "ADMIN")
        void retourne_200_et_statistiques() throws Exception {
            when(adminService.getTotalDemandes()).thenReturn(10L);
            when(adminService.getDemandesByStatus("PENDING")).thenReturn(3L);
            when(adminService.getDemandesByStatus("APPROVED")).thenReturn(2L);
            when(adminService.getDemandesByStatus("REJECTED")).thenReturn(1L);
            when(adminService.getDemandesByStatus("COMPLETED")).thenReturn(4L);
            when(adminService.getTotalUsers()).thenReturn(5L);
            when(adminService.getTotalGeneratedDocuments()).thenReturn(8L);

            mvc.perform(get("/api/admin/stats"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalDemandes").value(10))
                    .andExpect(jsonPath("$.totalUsers").value(5))
                    .andExpect(jsonPath("$.totalGeneratedDocuments").value(8));
        }
    }
}
