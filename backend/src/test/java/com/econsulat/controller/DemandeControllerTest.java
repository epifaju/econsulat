package com.econsulat.controller;

import com.econsulat.dto.DemandeRequest;
import com.econsulat.dto.DemandeResponse;
import com.econsulat.model.Civilite;
import com.econsulat.model.Demande;
import com.econsulat.model.DocumentType;
import com.econsulat.model.GeneratedDocument;
import com.econsulat.model.Pays;
import com.econsulat.model.User;
import com.econsulat.repository.CiviliteRepository;
import com.econsulat.repository.DemandeRepository;
import com.econsulat.repository.DocumentTypeRepository;
import com.econsulat.repository.GeneratedDocumentRepository;
import com.econsulat.repository.PaysRepository;
import com.econsulat.repository.UserRepository;
import com.econsulat.service.DemandeService;
import com.econsulat.service.JwtService;
import com.econsulat.service.PdfDocumentService;
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

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = DemandeController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("DemandeController")
class DemandeControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DemandeService demandeService;

    @MockBean
    private PdfDocumentService pdfDocumentService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private GeneratedDocumentRepository generatedDocumentRepository;

    @MockBean
    private CiviliteRepository civiliteRepository;

    @MockBean
    private PaysRepository paysRepository;

    @MockBean
    private DocumentTypeRepository documentTypeRepository;

    @MockBean
    private DemandeRepository demandeRepository;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserService userService;

    private static DemandeResponse demandeResponse(long id, String status) {
        DemandeResponse r = new DemandeResponse();
        r.setId(id);
        r.setStatus(status);
        r.setFirstName("Jean");
        r.setLastName("Dupont");
        return r;
    }

    @Nested
    @DisplayName("POST /api/demandes")
    class CreateDemande {

        @Test
        @WithMockUser(username = "user@test.com")
        void retourne_200_quand_creation_reussie() throws Exception {
            DemandeRequest request = new DemandeRequest();
            request.setDocumentTypeId(1L);
            request.setFirstName("Jean");
            request.setLastName("Dupont");
            DemandeResponse response = demandeResponse(10L, "PENDING");
            when(demandeService.createDemande(any(DemandeRequest.class), eq("user@test.com"))).thenReturn(response);

            mvc.perform(post("/api/demandes")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(10))
                    .andExpect(jsonPath("$.status").value("PENDING"));
            verify(demandeService).createDemande(any(DemandeRequest.class), eq("user@test.com"));
        }

        @Test
        @WithMockUser(username = "user@test.com")
        void retourne_400_quand_validation_echoue() throws Exception {
            when(demandeService.createDemande(any(DemandeRequest.class), eq("user@test.com")))
                    .thenThrow(new RuntimeException("Le prénom est requis"));

            DemandeRequest request = new DemandeRequest();
            request.setDocumentTypeId(1L);
            mvc.perform(post("/api/demandes")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("Erreur de validation"))
                    .andExpect(jsonPath("$.message").value("Le prénom est requis"));
        }
    }

    @Nested
    @DisplayName("GET /api/demandes/my")
    class GetMyDemandes {

        @Test
        @WithMockUser(username = "user@test.com")
        void retourne_200_et_liste_demandes() throws Exception {
            when(demandeService.getDemandesByUser("user@test.com"))
                    .thenReturn(List.of(demandeResponse(1L, "PENDING"), demandeResponse(2L, "APPROVED")));

            mvc.perform(get("/api/demandes/my"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].id").value(1))
                    .andExpect(jsonPath("$[1].status").value("APPROVED"));
        }
    }

    @Nested
    @DisplayName("GET /api/demandes/{id}")
    class GetDemandeById {

        @Test
        @WithMockUser(username = "user@test.com")
        void retourne_200_et_demande() throws Exception {
            when(demandeService.getDemandeById(1L, "user@test.com")).thenReturn(demandeResponse(1L, "APPROVED"));

            mvc.perform(get("/api/demandes/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.status").value("APPROVED"));
        }
    }

    @Nested
    @DisplayName("GET /api/demandes/all")
    class GetAllDemandes {

        @Test
        @WithMockUser(username = "admin@test.com")
        void retourne_200_et_liste() throws Exception {
            when(demandeService.getAllDemandes()).thenReturn(List.of(demandeResponse(1L, "PENDING")));

            mvc.perform(get("/api/demandes/all"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1));
        }
    }

    @Nested
    @DisplayName("GET /api/demandes/civilites")
    class GetCivilites {

        @Test
        void retourne_200_et_liste_civilites() throws Exception {
            Civilite m = new Civilite();
            m.setId(1L);
            m.setLibelle("M.");
            when(civiliteRepository.findAll()).thenReturn(List.of(m));

            mvc.perform(get("/api/demandes/civilites"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].libelle").value("M."));
        }
    }

    @Nested
    @DisplayName("GET /api/demandes/pays")
    class GetPays {

        @Test
        void retourne_200_et_liste_pays() throws Exception {
            Pays fr = new Pays();
            fr.setId(1L);
            fr.setLibelle("France");
            when(paysRepository.findAllOrdered()).thenReturn(List.of(fr));

            mvc.perform(get("/api/demandes/pays"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].libelle").value("France"));
        }
    }

    @Nested
    @DisplayName("GET /api/demandes/document-types")
    class GetDocumentTypes {

        @Test
        void retourne_200_et_liste_types_actifs() throws Exception {
            DocumentType type = new DocumentType();
            type.setId(1L);
            type.setLibelle("Acte de naissance");
            when(documentTypeRepository.findByIsActiveTrue()).thenReturn(List.of(type));

            mvc.perform(get("/api/demandes/document-types"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].value").value("1"))
                    .andExpect(jsonPath("$[0].label").value("Acte de naissance"));
        }
    }

    @Nested
    @DisplayName("POST /api/demandes/{id}/generate-document")
    class GenerateDocumentForDemande {

        @Test
        @WithMockUser(username = "user@test.com")
        void retourne_200_et_documentId_quand_demande_approuvee() throws Exception {
            Long demandeId = 1L;
            when(demandeService.getDemandeById(demandeId, "user@test.com"))
                    .thenReturn(demandeResponse(demandeId, "APPROVED"));
            User user = new User();
            user.setId(1L);
            user.setEmail("user@test.com");
            when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
            GeneratedDocument doc = new GeneratedDocument();
            doc.setId(100L);
            doc.setFileName("acte_naissance_1.pdf");
            when(pdfDocumentService.generatePdfDocument(eq(demandeId), eq(1L), any(User.class)))
                    .thenReturn(doc);

            mvc.perform(post("/api/demandes/" + demandeId + "/generate-document").with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("Document généré avec succès"))
                    .andExpect(jsonPath("$.documentId").value(100))
                    .andExpect(jsonPath("$.fileName").value("acte_naissance_1.pdf"));

            verify(pdfDocumentService).generatePdfDocument(eq(demandeId), eq(1L), any(User.class));
        }

        @Test
        @WithMockUser(username = "user@test.com")
        void retourne_400_quand_demande_non_approuvee() throws Exception {
            Long demandeId = 1L;
            when(demandeService.getDemandeById(demandeId, "user@test.com"))
                    .thenReturn(demandeResponse(demandeId, "PENDING"));

            mvc.perform(post("/api/demandes/" + demandeId + "/generate-document").with(csrf()))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("La demande doit être approuvée pour générer un document"));

            verify(pdfDocumentService, never()).generatePdfDocument(any(), any(), any());
        }

        @Test
        @WithMockUser(username = "user@test.com")
        void retourne_500_quand_utilisateur_non_trouve() throws Exception {
            Long demandeId = 1L;
            when(demandeService.getDemandeById(demandeId, "user@test.com"))
                    .thenReturn(demandeResponse(demandeId, "APPROVED"));
            when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.empty());

            mvc.perform(post("/api/demandes/" + demandeId + "/generate-document").with(csrf()))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.error").value("Erreur lors de la génération"));

            verify(pdfDocumentService, never()).generatePdfDocument(any(), any(), any());
        }

        @Test
        @WithMockUser(username = "user@test.com")
        void retourne_500_quand_generation_echoue() throws Exception {
            Long demandeId = 1L;
            when(demandeService.getDemandeById(demandeId, "user@test.com"))
                    .thenReturn(demandeResponse(demandeId, "APPROVED"));
            User user = new User();
            user.setId(1L);
            when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
            when(pdfDocumentService.generatePdfDocument(any(), any(), any()))
                    .thenThrow(new RuntimeException("Erreur PDF"));

            mvc.perform(post("/api/demandes/" + demandeId + "/generate-document").with(csrf()))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.error").value("Erreur lors de la génération"));
        }
    }

    @Nested
    @DisplayName("GET /api/demandes/{id}/download-document")
    class DownloadDocumentForDemande {

        @Test
        @WithMockUser(username = "user@test.com")
        void retourne_200_et_pdf_quand_document_trouve_par_type() throws Exception {
            Long demandeId = 1L;
            when(demandeService.getDemandeById(demandeId, "user@test.com"))
                    .thenReturn(demandeResponse(demandeId, "APPROVED"));
            Demande demande = new Demande();
            demande.setId(demandeId);
            DocumentType docType = new DocumentType();
            docType.setId(2L);
            docType.setLibelle("Acte de naissance");
            demande.setDocumentType(docType);
            when(demandeRepository.findById(demandeId)).thenReturn(Optional.of(demande));

            GeneratedDocument generatedDoc = new GeneratedDocument();
            generatedDoc.setId(50L);
            generatedDoc.setFileName("document_1.pdf");
            when(generatedDocumentRepository.findPdfDocumentByDemandeAndType(demandeId, 2L))
                    .thenReturn(Optional.of(generatedDoc));

            byte[] pdfBytes = "fake-pdf-content".getBytes();
            when(pdfDocumentService.downloadPdfDocument(50L)).thenReturn(pdfBytes);

            mvc.perform(get("/api/demandes/" + demandeId + "/download-document"))
                    .andExpect(status().isOk())
                    .andExpect(header().string("Content-Type", "application/pdf"))
                    .andExpect(header().exists("Content-Disposition"))
                    .andExpect(content().bytes(pdfBytes));

            verify(pdfDocumentService).downloadPdfDocument(50L);
        }

        @Test
        @WithMockUser(username = "user@test.com")
        void retourne_200_quand_document_trouve_via_findByDemandeId_fallback() throws Exception {
            Long demandeId = 1L;
            when(demandeService.getDemandeById(demandeId, "user@test.com"))
                    .thenReturn(demandeResponse(demandeId, "APPROVED"));
            Demande demande = new Demande();
            demande.setId(demandeId);
            DocumentType docType = new DocumentType();
            docType.setId(2L);
            demande.setDocumentType(docType);
            when(demandeRepository.findById(demandeId)).thenReturn(Optional.of(demande));
            when(generatedDocumentRepository.findPdfDocumentByDemandeAndType(demandeId, 2L))
                    .thenReturn(Optional.empty());

            GeneratedDocument generatedDoc = new GeneratedDocument();
            generatedDoc.setId(60L);
            generatedDoc.setFileName("autre_doc.pdf");
            when(generatedDocumentRepository.findByDemandeId(demandeId))
                    .thenReturn(List.of(generatedDoc));

            byte[] pdfBytes = "pdf-bytes".getBytes();
            when(pdfDocumentService.downloadPdfDocument(60L)).thenReturn(pdfBytes);

            mvc.perform(get("/api/demandes/" + demandeId + "/download-document"))
                    .andExpect(status().isOk())
                    .andExpect(content().bytes(pdfBytes));
        }

        @Test
        @WithMockUser(username = "user@test.com")
        void retourne_404_quand_aucun_document_pdf_trouve() throws Exception {
            Long demandeId = 1L;
            when(demandeService.getDemandeById(demandeId, "user@test.com"))
                    .thenReturn(demandeResponse(demandeId, "APPROVED"));
            Demande demande = new Demande();
            demande.setId(demandeId);
            DocumentType docType = new DocumentType();
            docType.setId(2L);
            demande.setDocumentType(docType);
            when(demandeRepository.findById(demandeId)).thenReturn(Optional.of(demande));
            when(generatedDocumentRepository.findPdfDocumentByDemandeAndType(demandeId, 2L))
                    .thenReturn(Optional.empty());
            when(generatedDocumentRepository.findByDemandeId(demandeId)).thenReturn(List.of());

            mvc.perform(get("/api/demandes/" + demandeId + "/download-document"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error").value("Aucun document PDF trouvé pour cette demande"));
        }

        @Test
        @WithMockUser(username = "user@test.com")
        void retourne_500_quand_demande_introuvable_en_base() throws Exception {
            Long demandeId = 1L;
            when(demandeService.getDemandeById(demandeId, "user@test.com"))
                    .thenReturn(demandeResponse(demandeId, "APPROVED"));
            when(demandeRepository.findById(demandeId)).thenReturn(Optional.empty());

            mvc.perform(get("/api/demandes/" + demandeId + "/download-document"))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.error").value("Erreur lors du téléchargement"));
        }

        @Test
        @WithMockUser(username = "user@test.com")
        void retourne_500_quand_download_echoue() throws Exception {
            Long demandeId = 1L;
            when(demandeService.getDemandeById(demandeId, "user@test.com"))
                    .thenReturn(demandeResponse(demandeId, "APPROVED"));
            Demande demande = new Demande();
            demande.setId(demandeId);
            DocumentType docType = new DocumentType();
            docType.setId(2L);
            demande.setDocumentType(docType);
            when(demandeRepository.findById(demandeId)).thenReturn(Optional.of(demande));
            GeneratedDocument generatedDoc = new GeneratedDocument();
            generatedDoc.setId(50L);
            generatedDoc.setFileName("doc.pdf");
            when(generatedDocumentRepository.findPdfDocumentByDemandeAndType(demandeId, 2L))
                    .thenReturn(Optional.of(generatedDoc));
            when(pdfDocumentService.downloadPdfDocument(50L))
                    .thenThrow(new RuntimeException("Fichier manquant"));

            mvc.perform(get("/api/demandes/" + demandeId + "/download-document"))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.error").value("Erreur lors du téléchargement"));
        }
    }

    @Nested
    @DisplayName("PUT /api/demandes/{id}/status")
    class UpdateDemandeStatus {

        @Test
        @WithMockUser(username = "admin@test.com", roles = {"ADMIN"})
        void retourne_200_et_demande_mise_a_jour_quand_statut_valide() throws Exception {
            Long demandeId = 1L;
            DemandeResponse updated = demandeResponse(demandeId, "APPROVED");
            when(demandeService.updateDemandeStatus(eq(demandeId), eq(Demande.Status.APPROVED), eq("admin@test.com")))
                    .thenReturn(updated);

            mvc.perform(put("/api/demandes/" + demandeId + "/status")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(Map.of("status", "APPROVED"))))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.status").value("APPROVED"));

            verify(demandeService).updateDemandeStatus(eq(demandeId), eq(Demande.Status.APPROVED), eq("admin@test.com"));
        }

        @Test
        @WithMockUser(username = "agent@test.com", roles = {"AGENT"})
        void retourne_200_quand_agent_met_a_jour_statut() throws Exception {
            Long demandeId = 2L;
            when(demandeService.updateDemandeStatus(eq(demandeId), eq(Demande.Status.REJECTED), eq("agent@test.com")))
                    .thenReturn(demandeResponse(demandeId, "REJECTED"));

            mvc.perform(put("/api/demandes/" + demandeId + "/status")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(Map.of("status", "REJECTED"))))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("REJECTED"));
        }

        @Test
        @WithMockUser(username = "admin@test.com", roles = {"ADMIN"})
        void retourne_400_quand_statut_manquant() throws Exception {
            mvc.perform(put("/api/demandes/1/status")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isBadRequest());

            verify(demandeService, never()).updateDemandeStatus(any(), any(), any());
        }

        @Test
        @WithMockUser(username = "admin@test.com", roles = {"ADMIN"})
        void retourne_400_quand_statut_invalide() throws Exception {
            mvc.perform(put("/api/demandes/1/status")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(Map.of("status", "INVALID_STATUS"))))
                    .andExpect(status().isBadRequest());

            verify(demandeService, never()).updateDemandeStatus(any(), any(), any());
        }

        @Test
        @WithMockUser(username = "admin@test.com", roles = {"ADMIN"})
        void retourne_400_quand_service_lance_exception() throws Exception {
            when(demandeService.updateDemandeStatus(eq(1L), eq(Demande.Status.APPROVED), eq("admin@test.com")))
                    .thenThrow(new RuntimeException("Demande introuvable"));

            mvc.perform(put("/api/demandes/1/status")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(Map.of("status", "APPROVED"))))
                    .andExpect(status().isBadRequest());
        }
    }
}
