package com.econsulat.service;

import com.econsulat.dto.BilanReportDTO;
import com.econsulat.dto.BilanReportRowDTO;
import com.econsulat.model.DocumentType;
import com.econsulat.model.Payment;
import com.econsulat.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service de construction du rapport de bilan comptable (demandes approuvées, paiements payés).
 */
@Service
public class BilanReportService {

    public static final String GROUP_BY_DAY = "DAY";
    public static final String GROUP_BY_MONTH = "MONTH";
    public static final String GROUP_BY_YEAR = "YEAR";

    private static final String DEFAULT_CURRENCY = "eur";

    private final PaymentRepository paymentRepository;

    public BilanReportService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    /**
     * Construit le rapport de bilan pour la granularité et la plage de dates données.
     *
     * @param groupBy  DAY, MONTH ou YEAR
     * @param dateFrom optionnel (null = pas de borne inférieure)
     * @param dateTo   optionnel (null = pas de borne supérieure)
     * @param locale   pour les libellés de période (mois)
     */
    /** Borne min pour filtrer par date (si dateFrom non fournie). */
    private static final LocalDateTime DEFAULT_FROM = LocalDateTime.of(2000, 1, 1, 0, 0);
    /** Borne max pour filtrer par date (si dateTo non fournie). */
    private static final LocalDateTime DEFAULT_TO = LocalDateTime.of(2100, 12, 31, 23, 59, 59);

    public BilanReportDTO buildReport(String groupBy, LocalDateTime dateFrom, LocalDateTime dateTo, Locale locale) {
        LocalDateTime from = dateFrom != null ? dateFrom : DEFAULT_FROM;
        LocalDateTime to = dateTo != null ? dateTo : DEFAULT_TO;
        List<Payment> payments = paymentRepository.findPaidPaymentsForApprovedDemandes(from, to);

        String normalizedGroupBy = groupBy == null || groupBy.isBlank() ? GROUP_BY_MONTH : groupBy.toUpperCase(Locale.ROOT);
        if (!GROUP_BY_DAY.equals(normalizedGroupBy) && !GROUP_BY_MONTH.equals(normalizedGroupBy) && !GROUP_BY_YEAR.equals(normalizedGroupBy)) {
            normalizedGroupBy = GROUP_BY_MONTH;
        }
        final String groupByKey = normalizedGroupBy;

        // Clé = (periodLabel, documentTypeId), valeur = (count, amountCents)
        Map<String, Map<Long, Aggregation>> byPeriodAndType = payments.stream()
                .filter(p -> p.getPaidAt() != null && p.getDemande() != null && p.getDemande().getDocumentType() != null)
                .collect(Collectors.groupingBy(
                        p -> getPeriodKey(p.getPaidAt(), groupByKey),
                        Collectors.groupingBy(
                                p -> p.getDemande().getDocumentType().getId(),
                                Collectors.reducing(
                                        new Aggregation(0L, 0L, null),
                                        p -> new Aggregation(1L, (long) (p.getAmountCents() != null ? p.getAmountCents() : 0), p.getDemande().getDocumentType()),
                                        (a, b) -> new Aggregation(a.count + b.count, a.amountCents + b.amountCents, a.docType != null ? a.docType : b.docType)
                                )
                        )
                ));

        List<BilanReportRowDTO> rows = new ArrayList<>();
        long totalCents = 0L;

        List<String> periodOrder = byPeriodAndType.keySet().stream().sorted().toList();
        for (String periodLabel : periodOrder) {
            Map<Long, Aggregation> byType = byPeriodAndType.get(periodLabel);
            List<Map.Entry<Long, Aggregation>> sortedTypes = byType.entrySet().stream()
                    .sorted(Comparator.comparing(e -> e.getValue().docType != null ? e.getValue().docType.getLibelle() : ""))
                    .toList();
            for (Map.Entry<Long, Aggregation> e : sortedTypes) {
                Aggregation agg = e.getValue();
                if (agg.docType == null) continue;
                String amountEuros = formatEuros(agg.amountCents);
                rows.add(new BilanReportRowDTO(
                        periodLabel,
                        agg.docType.getId(),
                        agg.docType.getLibelle(),
                        agg.count,
                        agg.amountCents,
                        amountEuros
                ));
                totalCents += agg.amountCents;
            }
        }

        return BilanReportDTO.builder()
                .groupBy(normalizedGroupBy)
                .dateFrom(dateFrom)
                .dateTo(dateTo)
                .generatedAt(LocalDateTime.now())
                .currency(DEFAULT_CURRENCY)
                .rows(rows)
                .totalAmountCents(totalCents)
                .totalAmountEuros(formatEuros(totalCents))
                .build();
    }

    private String getPeriodKey(LocalDateTime paidAt, String groupBy) {
        return switch (groupBy) {
            case GROUP_BY_DAY -> paidAt.toLocalDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ROOT));
            case GROUP_BY_MONTH -> YearMonth.from(paidAt).format(DateTimeFormatter.ofPattern("yyyy-MM", Locale.ROOT));
            case GROUP_BY_YEAR -> String.valueOf(paidAt.getYear());
            default -> paidAt.toLocalDate().toString();
        };
    }

    private static String formatEuros(long cents) {
        return String.format(Locale.ROOT, "%.2f", cents / 100.0);
    }

    private static class Aggregation {
        long count;
        long amountCents;
        DocumentType docType;

        Aggregation(long count, long amountCents, DocumentType docType) {
            this.count = count;
            this.amountCents = amountCents;
            this.docType = docType;
        }
    }
}
