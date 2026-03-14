package com.econsulat.controller;

import com.econsulat.model.Citizen;
import com.econsulat.model.User;
import com.econsulat.service.CitizenService;
import com.econsulat.service.JwtService;
import com.econsulat.service.UserService;
import com.econsulat.storage.StorageService;
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

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = CitizenController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("CitizenController")
class CitizenControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CitizenService citizenService;

    @MockBean
    private UserService userService;

    @MockBean
    private StorageService storageService;

    @MockBean
    private JwtService jwtService;

    private static Citizen citizen(long id) {
        Citizen c = new Citizen();
        c.setId(id);
        c.setFirstName("Jean");
        c.setLastName("Dupont");
        return c;
    }

    @Nested
    @DisplayName("GET /api/citizens")
    class GetAllCitizens {

        @Test
        @WithMockUser(roles = "ADMIN")
        void retourne_200_et_liste() throws Exception {
            when(citizenService.getAllCitizens()).thenReturn(List.of(citizen(1L), citizen(2L)));

            mvc.perform(get("/api/citizens"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2));
        }
    }

    @Nested
    @DisplayName("GET /api/citizens/my-requests")
    class GetMyRequests {

        @Test
        @WithMockUser(username = "user@test.com")
        void retourne_200_et_liste_citoyens() throws Exception {
            User user = new User();
            user.setId(1L);
            user.setEmail("user@test.com");
            when(userService.findByEmail("user@test.com")).thenReturn(Optional.of(user));
            when(citizenService.getCitizensByUser(1L)).thenReturn(List.of(citizen(1L)));

            mvc.perform(get("/api/citizens/my-requests"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1));
        }

        @Test
        @WithMockUser(username = "unknown@test.com")
        void retourne_404_quand_utilisateur_absent() throws Exception {
            when(userService.findByEmail("unknown@test.com")).thenReturn(Optional.empty());

            mvc.perform(get("/api/citizens/my-requests"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GET /api/citizens/{id}")
    class GetCitizenById {

        @Test
        @WithMockUser(roles = "ADMIN")
        void retourne_200_quand_trouve() throws Exception {
            when(citizenService.getCitizenById(1L)).thenReturn(Optional.of(citizen(1L)));

            mvc.perform(get("/api/citizens/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void retourne_404_quand_absent() throws Exception {
            when(citizenService.getCitizenById(99L)).thenReturn(Optional.empty());

            mvc.perform(get("/api/citizens/99"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GET /api/citizens/files/{filename}")
    class GetFile {

        @Test
        @WithMockUser(roles = "ADMIN")
        void retourne_404_quand_fichier_inexistant() throws Exception {
            when(storageService.citizenFileExists("missing.pdf")).thenReturn(false);

            mvc.perform(get("/api/citizens/files/missing.pdf"))
                    .andExpect(status().isNotFound());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void retourne_200_et_contenu_quand_fichier_existe() throws Exception {
            when(storageService.citizenFileExists("doc.pdf")).thenReturn(true);
            when(storageService.readCitizenFile("doc.pdf")).thenReturn(new byte[]{1, 2, 3});

            mvc.perform(get("/api/citizens/files/doc.pdf"))
                    .andExpect(status().isOk())
                    .andExpect(header().exists("Content-Disposition"));
        }
    }
}
