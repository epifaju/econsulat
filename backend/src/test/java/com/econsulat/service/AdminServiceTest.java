package com.econsulat.service;

import com.econsulat.dto.DemandeAdminResponse;
import com.econsulat.dto.DemandeRequest;
import com.econsulat.dto.UserAdminResponse;
import com.econsulat.model.*;
import com.econsulat.repository.CiviliteRepository;
import com.econsulat.repository.DemandeRepository;
import com.econsulat.repository.DocumentTypeRepository;
import com.econsulat.repository.GeneratedDocumentRepository;
import com.econsulat.repository.PaysRepository;
import com.econsulat.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AdminService")
class AdminServiceTest {

    @Mock
    private DemandeRepository demandeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private GeneratedDocumentRepository generatedDocumentRepository;

    @Mock
    private CiviliteRepository civiliteRepository;

    @Mock
    private PaysRepository paysRepository;

    @Mock
    private DocumentTypeRepository documentTypeRepository;

    @InjectMocks
    private AdminService adminService;

    private User user;
    private Demande demande;
    private PageRequest pageRequest;

    @BeforeEach
    void setUp() {
        pageRequest = PageRequest.of(0, 10);
        user = new User();
        user.setId(1L);
        user.setFirstName("Admin");
        user.setLastName("User");
        user.setEmail("admin@test.com");
        user.setRole(User.Role.ADMIN);

        demande = buildDemande(1L, user);
    }

    private Demande buildDemande(Long id, User demandeUser) {
        Civilite civilite = new Civilite(1L, "M.");
        Pays pays = new Pays(1L, "France");
        Adresse adresse = new Adresse();
        adresse.setId(1L);
        adresse.setStreetName("Rue Test");
        adresse.setStreetNumber("1");
        adresse.setPostalCode("75000");
        adresse.setCity("Paris");
        adresse.setCountry(pays);

        DocumentType docType = new DocumentType();
        docType.setId(1L);
        docType.setLibelle("Acte de naissance");

        Demande d = new Demande();
        d.setId(id);
        d.setCivilite(civilite);
        d.setFirstName("Jean");
        d.setLastName("Dupont");
        d.setBirthDate(LocalDate.of(1990, 1, 1));
        d.setBirthPlace("Paris");
        d.setBirthCountry(pays);
        d.setAdresse(adresse);
        d.setFatherFirstName("Paul");
        d.setFatherLastName("Dupont");
        d.setFatherBirthDate(LocalDate.of(1960, 1, 1));
        d.setFatherBirthPlace("Lyon");
        d.setFatherBirthCountry(pays);
        d.setMotherFirstName("Marie");
        d.setMotherLastName("Martin");
        d.setMotherBirthDate(LocalDate.of(1962, 1, 1));
        d.setMotherBirthPlace("Marseille");
        d.setMotherBirthCountry(pays);
        d.setDocumentType(docType);
        d.setUser(demandeUser);
        d.setStatus(Demande.Status.PENDING);
        d.setCreatedAt(LocalDateTime.now());
        d.setUpdatedAt(LocalDateTime.now());
        return d;
    }

    @Nested
    @DisplayName("getAllDemandes / getDemandesByStatus / searchDemandes")
    class ListDemandes {

        @Test
        void getAllDemandes_retourne_page_convertie() {
            when(demandeRepository.findAll(pageRequest)).thenReturn(new PageImpl<>(List.of(demande)));
            when(generatedDocumentRepository.findByDemandeId(1L)).thenReturn(List.of());

            Page<DemandeAdminResponse> result = adminService.getAllDemandes(pageRequest);

            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getId()).isEqualTo(1L);
        }

        @Test
        void getDemandesByStatus_filtre_par_statut() {
            when(demandeRepository.findByStatus(eq(Demande.Status.PENDING), eq(pageRequest)))
                    .thenReturn(new PageImpl<>(List.of(demande)));
            when(generatedDocumentRepository.findByDemandeId(1L)).thenReturn(List.of());

            Page<DemandeAdminResponse> result = adminService.getDemandesByStatus("PENDING", pageRequest);

            verify(demandeRepository).findByStatus(Demande.Status.PENDING, pageRequest);
            assertThat(result.getContent()).hasSize(1);
        }

        @Test
        void searchDemandes_recherche_par_nom() {
            when(demandeRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
                    eq("Dupont"), eq("Dupont"), eq(pageRequest))).thenReturn(new PageImpl<>(List.of(demande)));
            when(generatedDocumentRepository.findByDemandeId(1L)).thenReturn(List.of());

            Page<DemandeAdminResponse> result = adminService.searchDemandes("Dupont", pageRequest);

            assertThat(result.getContent()).hasSize(1);
        }
    }

    @Nested
    @DisplayName("getDemandeById / updateDemandeStatus")
    class DemandeByIdAndStatus {

        @Test
        void getDemandeById_retourne_reponse_quand_trouve() {
            when(demandeRepository.findById(1L)).thenReturn(Optional.of(demande));
            when(generatedDocumentRepository.findByDemandeId(1L)).thenReturn(List.of());

            DemandeAdminResponse result = adminService.getDemandeById(1L);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getFirstName()).isEqualTo("Jean");
        }

        @Test
        void getDemandeById_throw_quand_inexistant() {
            when(demandeRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> adminService.getDemandeById(99L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Demande non trouvée");
        }

        @Test
        void updateDemandeStatus_met_a_jour_et_sauvegarde() {
            when(demandeRepository.findById(1L)).thenReturn(Optional.of(demande));
            when(demandeRepository.save(any(Demande.class))).thenAnswer(inv -> inv.getArgument(0));

            Demande result = adminService.updateDemandeStatus(1L, "APPROVED");

            assertThat(demande.getStatus()).isEqualTo(Demande.Status.APPROVED);
            verify(demandeRepository).save(demande);
        }

        @Test
        void updateDemandeStatus_throw_quand_demande_inexistante() {
            when(demandeRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> adminService.updateDemandeStatus(99L, "APPROVED"))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Demande non trouvée");
        }
    }

    @Nested
    @DisplayName("getAllUsers / searchUsers / getUserById")
    class Users {

        @Test
        void getAllUsers_retourne_page_convertie() {
            when(userRepository.findAll(pageRequest)).thenReturn(new PageImpl<>(List.of(user)));
            when(demandeRepository.countByUser(user)).thenReturn(5L);

            Page<UserAdminResponse> result = adminService.getAllUsers(pageRequest);

            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getEmail()).isEqualTo("admin@test.com");
        }

        @Test
        void getUserById_retourne_reponse_quand_trouve() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(demandeRepository.countByUser(user)).thenReturn(3L);

            UserAdminResponse result = adminService.getUserById(1L);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getDemandesCount()).isEqualTo(3);
        }

        @Test
        void getUserById_throw_quand_utilisateur_absent() {
            when(userRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> adminService.getUserById(99L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Utilisateur non trouvé");
        }

        @Test
        void searchUsers_retourne_page_convertie() {
            when(userRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                    eq("dupont"), eq("dupont"), eq("dupont"), eq(pageRequest)))
                    .thenReturn(new PageImpl<>(List.of(user)));
            when(demandeRepository.countByUser(user)).thenReturn(2L);

            Page<UserAdminResponse> result = adminService.searchUsers("dupont", pageRequest);

            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getEmail()).isEqualTo("admin@test.com");
        }
    }

    @Nested
    @DisplayName("updateDemande")
    class UpdateDemande {

        private DemandeRequest validRequest() {
            DemandeRequest r = new DemandeRequest();
            r.setCiviliteId(1L);
            r.setFirstName("Jean");
            r.setLastName("Dupont");
            r.setBirthDate(LocalDate.of(1990, 1, 1));
            r.setBirthPlace("Paris");
            r.setBirthCountryId(1L);
            r.setCountryId(1L);
            r.setFatherBirthCountryId(1L);
            r.setMotherBirthCountryId(1L);
            r.setStreetName("Rue Test");
            r.setStreetNumber("1");
            r.setPostalCode("75000");
            r.setCity("Paris");
            r.setDocumentTypeId(1L);
            r.setFatherFirstName("Paul");
            r.setFatherLastName("Dupont");
            r.setMotherFirstName("Marie");
            r.setMotherLastName("Martin");
            r.setDocumentFiles(List.of());
            return r;
        }

        @Test
        void met_a_jour_et_retourne_demande_admin_response() {
            DemandeRequest req = validRequest();
            Civilite civ = new Civilite(1L, "M.");
            Pays pays = new Pays(1L, "France");
            DocumentType docType = new DocumentType();
            docType.setId(1L);
            docType.setLibelle("Acte de naissance");

            when(demandeRepository.findById(1L)).thenReturn(Optional.of(demande));
            when(civiliteRepository.findById(1L)).thenReturn(Optional.of(civ));
            when(paysRepository.findById(1L)).thenReturn(Optional.of(pays));
            when(documentTypeRepository.findById(1L)).thenReturn(Optional.of(docType));
            when(demandeRepository.save(any(Demande.class))).thenAnswer(inv -> inv.getArgument(0));
            when(generatedDocumentRepository.findByDemandeId(1L)).thenReturn(List.of());

            DemandeAdminResponse result = adminService.updateDemande(1L, req);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            verify(demandeRepository).save(demande);
        }

        @Test
        void throw_quand_demande_inexistante() {
            when(demandeRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> adminService.updateDemande(99L, validRequest()))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Demande non trouvée");
        }

        @Test
        void throw_quand_civilite_inexistante() {
            when(demandeRepository.findById(1L)).thenReturn(Optional.of(demande));
            when(civiliteRepository.findById(999L)).thenReturn(Optional.empty());
            DemandeRequest req = validRequest();
            req.setCiviliteId(999L);

            assertThatThrownBy(() -> adminService.updateDemande(1L, req))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Civilité");
        }
    }

    @Nested
    @DisplayName("deleteDemande")
    class DeleteDemande {

        @Test
        void supprime_documents_genères_et_demande() {
            when(demandeRepository.findById(1L)).thenReturn(Optional.of(demande));
            List<GeneratedDocument> docs = List.of(new GeneratedDocument());
            when(generatedDocumentRepository.findByDemandeId(1L)).thenReturn(docs);

            adminService.deleteDemande(1L);

            verify(generatedDocumentRepository).deleteAll(docs);
            verify(demandeRepository).delete(demande);
        }

        @Test
        void throw_quand_demande_inexistante() {
            when(demandeRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> adminService.deleteDemande(99L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Demande non trouvée");
        }
    }

    @Nested
    @DisplayName("getTotalDemandes / getDemandesByStatus count / getTotalUsers / getTotalGeneratedDocuments")
    class Stats {

        @Test
        void getTotalDemandes_retourne_count() {
            when(demandeRepository.count()).thenReturn(42L);
            assertThat(adminService.getTotalDemandes()).isEqualTo(42L);
        }

        @Test
        void getDemandesByStatus_retourne_count_par_statut() {
            when(demandeRepository.countByStatus(Demande.Status.PENDING)).thenReturn(10L);
            assertThat(adminService.getDemandesByStatus("PENDING")).isEqualTo(10L);
        }

        @Test
        void getTotalUsers_retourne_count() {
            when(userRepository.count()).thenReturn(7L);
            assertThat(adminService.getTotalUsers()).isEqualTo(7L);
        }

        @Test
        void getTotalGeneratedDocuments_retourne_count() {
            when(generatedDocumentRepository.count()).thenReturn(20L);
            assertThat(adminService.getTotalGeneratedDocuments()).isEqualTo(20L);
        }
    }
}
