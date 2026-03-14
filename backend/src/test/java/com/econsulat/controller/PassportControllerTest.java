package com.econsulat.controller;

import com.econsulat.model.Citizen;
import com.econsulat.service.CitizenService;
import com.econsulat.service.JwtService;
import com.econsulat.service.PassportDocumentService;
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

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = PassportController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("PassportController")
class PassportControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private PassportDocumentService passportDocumentService;

    @MockBean
    private CitizenService citizenService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserService userService;

    private static Citizen citizen(long id) {
        Citizen c = new Citizen();
        c.setId(id);
        c.setFirstName("Jean");
        c.setLastName("Dupont");
        return c;
    }

    @Nested
    @DisplayName("POST /api/passport/generate/{citizenId}")
    class GeneratePassportDocument {

        @Test
        @WithMockUser(roles = "ADMIN")
        void retourne_200_et_filename_quand_succes() throws Exception {
            Citizen c = citizen(1L);
            when(citizenService.getCitizenByIdOrThrow(1L)).thenReturn(c);
            when(passportDocumentService.generatePassportDocument(any(Citizen.class))).thenReturn("passport_1.docx");

            mvc.perform(post("/api/passport/generate/1").with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.filename").value("passport_1.docx"))
                    .andExpect(jsonPath("$.citizenId").value(1));
        }
    }

    @Nested
    @DisplayName("GET /api/passport/download/{filename}")
    class DownloadPassportDocument {

        @Test
        @WithMockUser(roles = "ADMIN")
        void retourne_200_et_contenu_quand_succes() throws Exception {
            when(passportDocumentService.getDocument("passport_1.docx")).thenReturn(new byte[]{1, 2, 3});

            mvc.perform(get("/api/passport/download/passport_1.docx"))
                    .andExpect(status().isOk())
                    .andExpect(header().exists("Content-Disposition"));
        }
    }

    @Nested
    @DisplayName("DELETE /api/passport/delete/{filename}")
    class DeletePassportDocument {

        @Test
        @WithMockUser(roles = "ADMIN")
        void retourne_200_quand_succes() throws Exception {
            mvc.perform(delete("/api/passport/delete/doc.docx").with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.filename").value("doc.docx"));
            verify(passportDocumentService).deleteDocument("doc.docx");
        }
    }

    @Nested
    @DisplayName("GET /api/passport/test")
    class TestEndpoint {

        @Test
        @WithMockUser(roles = "ADMIN")
        void retourne_200_et_message() throws Exception {
            mvc.perform(get("/api/passport/test"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("Endpoint de passeport fonctionnel"));
        }
    }
}
