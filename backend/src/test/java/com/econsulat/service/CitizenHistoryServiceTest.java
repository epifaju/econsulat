package com.econsulat.service;

import com.econsulat.dto.CitizenHistoryDTO;
import com.econsulat.dto.DemandeHistoryDTO;
import com.econsulat.model.Demande;
import com.econsulat.model.DocumentType;
import com.econsulat.model.GeneratedDocument;
import com.econsulat.model.Payment;
import com.econsulat.model.User;
import com.econsulat.repository.DemandeRepository;
import com.econsulat.repository.GeneratedDocumentRepository;
import com.econsulat.repository.PaymentRepository;
import com.econsulat.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("CitizenHistoryService")
class CitizenHistoryServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private DemandeRepository demandeRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private GeneratedDocumentRepository generatedDocumentRepository;

    @InjectMocks
    private CitizenHistoryService citizenHistoryService;

    private User user;
    private Demande demande;

    private void initUserAndDemande() {
        user = new User();
        user.setId(1L);
        user.setFirstName("Jean");
        user.setLastName("Dupont");
        user.setEmail("jean@test.com");

        DocumentType docType = new DocumentType();
        docType.setId(1L);
        docType.setLibelle("Acte de naissance");

        demande = new Demande();
        demande.setId(10L);
        demande.setUser(user);
        demande.setDocumentType(docType);
        demande.setStatus(Demande.Status.PENDING);
        demande.setCreatedAt(LocalDateTime.now());
    }

    @Nested
    @DisplayName("getHistoryByEmail")
    class GetHistoryByEmail {

        @Test
        @DisplayName("lève une exception si l'utilisateur n'existe pas")
        void throw_quand_utilisateur_inexistant() {
            when(userRepository.findByEmail("inconnu@test.com")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> citizenHistoryService.getHistoryByEmail("inconnu@test.com"))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Utilisateur non trouvé");
            verify(demandeRepository, never()).findByUserOrderByCreatedAtDesc(any());
        }

        @Test
        @DisplayName("retourne un DTO avec liste vide quand l'utilisateur n'a aucune demande")
        void retourne_dto_vide_quand_aucune_demande() {
            initUserAndDemande();
            when(userRepository.findByEmail("jean@test.com")).thenReturn(Optional.of(user));
            when(demandeRepository.findByUserOrderByCreatedAtDesc(user)).thenReturn(List.of());

            CitizenHistoryDTO result = citizenHistoryService.getHistoryByEmail("jean@test.com");

            assertThat(result).isNotNull();
            assertThat(result.getUserEmail()).isEqualTo("jean@test.com");
            assertThat(result.getUserFirstName()).isEqualTo("Jean");
            assertThat(result.getUserLastName()).isEqualTo("Dupont");
            assertThat(result.getTotalDemandes()).isZero();
            assertThat(result.getTotalPaidCents()).isZero();
            assertThat(result.getTotalPaidEuros()).isEqualTo("0.00");
            assertThat(result.getDemandes()).isEmpty();
        }

        @Test
        @DisplayName("retourne un DTO avec une demande, un paiement et un document")
        void retourne_dto_avec_demande_paiement_et_document() {
            initUserAndDemande();
            when(userRepository.findByEmail("jean@test.com")).thenReturn(Optional.of(user));
            when(demandeRepository.findByUserOrderByCreatedAtDesc(user)).thenReturn(List.of(demande));

            Payment payment = new Payment();
            payment.setId(100L);
            payment.setAmountCents(2500);
            payment.setPaidAt(LocalDateTime.now());
            payment.setStatus(Payment.PaymentStatus.PAID);
            when(paymentRepository.findByDemandeId(10L)).thenReturn(Optional.of(payment));

            GeneratedDocument doc = new GeneratedDocument();
            doc.setId(200L);
            doc.setFileName("acte_naissance_10.pdf");
            when(generatedDocumentRepository.findByDemandeId(10L)).thenReturn(List.of(doc));

            CitizenHistoryDTO result = citizenHistoryService.getHistoryByEmail("jean@test.com");

            assertThat(result).isNotNull();
            assertThat(result.getTotalDemandes()).isEqualTo(1);
            assertThat(result.getTotalPaidCents()).isEqualTo(2500);
            assertThat(result.getTotalPaidEuros()).isEqualTo("25.00");
            assertThat(result.getDemandes()).hasSize(1);

            DemandeHistoryDTO row = result.getDemandes().get(0);
            assertThat(row.getId()).isEqualTo(10L);
            assertThat(row.getDocumentTypeLibelle()).isEqualTo("Acte de naissance");
            assertThat(row.getStatus()).isEqualTo("PENDING");
            assertThat(row.getTotalPaidCents()).isEqualTo(2500L);
            assertThat(row.getTotalPaidEuros()).isEqualTo("25.00");
            assertThat(row.getPayments()).hasSize(1);
            assertThat(row.getPayments().get(0).getAmountCents()).isEqualTo(2500);
            assertThat(row.getDocuments()).hasSize(1);
            assertThat(row.getDocuments().get(0).getFileName()).isEqualTo("acte_naissance_10.pdf");
        }
    }

    @Nested
    @DisplayName("getHistoryByUserId")
    class GetHistoryByUserId {

        @Test
        @DisplayName("lève une exception si l'utilisateur n'existe pas")
        void throw_quand_utilisateur_inexistant() {
            when(userRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> citizenHistoryService.getHistoryByUserId(999L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Utilisateur non trouvé");
        }

        @Test
        @DisplayName("retourne le même format que getHistoryByEmail")
        void retourne_historique_pour_user_id() {
            initUserAndDemande();
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(demandeRepository.findByUserOrderByCreatedAtDesc(user)).thenReturn(List.of(demande));
            when(paymentRepository.findByDemandeId(10L)).thenReturn(Optional.empty());
            when(generatedDocumentRepository.findByDemandeId(10L)).thenReturn(List.of());

            CitizenHistoryDTO result = citizenHistoryService.getHistoryByUserId(1L);

            assertThat(result).isNotNull();
            assertThat(result.getUserEmail()).isEqualTo("jean@test.com");
            assertThat(result.getTotalDemandes()).isEqualTo(1);
            assertThat(result.getDemandes()).hasSize(1);
            assertThat(result.getDemandes().get(0).getTotalPaidCents()).isZero();
        }
    }
}
