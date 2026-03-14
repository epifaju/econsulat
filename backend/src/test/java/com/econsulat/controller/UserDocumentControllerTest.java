package com.econsulat.controller;

import com.econsulat.model.Demande;
import com.econsulat.model.DocumentType;
import com.econsulat.model.GeneratedDocument;
import com.econsulat.model.User;
import com.econsulat.repository.UserRepository;
import com.econsulat.service.DocumentGenerationService;
import com.econsulat.service.JwtService;
import com.econsulat.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserDocumentController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("UserDocumentController")
class UserDocumentControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private DocumentGenerationService documentGenerationService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserService userService;

    private static User user() {
        User u = new User();
        u.setId(1L);
        u.setEmail("user@test.com");
        return u;
    }

    private static GeneratedDocument generatedDocument() {
        GeneratedDocument doc = new GeneratedDocument();
        doc.setId(1L);
        doc.setFileName("doc.docx");
        doc.setStatus("GENERATED");
        doc.setDemande(new Demande());
        doc.getDemande().setId(10L);
        doc.setDocumentType(new DocumentType());
        doc.getDocumentType().setId(2L);
        doc.setCreatedBy(user());
        return doc;
    }

    @Nested
    @DisplayName("POST /api/user/documents/generate")
    class GenerateDocument {

        @Test
        @WithMockUser(username = "user@test.com")
        void retourne_200_et_reponse_quand_succes() throws Exception {
            when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user()));
            when(documentGenerationService.generateDocument(eq(10L), eq(2L), any(User.class)))
                    .thenReturn(generatedDocument());

            mvc.perform(post("/api/user/documents/generate")
                            .with(csrf())
                            .param("demandeId", "10")
                            .param("documentTypeId", "2"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.fileName").value("doc.docx"));
        }

        @Test
        @WithMockUser(username = "user@test.com")
        void retourne_400_quand_erreur_metier() throws Exception {
            when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user()));
            when(documentGenerationService.generateDocument(anyLong(), anyLong(), any()))
                    .thenThrow(new RuntimeException("Demande non trouvée"));

            mvc.perform(post("/api/user/documents/generate")
                            .with(csrf())
                            .param("demandeId", "99")
                            .param("documentTypeId", "1"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").exists());
        }
    }

    @Nested
    @DisplayName("GET /api/user/documents/download/{documentId}")
    class DownloadDocument {

        @Test
        @WithMockUser(username = "user@test.com")
        void retourne_200_et_fichier_quand_succes() throws Exception {
            when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user()));
            when(documentGenerationService.downloadDocument(1L)).thenReturn(new byte[]{1, 2, 3});

            mvc.perform(get("/api/user/documents/download/1"))
                    .andExpect(status().isOk())
                    .andExpect(header().string("Content-Disposition", org.hamcrest.Matchers.containsString("document.docx")));
        }
    }
}
