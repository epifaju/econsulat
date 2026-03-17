package com.econsulat.controller;

import com.econsulat.dto.CitizenHistoryDTO;
import com.econsulat.service.CitizenHistoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/me")
@CrossOrigin(origins = "http://localhost:5173")
public class MeController {

    private final CitizenHistoryService citizenHistoryService;

    public MeController(CitizenHistoryService citizenHistoryService) {
        this.citizenHistoryService = citizenHistoryService;
    }

    /**
     * Dossier citoyen / historique complet des demandes de l'utilisateur connecté.
     * N'utilise jamais d'ID utilisateur en paramètre : l'utilisateur est dérivé du JWT.
     */
    @GetMapping("/history")
    public ResponseEntity<CitizenHistoryDTO> getMyHistory() {
        String email = getCurrentUserEmail();
        CitizenHistoryDTO history = citizenHistoryService.getHistoryByEmail(email);
        return ResponseEntity.ok(history);
    }

    private String getCurrentUserEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new RuntimeException("Non authentifié");
        }
        return auth.getName();
    }
}
