package com.econsulat.service;

import com.econsulat.model.Citizen;
import com.econsulat.repository.CitizenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CitizenService")
class CitizenServiceTest {

    @Mock
    private CitizenRepository citizenRepository;

    @InjectMocks
    private CitizenService citizenService;

    private Citizen citizen;

    @BeforeEach
    void setUp() {
        citizen = new Citizen();
        citizen.setId(1L);
        citizen.setFirstName("Jean");
        citizen.setLastName("Dupont");
        citizen.setBirthDate(LocalDate.of(1990, 5, 15));
        citizen.setBirthPlace("Paris");
        citizen.setStatus(Citizen.Status.PENDING);
        citizen.setDocumentType("Acte de naissance");
    }

    @Nested
    @DisplayName("getAllCitizens / getCitizensByUser")
    class ListQueries {

        @Test
        void getAllCitizens_retourne_liste_du_repository() {
            when(citizenRepository.findAllByOrderByCreatedAtDesc()).thenReturn(List.of(citizen));

            List<Citizen> result = citizenService.getAllCitizens();

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getFirstName()).isEqualTo("Jean");
        }

        @Test
        void getCitizensByUser_retourne_liste_par_userId() {
            when(citizenRepository.findByUserId(10L)).thenReturn(List.of(citizen));

            List<Citizen> result = citizenService.getCitizensByUser(10L);

            verify(citizenRepository).findByUserId(10L);
            assertThat(result).hasSize(1);
        }
    }

    @Nested
    @DisplayName("createCitizen / getCitizenById / getCitizenByIdOrThrow")
    class CreateAndRead {

        @Test
        void createCitizen_sauvegarde_et_retourne_entite() {
            when(citizenRepository.save(any(Citizen.class))).thenAnswer(inv -> {
                Citizen c = inv.getArgument(0);
                c.setId(2L);
                return c;
            });

            Citizen result = citizenService.createCitizen(citizen);

            verify(citizenRepository).save(citizen);
            assertThat(result.getId()).isEqualTo(2L);
        }

        @Test
        void getCitizenById_retourne_Optional_vide_ou_present() {
            when(citizenRepository.findById(1L)).thenReturn(Optional.of(citizen));
            when(citizenRepository.findById(99L)).thenReturn(Optional.empty());

            assertThat(citizenService.getCitizenById(1L)).contains(citizen);
            assertThat(citizenService.getCitizenById(99L)).isEmpty();
        }

        @Test
        void getCitizenByIdOrThrow_retourne_entite_quand_trouve() {
            when(citizenRepository.findById(1L)).thenReturn(Optional.of(citizen));

            Citizen result = citizenService.getCitizenByIdOrThrow(1L);

            assertThat(result).isSameAs(citizen);
        }

        @Test
        void getCitizenByIdOrThrow_throw_quand_non_trouve() {
            when(citizenRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> citizenService.getCitizenByIdOrThrow(99L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Citizen not found");
        }
    }

    @Nested
    @DisplayName("updateCitizen / deleteCitizen")
    class UpdateAndDelete {

        @Test
        void updateCitizen_met_a_jour_et_sauvegarde() {
            Citizen details = new Citizen();
            details.setFirstName("Marie");
            details.setLastName("Martin");
            details.setBirthDate(LocalDate.of(1985, 1, 1));
            details.setBirthPlace("Lyon");
            details.setDocumentType("Certificat");
            details.setStatus(Citizen.Status.APPROVED);

            when(citizenRepository.findById(1L)).thenReturn(Optional.of(citizen));
            when(citizenRepository.save(any(Citizen.class))).thenAnswer(inv -> inv.getArgument(0));

            Citizen result = citizenService.updateCitizen(1L, details);

            assertThat(citizen.getFirstName()).isEqualTo("Marie");
            assertThat(citizen.getLastName()).isEqualTo("Martin");
            assertThat(citizen.getStatus()).isEqualTo(Citizen.Status.APPROVED);
            verify(citizenRepository).save(citizen);
        }

        @Test
        void updateCitizen_throw_quand_id_inexistant() {
            when(citizenRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> citizenService.updateCitizen(99L, citizen))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Citizen not found");
        }

        @Test
        void deleteCitizen_appelle_deleteById() {
            citizenService.deleteCitizen(1L);
            verify(citizenRepository).deleteById(1L);
        }
    }

    @Nested
    @DisplayName("getTotalCitizens / getTotalCitizensByUser")
    class Counts {

        @Test
        void getTotalCitizens_retourne_count_du_repository() {
            when(citizenRepository.count()).thenReturn(10L);
            assertThat(citizenService.getTotalCitizens()).isEqualTo(10L);
        }

        @Test
        void getTotalCitizensByUser_retourne_taille_liste_findByUserId() {
            when(citizenRepository.findByUserId(10L)).thenReturn(List.of(citizen, new Citizen()));
            assertThat(citizenService.getTotalCitizensByUser(10L)).isEqualTo(2);
        }
    }
}
