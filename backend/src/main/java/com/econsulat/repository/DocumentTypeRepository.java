package com.econsulat.repository;

import com.econsulat.model.DocumentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentTypeRepository extends JpaRepository<DocumentType, Long> {

    Optional<DocumentType> findByLibelle(String libelle);

    List<DocumentType> findByIsActiveTrue();

    @Query("SELECT dt FROM DocumentType dt WHERE dt.isActive = true AND dt.libelle LIKE %:searchTerm%")
    List<DocumentType> findActiveBySearchTerm(@Param("searchTerm") String searchTerm);

    boolean existsByLibelle(String libelle);

    boolean existsByLibelleAndIdNot(String libelle, Long id);
}