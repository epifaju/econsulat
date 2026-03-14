package com.econsulat.service;

import com.econsulat.storage.StorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("PdfService")
class PdfServiceTest {

    @Mock
    private StorageService storageService;

    private PdfService pdfService;

    @BeforeEach
    void setUp() {
        pdfService = new PdfService(storageService);
    }

    @Test
    @DisplayName("loadFileAsResource délègue à getDocumentResource et retourne la ressource")
    void loadFileAsResource_delegates_to_storage_and_returns_resource() throws IOException {
        String fileName = "document.pdf";
        Resource expected = new ByteArrayResource("pdf content".getBytes());
        when(storageService.getDocumentResource(fileName)).thenReturn(expected);

        Resource result = pdfService.loadFileAsResource(fileName);

        verify(storageService).getDocumentResource(eq(fileName));
        assertThat(result).isSameAs(expected);
    }

    @Test
    @DisplayName("loadFileAsResource propage l'IOException du storage")
    void loadFileAsResource_propagates_io_exception() throws IOException {
        when(storageService.getDocumentResource("absent.pdf"))
                .thenThrow(new IOException("Document non trouvé: absent.pdf"));

        assertThatThrownBy(() -> pdfService.loadFileAsResource("absent.pdf"))
                .isInstanceOf(IOException.class)
                .hasMessageContaining("absent.pdf");
    }
}
