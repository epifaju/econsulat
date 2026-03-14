package com.econsulat.controller;

import com.econsulat.dto.DocumentTypeRequest;
import com.econsulat.model.DocumentType;
import com.econsulat.service.DocumentTypeService;
import com.econsulat.service.JwtService;
import com.econsulat.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = DocumentTypeController.class)
@DisplayName("DocumentTypeController")
class DocumentTypeControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DocumentTypeService documentTypeService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserService userService;

    private static DocumentType docType(long id, String libelle) {
        DocumentType dt = new DocumentType();
        dt.setId(id);
        dt.setLibelle(libelle);
        dt.setIsActive(true);
        return dt;
    }

    @Nested
    @DisplayName("GET /api/admin/document-types")
    class GetAllDocumentTypes {

        @Test
        @WithMockUser(roles = "ADMIN")
        void retourne_200_et_liste() throws Exception {
            when(documentTypeService.getAllDocumentTypes())
                    .thenReturn(List.of(docType(1L, "Acte de naissance"), docType(2L, "Certificat de mariage")));

            mvc.perform(get("/api/admin/document-types"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].libelle").value("Acte de naissance"));
        }
    }

    @Nested
    @DisplayName("GET /api/admin/document-types/search")
    class SearchDocumentTypes {

        @Test
        @WithMockUser(roles = "ADMIN")
        void retourne_200_et_resultats_recherche() throws Exception {
            when(documentTypeService.searchDocumentTypes("acte"))
                    .thenReturn(List.of(docType(1L, "Acte de naissance")));

            mvc.perform(get("/api/admin/document-types/search").param("q", "acte"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].libelle").value("Acte de naissance"));
        }
    }

    @Nested
    @DisplayName("GET /api/admin/document-types/{id}")
    class GetDocumentTypeById {

        @Test
        @WithMockUser(roles = "ADMIN")
        void retourne_200_et_type_quand_trouve() throws Exception {
            when(documentTypeService.getDocumentTypeById(1L)).thenReturn(docType(1L, "Acte de naissance"));

            mvc.perform(get("/api/admin/document-types/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.libelle").value("Acte de naissance"));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void retourne_4xx_quand_type_inexistant() throws Exception {
            when(documentTypeService.getDocumentTypeById(999L))
                    .thenThrow(new RuntimeException("Type de document non trouvé"));

            mvc.perform(get("/api/admin/document-types/999"))
                    .andExpect(status().is4xxClientError());
        }
    }

    @Nested
    @DisplayName("POST /api/admin/document-types")
    class CreateDocumentType {

        @Test
        @WithMockUser(roles = "ADMIN")
        void retourne_200_et_type_cree() throws Exception {
            DocumentType created = docType(3L, "Nouveau type");
            when(documentTypeService.createDocumentType(any(DocumentTypeRequest.class))).thenReturn(created);

            DocumentTypeRequest request = new DocumentTypeRequest("Nouveau type", "Description", null);
            mvc.perform(post("/api/admin/document-types")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.libelle").value("Nouveau type"));
            verify(documentTypeService).createDocumentType(any(DocumentTypeRequest.class));
        }
    }

    @Nested
    @DisplayName("PUT /api/admin/document-types/{id}")
    class UpdateDocumentType {

        @Test
        @WithMockUser(roles = "ADMIN")
        void retourne_200_et_type_modifie() throws Exception {
            DocumentType updated = docType(1L, "Acte mis à jour");
            when(documentTypeService.updateDocumentType(eq(1L), any(DocumentTypeRequest.class))).thenReturn(updated);

            DocumentTypeRequest request = new DocumentTypeRequest("Acte mis à jour", "Desc", null);
            mvc.perform(put("/api/admin/document-types/1")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.libelle").value("Acte mis à jour"));
        }
    }

    @Nested
    @DisplayName("DELETE /api/admin/document-types/{id}")
    class DeleteDocumentType {

        @Test
        @WithMockUser(roles = "ADMIN")
        void retourne_200_et_appelle_service() throws Exception {
            mvc.perform(delete("/api/admin/document-types/1").with(csrf()))
                    .andExpect(status().isOk());
            verify(documentTypeService).deleteDocumentType(1L);
        }
    }

    @Nested
    @DisplayName("PUT /api/admin/document-types/{id}/activate")
    class ActivateDocumentType {

        @Test
        @WithMockUser(roles = "ADMIN")
        void retourne_200() throws Exception {
            mvc.perform(put("/api/admin/document-types/1/activate").with(csrf()))
                    .andExpect(status().isOk());
            verify(documentTypeService).activateDocumentType(1L);
        }
    }
}
