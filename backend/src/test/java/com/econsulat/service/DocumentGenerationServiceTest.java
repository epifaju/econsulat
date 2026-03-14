package com.econsulat.service;

import com.econsulat.model.Adresse;
import com.econsulat.model.Demande;
import com.econsulat.model.DocumentType;
import com.econsulat.model.GeneratedDocument;
import com.econsulat.model.Pays;
import com.econsulat.model.User;

import java.time.LocalDate;
import com.econsulat.repository.DemandeRepository;
import com.econsulat.repository.DocumentTypeRepository;
import com.econsulat.repository.GeneratedDocumentRepository;
import com.econsulat.storage.StorageService;
import org.junit.jupiter.api.BeforeEach;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DocumentGenerationService")
class DocumentGenerationServiceTest {

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

    @InjectMocks
    private DocumentGenerationService documentGenerationService;

    private Demande demande;
    private DocumentType documentType;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("user@test.com");

        documentType = new DocumentType();
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
        demande.setBirthCountry(pays);
        Adresse adresse = new Adresse();
        adresse.setStreetName("Rue Test");
        adresse.setStreetNumber("1");
        adresse.setPostalCode("75001");
        adresse.setCity("Paris");
        adresse.setCountry(pays);
        demande.setAdresse(adresse);
        demande.setFatherFirstName("Pierre");
        demande.setFatherLastName("Dupont");
        demande.setMotherFirstName("Marie");
        demande.setMotherLastName("Martin");
        demande.setDocumentType(documentType);
    }

    @Nested
    @DisplayName("generateDocument")
    class GenerateDocument {

        @Test
        void throw_quand_demande_inexistante() {
            when(demandeRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> documentGenerationService.generateDocument(99L, 2L, user))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Demande non trouvée");
            verify(generatedDocumentRepository, never()).save(any());
        }

        @Test
        void throw_quand_type_document_inexistant() {
            when(demandeRepository.findById(10L)).thenReturn(Optional.of(demande));
            when(documentTypeRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> documentGenerationService.generateDocument(10L, 99L, user))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Type de document non trouvé");
        }

        @Test
        void retourne_document_existant_quand_deja_genere() {
            GeneratedDocument existing = new GeneratedDocument();
            existing.setId(1L);
            existing.setFileName("existing.docx");

            when(demandeRepository.findById(10L)).thenReturn(Optional.of(demande));
            when(documentTypeRepository.findById(2L)).thenReturn(Optional.of(documentType));
            when(generatedDocumentRepository.findWordDocumentByDemandeAndType(10L, 2L))
                    .thenReturn(Optional.of(existing));

            GeneratedDocument result = documentGenerationService.generateDocument(10L, 2L, user);

            assertThat(result).isSameAs(existing);
            verify(generatedDocumentRepository, never()).save(any());
        }

        @Test
        void throw_quand_validation_echoue_prenom_vide() {
            demande.setFirstName(null);
            when(demandeRepository.findById(10L)).thenReturn(Optional.of(demande));
            when(documentTypeRepository.findById(2L)).thenReturn(Optional.of(documentType));

            assertThatThrownBy(() -> documentGenerationService.generateDocument(10L, 2L, user))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("prénom");
        }

        @Test
        void throw_quand_validation_echoue_nom_famille_vide() {
            demande.setLastName("   ");
            when(demandeRepository.findById(10L)).thenReturn(Optional.of(demande));
            when(documentTypeRepository.findById(2L)).thenReturn(Optional.of(documentType));

            assertThatThrownBy(() -> documentGenerationService.generateDocument(10L, 2L, user))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("nom de famille");
        }

        @Test
        void throw_quand_validation_echoue_lieu_naissance_vide() {
            demande.setBirthPlace("");
            when(demandeRepository.findById(10L)).thenReturn(Optional.of(demande));
            when(documentTypeRepository.findById(2L)).thenReturn(Optional.of(documentType));

            assertThatThrownBy(() -> documentGenerationService.generateDocument(10L, 2L, user))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("lieu de naissance");
        }
    }

    @Nested
    @DisplayName("downloadDocument")
    class DownloadDocument {

        @Test
        void throw_quand_document_inexistant() {
            when(generatedDocumentRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> {
                try {
                    documentGenerationService.downloadDocument(99L);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Document non trouvé");
        }
    }
}
