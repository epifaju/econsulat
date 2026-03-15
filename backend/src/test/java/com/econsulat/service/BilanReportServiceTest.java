package com.econsulat.service;

import com.econsulat.dto.BilanReportDTO;
import com.econsulat.model.Demande;
import com.econsulat.model.DocumentType;
import com.econsulat.model.Payment;
import com.econsulat.repository.PaymentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("BilanReportService")
class BilanReportServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private BilanReportService bilanReportService;

    @Test
    @DisplayName("buildReport retourne un DTO avec total 0 quand aucun paiement")
    void buildReport_vide_retourne_total_zero() {
        when(paymentRepository.findPaidPaymentsForApprovedDemandes(any(), any())).thenReturn(List.of());

        BilanReportDTO report = bilanReportService.buildReport("MONTH", null, null, Locale.FRENCH);

        assertThat(report).isNotNull();
        assertThat(report.getGroupBy()).isEqualTo("MONTH");
        assertThat(report.getRows()).isEmpty();
        assertThat(report.getTotalAmountCents()).isZero();
        assertThat(report.getTotalAmountEuros()).isEqualTo("0.00");
    }

    @Test
    @DisplayName("buildReport agrège un paiement par type de document")
    void buildReport_avec_un_paiement_agrege_correctement() {
        DocumentType docType = new DocumentType();
        docType.setId(1L);
        docType.setLibelle("Acte de naissance");
        Demande demande = new Demande();
        demande.setDocumentType(docType);
        Payment payment = new Payment();
        payment.setPaidAt(LocalDateTime.of(2025, 1, 15, 10, 0));
        payment.setAmountCents(2500);
        payment.setDemande(demande);

        when(paymentRepository.findPaidPaymentsForApprovedDemandes(any(), any())).thenReturn(List.of(payment));

        BilanReportDTO report = bilanReportService.buildReport("MONTH", null, null, Locale.FRENCH);

        assertThat(report).isNotNull();
        assertThat(report.getRows()).hasSize(1);
        assertThat(report.getRows().get(0).getPeriodLabel()).isEqualTo("2025-01");
        assertThat(report.getRows().get(0).getDocumentTypeLibelle()).isEqualTo("Acte de naissance");
        assertThat(report.getRows().get(0).getCount()).isEqualTo(1);
        assertThat(report.getRows().get(0).getAmountCents()).isEqualTo(2500);
        assertThat(report.getTotalAmountCents()).isEqualTo(2500);
        assertThat(report.getTotalAmountEuros()).isEqualTo("25.00");
    }
}
