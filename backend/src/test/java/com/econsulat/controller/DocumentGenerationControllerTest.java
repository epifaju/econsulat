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

@WebMvcTest(controllers = DocumentGenerationController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("DocumentGenerationController")
class DocumentGenerationControllerTest {

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
        u.setEmail("admin@test.com");
        return u;
    }

    private static GeneratedDocument generatedDocument() {
        GeneratedDocument doc = new GeneratedDocument();
        doc.setId(1L);
        doc.setFileName("doc.docx");
        doc.setDemande(new Demande());
        doc.getDemande().setId(10L);
        doc.setDocumentType(new DocumentType());
        doc.getDocumentType().setId(2L);
        doc.setCreatedBy(user());
        return doc;
    }

    @Nested
    @DisplayName("POST /api/admin/documents/generate")
    class GenerateDocument {

        @Test
        @WithMockUser(roles = "ADMIN", username = "admin@test.com")
        void retourne_200_et_reponse_quand_succes() throws Exception {
            when(userRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(user()));
            when(documentGenerationService.generateDocument(eq(10L), eq(2L), any(User.class)))
                    .thenReturn(generatedDocument());

            mvc.perform(post("/api/admin/documents/generate")
                            .with(csrf())
                            .param("demandeId", "10")
                            .param("documentTypeId", "2"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.fileName").value("doc.docx"));
        }
    }

    @Nested
    @DisplayName("GET /api/admin/documents/download/{documentId}")
    class DownloadDocument {

        @Test
        @WithMockUser(roles = "ADMIN")
        void retourne_200_quand_succes() throws Exception {
            when(documentGenerationService.downloadDocument(1L)).thenReturn(new byte[]{1, 2, 3});

            mvc.perform(get("/api/admin/documents/download/1"))
                    .andExpect(status().isOk());
        }
    }
}
