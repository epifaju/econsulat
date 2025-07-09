package com.econsulat.repository;

import com.econsulat.model.GeneratedDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface GeneratedDocumentRepository extends JpaRepository<GeneratedDocument, Long> {

    List<GeneratedDocument> findByDemandeId(Long demandeId);

    List<GeneratedDocument> findByCreatedByEmail(String email);

    @Query("SELECT gd FROM GeneratedDocument gd WHERE gd.demande.id = :demandeId AND gd.documentType.id = :documentTypeId")
    Optional<GeneratedDocument> findByDemandeAndDocumentType(@Param("demandeId") Long demandeId,
            @Param("documentTypeId") Long documentTypeId);

    @Query("SELECT gd FROM GeneratedDocument gd WHERE gd.status = :status")
    Page<GeneratedDocument> findByStatus(@Param("status") String status, Pageable pageable);

    @Query("SELECT gd FROM GeneratedDocument gd WHERE gd.createdAt BETWEEN :startDate AND :endDate")
    List<GeneratedDocument> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT gd FROM GeneratedDocument gd WHERE gd.expiresAt < :now")
    List<GeneratedDocument> findExpiredDocuments(@Param("now") LocalDateTime now);

    @Query("SELECT COUNT(gd) FROM GeneratedDocument gd WHERE gd.createdAt >= :startDate")
    Long countDocumentsGeneratedSince(@Param("startDate") LocalDateTime startDate);
}