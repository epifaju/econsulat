package com.econsulat.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

@ExtendWith(MockitoExtension.class)
@DisplayName("WatermarkService")
class WatermarkServiceTest {

    private WatermarkService watermarkService;

    @BeforeEach
    void setUp() {
        watermarkService = new WatermarkService();
    }

    @Nested
    @DisplayName("generateCustomWatermark")
    class GenerateCustomWatermark {

        @Test
        void retourne_texte_avec_type_document_nom_et_timestamp() {
            String result = watermarkService.generateCustomWatermark("Acte de naissance", "Jean Dupont");

            assertThat(result).startsWith("eConsulat - Acte de naissance - Jean Dupont - ");
            assertThat(result).matches("eConsulat - Acte de naissance - Jean Dupont - \\d{2}/\\d{2}/\\d{4} \\d{2}:\\d{2}:\\d{2}");
        }
    }

    @Nested
    @DisplayName("addWatermarkToWord")
    class AddWatermarkToWord {

        @Test
        void ajoute_watermark_et_retourne_docx_valide() throws IOException {
            byte[] minimalDocx = createMinimalDocx();
            assertThat(minimalDocx.length).isGreaterThan(0);

            byte[] result = watermarkService.addWatermarkToWord(minimalDocx, "Document test");

            assertThat(result).isNotEmpty();
            assertThat(result.length).isGreaterThan(minimalDocx.length);
        }

        private byte[] createMinimalDocx() throws IOException {
            try (XWPFDocument doc = new XWPFDocument();
                 ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                XWPFParagraph p = doc.createParagraph();
                XWPFRun run = p.createRun();
                run.setText("Contenu minimal");
                doc.write(out);
                return out.toByteArray();
            }
        }
    }

    @Nested
    @DisplayName("addSimpleWatermarkToPdf")
    class AddSimpleWatermarkToPdf {

        @Test
        void ajoute_watermark_et_retourne_pdf_valide() throws IOException {
            byte[] minimalPdf = createMinimalPdf();

            byte[] result = watermarkService.addSimpleWatermarkToPdf(minimalPdf, "Document officiel");

            assertThat(result).isNotEmpty();
            assertThat(result.length).isGreaterThan(minimalPdf.length);
        }

        private byte[] createMinimalPdf() throws IOException {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            try (PdfWriter writer = new PdfWriter(out);
                 PdfDocument pdf = new PdfDocument(writer)) {
                pdf.addNewPage();
            }
            return out.toByteArray();
        }
    }
}
