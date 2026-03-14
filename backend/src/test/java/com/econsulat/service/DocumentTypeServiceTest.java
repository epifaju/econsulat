package com.econsulat.service;

import com.econsulat.dto.DocumentTypeRequest;
import com.econsulat.model.DocumentType;
import com.econsulat.repository.DocumentTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DocumentTypeService")
class DocumentTypeServiceTest {

    @Mock
    private DocumentTypeRepository documentTypeRepository;

    @InjectMocks
    private DocumentTypeService documentTypeService;

    private DocumentType documentType;
    private DocumentTypeRequest request;

    @BeforeEach
    void setUp() {
        documentType = new DocumentType();
        documentType.setId(1L);
        documentType.setLibelle("Acte de naissance");
        documentType.setDescription("Description");
        documentType.setIsActive(true);
        documentType.setPriceCents(1000);

        request = new DocumentTypeRequest();
        request.setLibelle("Certificat de mariage");
        request.setDescription("Desc");
        request.setTemplatePath("templates/mariage.docx");
        request.setIsActive(true);
        request.setPriceCents(1500);
    }

    @Nested
    @DisplayName("getAllDocumentTypes")
    class GetAllDocumentTypes {

        @Test
        void retourne_la_liste_des_types_actifs() {
            List<DocumentType> list = List.of(documentType);
            when(documentTypeRepository.findByIsActiveTrue()).thenReturn(list);

            List<DocumentType> result = documentTypeService.getAllDocumentTypes();

            verify(documentTypeRepository).findByIsActiveTrue();
            assertThat(result).isSameAs(list);
        }
    }

    @Nested
    @DisplayName("searchDocumentTypes")
    class SearchDocumentTypes {

        @Test
        void delegue_au_repository_avec_le_terme() {
            List<DocumentType> list = List.of(documentType);
            when(documentTypeRepository.findActiveBySearchTerm("acte")).thenReturn(list);

            List<DocumentType> result = documentTypeService.searchDocumentTypes("acte");

            verify(documentTypeRepository).findActiveBySearchTerm("acte");
            assertThat(result).isSameAs(list);
        }
    }

    @Nested
    @DisplayName("getDocumentTypeById")
    class GetDocumentTypeById {

        @Test
        void retourne_le_type_quand_trouve() {
            when(documentTypeRepository.findById(1L)).thenReturn(Optional.of(documentType));

            DocumentType result = documentTypeService.getDocumentTypeById(1L);

            assertThat(result).isSameAs(documentType);
        }

        @Test
        void throw_quand_non_trouve() {
            when(documentTypeRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> documentTypeService.getDocumentTypeById(99L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("non trouvé");
        }
    }

    @Nested
    @DisplayName("createDocumentType")
    class CreateDocumentType {

        @Test
        void enregistre_quand_libelle_inexistant() {
            when(documentTypeRepository.existsByLibelle(request.getLibelle())).thenReturn(false);
            when(documentTypeRepository.save(any(DocumentType.class))).thenAnswer(inv -> {
                DocumentType dt = inv.getArgument(0);
                dt.setId(2L);
                return dt;
            });

            DocumentType result = documentTypeService.createDocumentType(request);

            assertThat(result.getLibelle()).isEqualTo("Certificat de mariage");
            assertThat(result.getDescription()).isEqualTo("Desc");
            assertThat(result.getTemplatePath()).isEqualTo("templates/mariage.docx");
            assertThat(result.getIsActive()).isTrue();
            assertThat(result.getPriceCents()).isEqualTo(1500);
            verify(documentTypeRepository).save(any(DocumentType.class));
        }

        @Test
        void utilise_isActive_true_par_defaut_quand_null() {
            request.setIsActive(null);
            when(documentTypeRepository.existsByLibelle(request.getLibelle())).thenReturn(false);
            when(documentTypeRepository.save(any(DocumentType.class))).thenAnswer(inv -> inv.getArgument(0));

            DocumentType result = documentTypeService.createDocumentType(request);

            assertThat(result.getIsActive()).isTrue();
        }

        @Test
        void throw_quand_libelle_deja_existant() {
            when(documentTypeRepository.existsByLibelle(request.getLibelle())).thenReturn(true);

            assertThatThrownBy(() -> documentTypeService.createDocumentType(request))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("existe déjà");
            verify(documentTypeRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("updateDocumentType")
    class UpdateDocumentType {

        @Test
        void met_a_jour_quand_libelle_identique_ou_unique() {
            when(documentTypeRepository.findById(1L)).thenReturn(Optional.of(documentType));
            when(documentTypeRepository.existsByLibelleAndIdNot(eq("Certificat"), eq(1L))).thenReturn(false);
            when(documentTypeRepository.save(any(DocumentType.class))).thenAnswer(inv -> inv.getArgument(0));
            request.setLibelle("Certificat");

            DocumentType result = documentTypeService.updateDocumentType(1L, request);

            assertThat(result.getLibelle()).isEqualTo("Certificat");
            assertThat(result.getDescription()).isEqualTo("Desc");
            verify(documentTypeRepository).save(documentType);
        }

        @Test
        void throw_quand_nouveau_libelle_deja_utilise_par_autre() {
            when(documentTypeRepository.findById(1L)).thenReturn(Optional.of(documentType));
            when(documentTypeRepository.existsByLibelleAndIdNot(eq("Autre type"), eq(1L))).thenReturn(true);
            request.setLibelle("Autre type");

            assertThatThrownBy(() -> documentTypeService.updateDocumentType(1L, request))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("existe déjà");
            verify(documentTypeRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("deleteDocumentType")
    class DeleteDocumentType {

        @Test
        void desactive_le_type_au_lieu_de_supprimer() {
            when(documentTypeRepository.findById(1L)).thenReturn(Optional.of(documentType));
            when(documentTypeRepository.save(any(DocumentType.class))).thenAnswer(inv -> inv.getArgument(0));

            documentTypeService.deleteDocumentType(1L);

            ArgumentCaptor<DocumentType> captor = ArgumentCaptor.forClass(DocumentType.class);
            verify(documentTypeRepository).save(captor.capture());
            assertThat(captor.getValue().getIsActive()).isFalse();
        }
    }

    @Nested
    @DisplayName("activateDocumentType")
    class ActivateDocumentType {

        @Test
        void reactive_le_type() {
            documentType.setIsActive(false);
            when(documentTypeRepository.findById(1L)).thenReturn(Optional.of(documentType));
            when(documentTypeRepository.save(any(DocumentType.class))).thenAnswer(inv -> inv.getArgument(0));

            documentTypeService.activateDocumentType(1L);

            ArgumentCaptor<DocumentType> captor = ArgumentCaptor.forClass(DocumentType.class);
            verify(documentTypeRepository).save(captor.capture());
            assertThat(captor.getValue().getIsActive()).isTrue();
        }
    }
}
