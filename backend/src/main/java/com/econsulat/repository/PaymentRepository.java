package com.econsulat.repository;

import com.econsulat.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByStripeSessionId(String stripeSessionId);

    Optional<Payment> findByDemandeId(Long demandeId);

    /**
     * Paiements payés pour des demandes approuvées, filtrés par plage paid_at (from/to non null).
     */
    @Query("SELECT DISTINCT p FROM Payment p JOIN FETCH p.demande d JOIN FETCH d.documentType " +
           "WHERE d.status = 'APPROVED' AND p.status = 'PAID' AND p.paidAt IS NOT NULL " +
           "AND p.paidAt >= :from AND p.paidAt <= :to")
    List<Payment> findPaidPaymentsForApprovedDemandes(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);
}
