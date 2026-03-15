package com.econsulat.controller;

import com.econsulat.dto.BilanReportDTO;
import com.econsulat.service.BilanReportExcelService;
import com.econsulat.service.BilanReportPdfService;
import com.econsulat.service.BilanReportService;
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

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BilanReportController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("BilanReportController")
class BilanReportControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private BilanReportService bilanReportService;

    @MockBean
    private BilanReportPdfService bilanReportPdfService;

    @MockBean
    private BilanReportExcelService bilanReportExcelService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserService userService;

    @Nested
    @DisplayName("GET /api/admin/reports/bilan")
    class GetBilan {

        @Test
        @WithMockUser(roles = "ADMIN")
        void retourne_200_et_rapport_JSON() throws Exception {
            BilanReportDTO report = BilanReportDTO.builder()
                    .groupBy("MONTH")
                    .generatedAt(LocalDateTime.now())
                    .currency("eur")
                    .rows(List.of())
                    .totalAmountCents(0L)
                    .totalAmountEuros("0.00")
                    .build();
            when(bilanReportService.buildReport(eq("MONTH"), any(), any(), any())).thenReturn(report);

            mvc.perform(get("/api/admin/reports/bilan").param("groupBy", "MONTH"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.groupBy").value("MONTH"))
                    .andExpect(jsonPath("$.totalAmountCents").value(0))
                    .andExpect(jsonPath("$.totalAmountEuros").value("0.00"));
        }

    }

    @Nested
    @DisplayName("GET /api/admin/reports/bilan/export/pdf")
    class ExportPdf {

        @Test
        @WithMockUser(roles = "ADMIN")
        void retourne_200_et_PDF() throws Exception {
            BilanReportDTO report = BilanReportDTO.builder()
                    .groupBy("MONTH")
                    .rows(List.of())
                    .totalAmountCents(0L)
                    .totalAmountEuros("0.00")
                    .build();
            when(bilanReportService.buildReport(any(), any(), any(), any())).thenReturn(report);
            when(bilanReportPdfService.generatePdf(any(BilanReportDTO.class), any())).thenReturn(new byte[]{'%', 'P', 'D', 'F'});

            mvc.perform(get("/api/admin/reports/bilan/export/pdf"))
                    .andExpect(status().isOk())
                    .andExpect(header().string("Content-Disposition", org.hamcrest.Matchers.containsString("bilan-comptable_")))
                    .andExpect(header().string("Content-Disposition", org.hamcrest.Matchers.containsString(".pdf")))
                    .andExpect(content().contentType(MediaType.APPLICATION_PDF));
        }
    }

    @Nested
    @DisplayName("GET /api/admin/reports/bilan/export/excel")
    class ExportExcel {

        @Test
        @WithMockUser(roles = "ADMIN")
        void retourne_200_et_Excel() throws Exception {
            BilanReportDTO report = BilanReportDTO.builder()
                    .groupBy("MONTH")
                    .rows(List.of())
                    .totalAmountCents(0L)
                    .totalAmountEuros("0.00")
                    .build();
            when(bilanReportService.buildReport(any(), any(), any(), any())).thenReturn(report);
            when(bilanReportExcelService.generateExcel(any(BilanReportDTO.class), any())).thenReturn(new byte[]{1, 2, 3});

            mvc.perform(get("/api/admin/reports/bilan/export/excel"))
                    .andExpect(status().isOk())
                    .andExpect(header().string("Content-Disposition", org.hamcrest.Matchers.containsString("bilan-comptable_")))
                    .andExpect(header().string("Content-Disposition", org.hamcrest.Matchers.containsString(".xlsx")));
        }
    }
}
