package com.econsulat.service;

import com.econsulat.model.Citizen;
import com.econsulat.storage.StorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PassportDocumentService")
class PassportDocumentServiceTest {

    @Mock
    private StorageService storageService;

    private PassportDocumentService passportDocumentService;

    @BeforeEach
    void setUp() {
        passportDocumentService = new PassportDocumentService(storageService);
    }

    @Nested
    @DisplayName("generatePassportDocument")
    class GeneratePassportDocument {

        private Citizen citizen() {
            Citizen c = new Citizen();
            c.setId(1L);
            c.setFirstName("Maria");
            c.setLastName("Silva");
            c.setBirthDate(LocalDate.of(1990, 5, 20));
            c.setBirthPlace("Lisboa");
            return c;
        }

        @Test
        @DisplayName("retourne nom fichier et écrit quand template présent et storage OK")
        void retourne_nom_fichier_et_ecrit_quand_succes() throws IOException {
            Citizen citizen = citizen();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            when(storageService.openPassportOutputStream(anyString())).thenReturn(out);

            String filename = passportDocumentService.generatePassportDocument(citizen);

            assertThat(filename).startsWith("passport_1_").endsWith(".docx");
            ArgumentCaptor<String> filenameCaptor = ArgumentCaptor.forClass(String.class);
            verify(storageService).openPassportOutputStream(filenameCaptor.capture());
            assertThat(filenameCaptor.getValue()).startsWith("passport_1_").endsWith(".docx");
            assertThat(out.size()).isGreaterThan(0);
        }

        @Test
        @DisplayName("lance IOException quand openPassportOutputStream échoue")
        void throw_quand_openOutputStream_echoue() throws Exception {
            when(storageService.openPassportOutputStream(anyString()))
                    .thenThrow(new IOException("Storage indisponible"));

            assertThatThrownBy(() -> passportDocumentService.generatePassportDocument(citizen()))
                    .isInstanceOf(IOException.class)
                    .hasMessageContaining("Erreur lors de la génération");
        }

        @Test
        @DisplayName("gère citoyen avec champs null (chaînes vides)")
        void gere_citoyen_avec_champs_null() throws IOException {
            Citizen c = citizen();
            c.setFirstName(null);
            c.setLastName(null);
            c.setBirthPlace(null);
            c.setBirthDate(null);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            when(storageService.openPassportOutputStream(anyString())).thenReturn(out);

            String filename = passportDocumentService.generatePassportDocument(c);

            assertThat(filename).startsWith("passport_1_").endsWith(".docx");
            verify(storageService).openPassportOutputStream(anyString());
        }
    }

    @Nested
    @DisplayName("getDocument")
    class GetDocument {

    @Test
    @DisplayName("getDocument délègue à readPassport et retourne les octets")
    void getDocument_delegates_to_readPassport() throws IOException {
        String filename = "passport_1_abc.docx";
        byte[] expected = "contenu passeport".getBytes();
        when(storageService.readPassport(filename)).thenReturn(expected);

        byte[] result = passportDocumentService.getDocument(filename);

        verify(storageService).readPassport(eq(filename));
        assertThat(result).isEqualTo(expected);
    }

    @Test
    @DisplayName("getDocument propage l'IOException")
    void getDocument_propagates_io_exception() throws IOException {
        when(storageService.readPassport("absent.docx"))
                .thenThrow(new IOException("Passeport non trouvé"));

        assertThatThrownBy(() -> passportDocumentService.getDocument("absent.docx"))
                .isInstanceOf(IOException.class)
                .hasMessageContaining("non trouvé");
    }
    }

    @Nested
    @DisplayName("deleteDocument")
    class DeleteDocument {

    @Test
    @DisplayName("deleteDocument appelle deletePassport si le fichier existe")
    void deleteDocument_calls_deletePassport_when_exists() throws IOException {
        String filename = "passport_1_xyz.docx";
        when(storageService.passportExists(filename)).thenReturn(true);

        passportDocumentService.deleteDocument(filename);

        verify(storageService).passportExists(filename);
        verify(storageService).deletePassport(filename);
    }

    @Test
    @DisplayName("deleteDocument ne fait rien si le fichier n'existe pas")
    void deleteDocument_does_nothing_when_not_exists() throws IOException {
        when(storageService.passportExists("absent.docx")).thenReturn(false);

        passportDocumentService.deleteDocument("absent.docx");

        verify(storageService).passportExists("absent.docx");
        verify(storageService, never()).deletePassport(any());
    }
    }
}
