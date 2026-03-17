package com.econsulat.service;

import com.econsulat.dto.CitizenHistoryDTO;
import com.econsulat.dto.DemandeHistoryDTO;
import com.econsulat.dto.DocumentSummaryDTO;
import com.econsulat.dto.PaymentSummaryDTO;
import com.econsulat.model.Demande;
import com.econsulat.model.GeneratedDocument;
import com.econsulat.model.Payment;
import com.econsulat.model.User;
import com.econsulat.repository.DemandeRepository;
import com.econsulat.repository.GeneratedDocumentRepository;
import com.econsulat.repository.PaymentRepository;
import com.econsulat.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Service du dossier citoyen / historique complet des demandes.
 */
@Service
public class CitizenHistoryService {

    private final UserRepository userRepository;
    private final DemandeRepository demandeRepository;
    private final PaymentRepository paymentRepository;
    private final GeneratedDocumentRepository generatedDocumentRepository;

    public CitizenHistoryService(UserRepository userRepository,
                                 DemandeRepository demandeRepository,
                                 PaymentRepository paymentRepository,
                                 GeneratedDocumentRepository generatedDocumentRepository) {
        this.userRepository = userRepository;
        this.demandeRepository = demandeRepository;
        this.paymentRepository = paymentRepository;
        this.generatedDocumentRepository = generatedDocumentRepository;
    }

    /**
     * Historique complet pour l'utilisateur connecté (par email).
     */
    @Transactional(readOnly = true)
    public CitizenHistoryDTO getHistoryByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        return buildHistory(user);
    }

    /**
     * Historique complet d'un utilisateur par son ID (usage admin).
     */
    @Transactional(readOnly = true)
    public CitizenHistoryDTO getHistoryByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        return buildHistory(user);
    }

    private CitizenHistoryDTO buildHistory(User user) {
        List<Demande> demandes = demandeRepository.findByUserOrderByCreatedAtDesc(user);
        List<DemandeHistoryDTO> dtos = new ArrayList<>();
        long totalPaidCents = 0;

        for (Demande d : demandes) {
            DemandeHistoryDTO dto = toDemandeHistoryDTO(d);
            dtos.add(dto);
            totalPaidCents += dto.getTotalPaidCents() != null ? dto.getTotalPaidCents() : 0;
        }

        CitizenHistoryDTO result = new CitizenHistoryDTO();
        result.setUserFirstName(user.getFirstName());
        result.setUserLastName(user.getLastName());
        result.setUserEmail(user.getEmail());
        result.setTotalDemandes(demandes.size());
        result.setTotalPaidCents(totalPaidCents);
        result.setTotalPaidEuros(formatEuros(totalPaidCents));
        result.setDemandes(dtos);
        return result;
    }

    private DemandeHistoryDTO toDemandeHistoryDTO(Demande d) {
        DemandeHistoryDTO dto = new DemandeHistoryDTO();
        dto.setId(d.getId());
        dto.setDocumentTypeId(d.getDocumentType() != null ? d.getDocumentType().getId() : null);
        dto.setDocumentTypeLibelle(d.getDocumentType() != null ? d.getDocumentType().getLibelle() : null);
        dto.setCreatedAt(d.getCreatedAt());
        dto.setStatus(d.getStatus() != null ? d.getStatus().name() : null);

        List<PaymentSummaryDTO> payments = new ArrayList<>();
        long demandePaidCents = 0;
        Optional<Payment> paymentOpt = paymentRepository.findByDemandeId(d.getId());
        if (paymentOpt.isPresent()) {
            Payment p = paymentOpt.get();
            PaymentSummaryDTO ps = new PaymentSummaryDTO(
                    p.getId(),
                    p.getAmountCents(),
                    formatEuros(p.getAmountCents() != null ? p.getAmountCents() : 0),
                    p.getPaidAt(),
                    p.getStatus() != null ? p.getStatus().name() : null
            );
            payments.add(ps);
            if (p.getStatus() == Payment.PaymentStatus.PAID && p.getAmountCents() != null) {
                demandePaidCents = p.getAmountCents();
            }
        }
        dto.setPayments(payments);
        dto.setTotalPaidCents(demandePaidCents);
        dto.setTotalPaidEuros(formatEuros(demandePaidCents));

        List<GeneratedDocument> docs = generatedDocumentRepository.findByDemandeId(d.getId());
        List<DocumentSummaryDTO> docSummaries = docs.stream()
                .map(doc -> new DocumentSummaryDTO(doc.getId(), doc.getFileName()))
                .toList();
        dto.setDocuments(docSummaries);

        return dto;
    }

    private static String formatEuros(long cents) {
        return String.format(Locale.ROOT, "%.2f", cents / 100.0);
    }
}
