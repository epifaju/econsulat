package com.econsulat.repository;

import com.econsulat.model.Notification;
import com.econsulat.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /**
     * Trouve toutes les notifications d'un utilisateur (avec demande et utilisateur chargés pour le DTO).
     */
    List<Notification> findByUtilisateurOrderByDateEnvoiDesc(User utilisateur);

    @Query("SELECT n FROM Notification n JOIN FETCH n.demande JOIN FETCH n.utilisateur WHERE n.utilisateur = :user ORDER BY n.dateEnvoi DESC")
    List<Notification> findByUtilisateurWithRelationsOrderByDateEnvoiDesc(@Param("user") User user);

    /**
     * Trouve toutes les notifications d'une demande par son ID
     */
    @Query("SELECT n FROM Notification n WHERE n.demande.id = :demandeId ORDER BY n.dateEnvoi DESC")
    List<Notification> findByDemandeOrderByDateEnvoiDesc(@Param("demandeId") Long demandeId);

    /**
     * Trouve la dernière notification envoyée pour une demande
     */
    @Query("SELECT n FROM Notification n WHERE n.demande.id = :demandeId ORDER BY n.dateEnvoi DESC")
    List<Notification> findLatestByDemande(@Param("demandeId") Long demandeId);

    /**
     * Trouve toutes les notifications d'un utilisateur avec pagination
     */
    @Query("SELECT n FROM Notification n WHERE n.utilisateur.id = :userId ORDER BY n.dateEnvoi DESC")
    List<Notification> findByUtilisateurIdOrderByDateEnvoiDesc(@Param("userId") Long userId);

    /**
     * Compte le nombre de notifications d'un utilisateur
     */
    long countByUtilisateur(User utilisateur);
}
