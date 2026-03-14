package com.econsulat.service;

import com.econsulat.dto.DemandeRequest;
import com.econsulat.dto.DemandeResponse;
import com.econsulat.model.*;
import com.econsulat.repository.CiviliteRepository;
import com.econsulat.repository.DemandeRepository;
import com.econsulat.repository.DocumentTypeRepository;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DemandeService")
class DemandeServiceTest {

    @Mock
    private DemandeRepository demandeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CiviliteRepository civiliteRepository;

    @Mock
    private PaysRepository paysRepository;

    @Mock
    private DocumentTypeRepository documentTypeRepository;

    @Mock
    private EmailNotificationService emailNotificationService;

    @InjectMocks
    private DemandeService demandeService;

    private User owner;
    private User admin;
    private User otherUser;
    private Demande demande;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setId(10L);
        owner.setEmail("owner@test.com");
        owner.setFirstName("Owner");
        owner.setLastName("User");
        owner.setRole(User.Role.USER);

        admin = new User();
        admin.setId(1L);
        admin.setEmail("admin@test.com");
        admin.setRole(User.Role.ADMIN);
        admin.setEmailVerified(true);

        otherUser = new User();
        otherUser.setId(99L);
        otherUser.setEmail("other@test.com");
        otherUser.setRole(User.Role.USER);

        demande = buildDemande(1L, owner);
    }

    private Demande buildDemande(Long id, User user) {
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
        d.setUser(user);
        d.setStatus(Demande.Status.PENDING);
        d.setCreatedAt(LocalDateTime.now());
        d.setUpdatedAt(LocalDateTime.now());
        return d;
    }

    @Nested
    @DisplayName("getDemandesByUser")
    class GetDemandesByUser {

        @Test
        void retourne_liste_convertie_quand_utilisateur_trouve() {
            when(userRepository.findByEmail("owner@test.com")).thenReturn(Optional.of(owner));
            when(demandeRepository.findByUserOrderByCreatedAtDesc(owner)).thenReturn(List.of(demande));

            List<DemandeResponse> result = demandeService.getDemandesByUser("owner@test.com");

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getFirstName()).isEqualTo("Jean");
            assertThat(result.get(0).getStatus()).isEqualTo("PENDING");
        }

        @Test
        void throw_quand_utilisateur_non_trouve() {
            when(userRepository.findByEmail("absent@test.com")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> demandeService.getDemandesByUser("absent@test.com"))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Utilisateur non trouvé");
        }
    }

    @Nested
    @DisplayName("getDemandeById")
    class GetDemandeById {

        @Test
        void retourne_demande_quand_utilisateur_est_proprietaire() {
            when(userRepository.findByEmail("owner@test.com")).thenReturn(Optional.of(owner));
            when(demandeRepository.findById(1L)).thenReturn(Optional.of(demande));

            DemandeResponse result = demandeService.getDemandeById(1L, "owner@test.com");

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
        }

        @Test
        void retourne_demande_quand_utilisateur_est_admin() {
            when(userRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(admin));
            when(demandeRepository.findById(1L)).thenReturn(Optional.of(demande));

            DemandeResponse result = demandeService.getDemandeById(1L, "admin@test.com");

            assertThat(result).isNotNull();
        }

        @Test
        void throw_quand_utilisateur_ni_proprietaire_ni_admin() {
            when(userRepository.findByEmail("other@test.com")).thenReturn(Optional.of(otherUser));
            when(demandeRepository.findById(1L)).thenReturn(Optional.of(demande));

            assertThatThrownBy(() -> demandeService.getDemandeById(1L, "other@test.com"))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Accès non autorisé");
        }

        @Test
        void throw_quand_demande_inexistante() {
            when(userRepository.findByEmail("owner@test.com")).thenReturn(Optional.of(owner));
            when(demandeRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> demandeService.getDemandeById(99L, "owner@test.com"))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Demande non trouvée");
        }
    }

    @Nested
    @DisplayName("getAllDemandes")
    class GetAllDemandes {

        @Test
        void retourne_liste_convertie_de_toutes_les_demandes() {
            when(demandeRepository.findAll()).thenReturn(List.of(demande));

            List<DemandeResponse> result = demandeService.getAllDemandes();

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getId()).isEqualTo(1L);
        }
    }

    @Nested
    @DisplayName("createDemande")
    class CreateDemande {

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
            r.setFatherBirthDate(LocalDate.of(1960, 1, 1));
            r.setFatherBirthPlace("Lyon");
            r.setMotherFirstName("Marie");
            r.setMotherLastName("Martin");
            r.setMotherBirthDate(LocalDate.of(1962, 1, 1));
            r.setMotherBirthPlace("Marseille");
            return r;
        }

        @Test
        void throw_quand_utilisateur_non_trouve() {
            when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> demandeService.createDemande(validRequest(), "user@test.com"))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Utilisateur non trouvé");
            verify(demandeRepository, never()).save(any());
        }

        @Test
        void throw_quand_documentTypeId_null() {
            DemandeRequest r = validRequest();
            r.setDocumentTypeId(null);

            assertThatThrownBy(() -> demandeService.createDemande(r, "owner@test.com"))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("type de document");
        }

        @Test
        void throw_quand_civilite_inexistante() {
            DemandeRequest r = validRequest();
            when(userRepository.findByEmail("owner@test.com")).thenReturn(Optional.of(owner));
            when(civiliteRepository.findById(1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> demandeService.createDemande(r, "owner@test.com"))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Civilité non trouvée");
        }

        @Test
        void cree_demande_et_retourne_response_avec_statut_PENDING_PAYMENT() {
            DemandeRequest r = validRequest();
            Civilite civ = new Civilite(1L, "M.");
            Pays pays = new Pays(1L, "France");
            DocumentType docType = new DocumentType();
            docType.setId(1L);
            docType.setLibelle("Acte de naissance");

            when(userRepository.findByEmail("owner@test.com")).thenReturn(Optional.of(owner));
            when(civiliteRepository.findById(1L)).thenReturn(Optional.of(civ));
            when(paysRepository.findById(1L)).thenReturn(Optional.of(pays));
            when(documentTypeRepository.findById(1L)).thenReturn(Optional.of(docType));
            when(demandeRepository.save(any(Demande.class))).thenAnswer(inv -> {
                Demande d = inv.getArgument(0);
                d.setId(100L);
                d.setStatus(Demande.Status.PENDING_PAYMENT);
                return d;
            });

            DemandeResponse result = demandeService.createDemande(r, "owner@test.com");

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(100L);
            assertThat(result.getStatus()).isEqualTo("PENDING_PAYMENT");
            assertThat(result.getFirstName()).isEqualTo("Jean");
            verify(demandeRepository).save(any(Demande.class));
        }
    }

    @Nested
    @DisplayName("updateDemandeStatus")
    class UpdateDemandeStatus {

        @Test
        void throw_quand_utilisateur_admin_non_trouve() {
            when(userRepository.findByEmail("admin@test.com")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> demandeService.updateDemandeStatus(1L, Demande.Status.REJECTED, "admin@test.com"))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Utilisateur non trouvé");
            verify(demandeRepository, never()).save(any());
        }

        @Test
        void throw_quand_role_insuffisant() {
            when(userRepository.findByEmail("owner@test.com")).thenReturn(Optional.of(owner));

            assertThatThrownBy(() -> demandeService.updateDemandeStatus(1L, Demande.Status.REJECTED, "owner@test.com"))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("administrateurs et agents");
        }

        @Test
        void throw_quand_email_non_verifie() {
            admin.setEmailVerified(false);
            when(userRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(admin));

            assertThatThrownBy(() -> demandeService.updateDemandeStatus(1L, Demande.Status.APPROVED, "admin@test.com"))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("email");
        }

        @Test
        void throw_quand_demande_inexistante() {
            when(userRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(admin));
            when(demandeRepository.findByIdWithAllRelations(99L)).thenReturn(null);

            assertThatThrownBy(() -> demandeService.updateDemandeStatus(99L, Demande.Status.REJECTED, "admin@test.com"))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Demande non trouvée");
        }

        @Test
        void retourne_meme_response_quand_statut_identique_idempotence() {
            when(userRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(admin));
            when(demandeRepository.findByIdWithAllRelations(1L)).thenReturn(demande);

            DemandeResponse result = demandeService.updateDemandeStatus(1L, Demande.Status.PENDING, "admin@test.com");

            assertThat(result).isNotNull();
            assertThat(result.getStatus()).isEqualTo("PENDING");
            verify(demandeRepository, never()).save(any());
        }

        @Test
        void met_a_jour_statut_rejet_et_sauvegarde() {
            when(userRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(admin));
            when(demandeRepository.findByIdWithAllRelations(1L)).thenReturn(demande);
            when(demandeRepository.save(any(Demande.class))).thenAnswer(inv -> inv.getArgument(0));

            DemandeResponse result = demandeService.updateDemandeStatus(1L, Demande.Status.REJECTED, "admin@test.com");

            assertThat(result).isNotNull();
            assertThat(result.getStatus()).isEqualTo("REJECTED");
            verify(demandeRepository).save(argThat(d -> d.getStatus() == Demande.Status.REJECTED));
        }

        @Test
        void accepte_agent_pour_mise_a_jour_statut() {
            User agent = new User();
            agent.setId(2L);
            agent.setEmail("agent@test.com");
            agent.setRole(User.Role.AGENT);
            agent.setEmailVerified(true);
            when(userRepository.findByEmail("agent@test.com")).thenReturn(Optional.of(agent));
            when(demandeRepository.findByIdWithAllRelations(1L)).thenReturn(demande);
            when(demandeRepository.save(any(Demande.class))).thenAnswer(inv -> inv.getArgument(0));

            DemandeResponse result = demandeService.updateDemandeStatus(1L, Demande.Status.APPROVED, "agent@test.com");

            assertThat(result.getStatus()).isEqualTo("APPROVED");
            verify(demandeRepository).save(any(Demande.class));
        }
    }
}
