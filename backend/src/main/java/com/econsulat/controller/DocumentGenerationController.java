package com.econsulat.controller;

import com.econsulat.model.GeneratedDocument;
import com.econsulat.model.User;
import com.econsulat.service.DocumentGenerationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/admin/documents")
@PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
@CrossOrigin(origins = "http://localhost:5173")
public class DocumentGenerationController {

    @Autowired
    private DocumentGenerationService documentGenerationService;

    @PostMapping("/generate")
    public ResponseEntity<GeneratedDocument> generateDocument(
            @RequestParam Long demandeId,
            @RequestParam Long documentTypeId) {

        User currentUser = getCurrentUser();
        GeneratedDocument document = documentGenerationService.generateDocument(demandeId, documentTypeId, currentUser);
        return ResponseEntity.ok(document);
    }

    @GetMapping("/download/{documentId}")
    public ResponseEntity<ByteArrayResource> downloadDocument(@PathVariable Long documentId) {
        try {
            byte[] documentBytes = documentGenerationService.downloadDocument(documentId);
            ByteArrayResource resource = new ByteArrayResource(documentBytes);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"document.pdf\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .contentLength(documentBytes.length)
                    .body(resource);

        } catch (IOException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Utilisateur non authentifié");
        }
        String email = authentication.getName();
        // Pour l'instant, on retourne un utilisateur factice avec le rôle ADMIN
        // En production, vous devriez récupérer l'utilisateur depuis la base de données
        User user = new User();
        user.setEmail(email);
        user.setRole(User.Role.ADMIN);
        return user;
    }
}