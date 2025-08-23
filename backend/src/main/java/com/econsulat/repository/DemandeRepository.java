package com.econsulat.repository;

import com.econsulat.model.Demande;
import com.econsulat.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DemandeRepository extends JpaRepository<Demande, Long> {

        List<Demande> findByUserOrderByCreatedAtDesc(User user);

        @Query("SELECT d FROM Demande d WHERE d.user.id = :userId ORDER BY d.createdAt DESC")
        List<Demande> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);

        List<Demande> findByStatusOrderByCreatedAtDesc(Demande.Status status);

        // Méthodes pour l'interface admin
        Page<Demande> findByStatus(Demande.Status status, Pageable pageable);

        Page<Demande> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
                        String firstName, String lastName, Pageable pageable);

        long countByStatus(Demande.Status status);

        long countByUser(User user);

        // Méthode pour récupérer une demande avec toutes ses relations initialisées
        @Query("SELECT d FROM Demande d " +
                        "LEFT JOIN FETCH d.civilite " +
                        "LEFT JOIN FETCH d.birthCountry " +
                        "LEFT JOIN FETCH d.adresse " +
                        "LEFT JOIN FETCH d.fatherBirthCountry " +
                        "LEFT JOIN FETCH d.motherBirthCountry " +
                        "LEFT JOIN FETCH d.user " +
                        "WHERE d.id = :id")
        Demande findByIdWithAllRelations(@Param("id") Long id);
}