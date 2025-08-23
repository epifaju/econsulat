package com.econsulat.controller;

import com.econsulat.model.Notification;
import com.econsulat.model.User;
import com.econsulat.repository.NotificationRepository;
import com.econsulat.repository.UserRepository;
import com.econsulat.service.EmailNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final EmailNotificationService emailNotificationService;

    /**
     * Récupère toutes les notifications de l'utilisateur connecté
     */
    @GetMapping("/my")
    @PreAuthorize("hasAnyRole('USER', 'CITIZEN')")
    public ResponseEntity<List<Notification>> getMyNotifications() {
        try {
            String userEmail = getCurrentUserEmail();
            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

            List<Notification> notifications = notificationRepository
                    .findByUtilisateurOrderByDateEnvoiDesc(user);

            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des notifications : {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Récupère toutes les notifications d'une demande spécifique (admin/agent
     * seulement)
     */
    @GetMapping("/demande/{demandeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    public ResponseEntity<List<Notification>> getNotificationsByDemande(@PathVariable Long demandeId) {
        try {
            List<Notification> notifications = notificationRepository
                    .findByDemandeOrderByDateEnvoiDesc(demandeId);

            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des notifications de la demande {} : {}",
                    demandeId, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Récupère toutes les notifications (admin seulement)
     */
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Notification>> getAllNotifications() {
        try {
            List<Notification> notifications = notificationRepository.findAll();
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            log.error("Erreur lors de la récupération de toutes les notifications : {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Renvoie manuellement une notification (admin/agent seulement)
     */
    @PostMapping("/{notificationId}/resend")
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    public ResponseEntity<Map<String, String>> resendNotification(@PathVariable Long notificationId) {
        try {
            emailNotificationService.resendNotification(notificationId);
            return ResponseEntity.ok(Map.of("message", "Notification renvoyée avec succès"));
        } catch (Exception e) {
            log.error("Erreur lors du renvoi de la notification {} : {}", notificationId, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Erreur lors du renvoi : " + e.getMessage()));
        }
    }

    /**
     * Récupère le nombre de notifications de l'utilisateur connecté
     */
    @GetMapping("/count")
    @PreAuthorize("hasAnyRole('USER', 'CITIZEN')")
    public ResponseEntity<Map<String, Long>> getNotificationCount() {
        try {
            String userEmail = getCurrentUserEmail();
            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

            long count = notificationRepository.countByUtilisateur(user);
            return ResponseEntity.ok(Map.of("count", count));
        } catch (Exception e) {
            log.error("Erreur lors du comptage des notifications : {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    private String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
}
