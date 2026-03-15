package com.econsulat.service;

import com.econsulat.model.Adresse;
import com.econsulat.model.Demande;
import com.econsulat.model.DocumentType;
import com.econsulat.model.GeneratedDocument;
import com.econsulat.model.Pays;
import com.econsulat.model.User;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import com.econsulat.repository.DemandeRepository;
import com.econsulat.repository.DocumentTypeRepository;
import com.econsulat.repository.GeneratedDocumentRepository;
import com.econsulat.storage.StorageService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.context.MessageSource;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;
import java.util.Locale;

@ExtendWith(MockitoExtension.class)
@DisplayName("PdfDocumentService")
class PdfDocumentServiceTest {

    @Mock
    private DemandeRepository demandeRepository;

    @Mock
    private DocumentTypeRepository documentTypeRepository;

    @Mock
    private GeneratedDocumentRepository generatedDocumentRepository;

    @Mock
    private WatermarkService watermarkService;

    @Mock
    private StorageService storageService;

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private PdfDocumentService pdfDocumentService;

    private Demande demande;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("user@test.com");

        DocumentType documentType = new DocumentType();
        documentType.setId(2L);
        documentType.setLibelle("Acte de naissance");

        demande = new Demande();
        demande.setId(10L);
        demande.setFirstName("Jean");
        demande.setLastName("Dupont");
        demande.setBirthDate(LocalDate.of(1990, 1, 15));
        demande.setBirthPlace("Paris");
        Pays pays = new Pays();
        pays.setId(1L);
        pays.setLibelle("France");
        demande.setBirthCountry(pays);
        Adresse adresse = new Adresse();
        adresse.setStreetName("Rue Test");
        adresse.setStreetNumber("1");
        adresse.setPostalCode("75001");
        adresse.setCity("Paris");
        adresse.setCountry(pays);
        demande.setAdresse(adresse);
        demande.setDocumentType(documentType);

        lenient().when(messageSource.getMessage(anyString(), any(), anyString(), any(Locale.class)))
                .thenAnswer(inv -> inv.getArgument(2));
        lenient().when(messageSource.getMessage(anyString(), any(Object[].class), any(Locale.class)))
                .thenAnswer(inv -> {
                    Object[] args = inv.getArgument(1);
                    String code = inv.getArgument(0);
                    if ("pdf.header".equals(code) && args != null && args.length > 0) return "eConsulat — Type: " + args[0];
                    if ("pdf.footer".equals(code) && args != null && args.length > 0) return "Page " + args[0];
                    return code;
                });
    }

    @Nested
    @DisplayName("generatePdfDocument")
    class GeneratePdfDocument {

        @Test
        void throw_quand_demande_inexistante() {
            when(demandeRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> pdfDocumentService.generatePdfDocument(99L, 2L, user))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Demande non trouvée");
        }

        @Test
        void throw_quand_type_document_null_sur_la_demande() {
            demande.setDocumentType(null);
            when(demandeRepository.findById(10L)).thenReturn(Optional.of(demande));

            assertThatThrownBy(() -> pdfDocumentService.generatePdfDocument(10L, 2L, user))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("type de document n'est pas défini");
        }

        @Test
        void retourne_document_existant_quand_deja_genere() throws Exception {
            GeneratedDocument existing = new GeneratedDocument();
            existing.setId(1L);
            existing.setFileName("existing.pdf");

            when(demandeRepository.findById(10L)).thenReturn(Optional.of(demande));
            when(generatedDocumentRepository.findPdfDocumentByDemandeAndType(10L, 2L))
                    .thenReturn(Optional.of(existing));

            GeneratedDocument result = pdfDocumentService.generatePdfDocument(10L, 2L, user);

            assertThat(result).isSameAs(existing);
            verify(storageService, never()).writeDocument(anyString(), any(byte[].class));
            verify(generatedDocumentRepository, never()).save(any());
        }

        @Test
        void throw_quand_validation_echoue_prenom_vide() {
            demande.setFirstName(null);
            when(demandeRepository.findById(10L)).thenReturn(Optional.of(demande));

            assertThatThrownBy(() -> pdfDocumentService.generatePdfDocument(10L, 2L, user))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("prénom");
        }

        @Test
        void throw_quand_validation_echoue_nom_vide() {
            demande.setLastName("   ");
            when(demandeRepository.findById(10L)).thenReturn(Optional.of(demande));

            assertThatThrownBy(() -> pdfDocumentService.generatePdfDocument(10L, 2L, user))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("nom de famille");
        }

        @Test
        void throw_quand_validation_echoue_date_naissance_nulle() {
            demande.setBirthDate(null);
            when(demandeRepository.findById(10L)).thenReturn(Optional.of(demande));

            assertThatThrownBy(() -> pdfDocumentService.generatePdfDocument(10L, 2L, user))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("date de naissance");
        }

        @Test
        void throw_quand_validation_echoue_lieu_naissance_vide() {
            demande.setBirthPlace("");
            when(demandeRepository.findById(10L)).thenReturn(Optional.of(demande));

            assertThatThrownBy(() -> pdfDocumentService.generatePdfDocument(10L, 2L, user))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("lieu de naissance");
        }

        @Test
        void throw_quand_validation_echoue_pays_naissance_nul() {
            demande.setBirthCountry(null);
            when(demandeRepository.findById(10L)).thenReturn(Optional.of(demande));

            assertThatThrownBy(() -> pdfDocumentService.generatePdfDocument(10L, 2L, user))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("pays de naissance");
        }

        @Test
        void throw_quand_validation_echoue_adresse_nulle() {
            demande.setAdresse(null);
            when(demandeRepository.findById(10L)).thenReturn(Optional.of(demande));

            assertThatThrownBy(() -> pdfDocumentService.generatePdfDocument(10L, 2L, user))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("adresse");
        }

        @Test
        void genere_pdf_et_enregistre_quand_donnees_valides() throws Exception {
            when(demandeRepository.findById(10L)).thenReturn(Optional.of(demande));
            when(generatedDocumentRepository.findPdfDocumentByDemandeAndType(10L, 2L))
                    .thenReturn(Optional.empty());
            when(watermarkService.generateCustomWatermark(anyString(), anyString())).thenReturn("Filigrane");
            when(watermarkService.addSimpleWatermarkToPdf(any(byte[].class), anyString(), any(java.util.Locale.class)))
                    .thenAnswer(inv -> inv.getArgument(0));
            when(storageService.getStoredDocumentPath(anyString())).thenReturn("/storage/doc.pdf");
            when(generatedDocumentRepository.save(any(GeneratedDocument.class))).thenAnswer(inv -> {
                GeneratedDocument d = inv.getArgument(0);
                d.setId(100L);
                return d;
            });

            GeneratedDocument result = pdfDocumentService.generatePdfDocument(10L, 2L, user);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(100L);
            assertThat(result.getFileName()).startsWith("PDF_").endsWith(".pdf");
            assertThat(result.getDemande()).isSameAs(demande);
            assertThat(result.getDocumentType()).isSameAs(demande.getDocumentType());
            verify(storageService).writeDocument(anyString(), any(byte[].class));
            verify(generatedDocumentRepository).save(any(GeneratedDocument.class));
        }

        @Test
        void throw_quand_ecriture_storage_echoue() throws Exception {
            when(demandeRepository.findById(10L)).thenReturn(Optional.of(demande));
            when(generatedDocumentRepository.findPdfDocumentByDemandeAndType(10L, 2L))
                    .thenReturn(Optional.empty());
            when(watermarkService.generateCustomWatermark(anyString(), anyString())).thenReturn("Filigrane");
            when(watermarkService.addSimpleWatermarkToPdf(any(byte[].class), anyString(), any(java.util.Locale.class)))
                    .thenAnswer(inv -> inv.getArgument(0));
            doThrow(new RuntimeException("Storage indisponible")).when(storageService).writeDocument(anyString(), any(byte[].class));

            assertThatThrownBy(() -> pdfDocumentService.generatePdfDocument(10L, 2L, user))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Erreur lors de la génération");
        }
    }

    @Nested
    @DisplayName("downloadPdfDocument")
    class DownloadPdfDocument {

        @Test
        void throw_quand_document_inexistant() {
            when(generatedDocumentRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> {
                try {
                    pdfDocumentService.downloadPdfDocument(99L);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Document non trouvé");
        }

        @Test
        void throw_quand_fichier_inexistant_sur_disque() {
            GeneratedDocument doc = new GeneratedDocument();
            doc.setId(50L);
            doc.setFilePath("/chemin/inexistant/document.pdf");
            when(generatedDocumentRepository.findById(50L)).thenReturn(Optional.of(doc));

            assertThatThrownBy(() -> {
                try {
                    pdfDocumentService.downloadPdfDocument(50L);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Fichier PDF non trouvé");
        }

        @Test
        void retourne_contenu_et_marque_téléchargé(@TempDir Path tempDir) throws Exception {
            byte[] pdfContent = "%PDF-1.4 fake content".getBytes();
            Path filePath = tempDir.resolve("doc.pdf");
            Files.write(filePath, pdfContent);

            GeneratedDocument doc = new GeneratedDocument();
            doc.setId(50L);
            doc.setFilePath(filePath.toString());
            doc.setStatus("GENERATED");
            when(generatedDocumentRepository.findById(50L)).thenReturn(Optional.of(doc));
            when(generatedDocumentRepository.save(any(GeneratedDocument.class))).thenAnswer(inv -> inv.getArgument(0));

            byte[] result = pdfDocumentService.downloadPdfDocument(50L);

            assertThat(result).isEqualTo(pdfContent);
            verify(generatedDocumentRepository).save(argThat((GeneratedDocument d) ->
                    "DOWNLOADED".equals(d.getStatus()) && d.getDownloadedAt() != null));
        }
    }
}
