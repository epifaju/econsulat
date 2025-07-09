package com.econsulat.repository;

import com.econsulat.model.Demande;
import com.econsulat.model.User;
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
}