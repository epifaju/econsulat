package com.econsulat.controller;

import com.econsulat.model.GeneratedDocument;
import com.econsulat.model.User;
import com.econsulat.repository.UserRepository;
import com.econsulat.service.DocumentGenerationService;
import com.econsulat.dto.GeneratedDocumentResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/user/documents")
@CrossOrigin(origins = "http://localhost:5173")
public class UserDocumentController {

    @Autowired
    private DocumentGenerationService documentGenerationService;

    @Autowired
    private UserRepository userRepository;

    /**
     * Génère un document pour une demande de l'utilisateur connecté
     */
    @PostMapping("/generate")
    public ResponseEntity<?> generateDocument(
            @RequestParam Long demandeId,
            @RequestParam Long documentTypeId) {

        try {
            User currentUser = getCurrentUser();

            // Vérifier que l'utilisateur a le droit de générer ce document
            // (la vérification se fait dans le service)

            GeneratedDocument document = documentGenerationService.generateDocument(demandeId, documentTypeId,
                    currentUser);

            // Convertir en DTO pour éviter les problèmes de sérialisation
            GeneratedDocumentResponse response = new GeneratedDocumentResponse(
                    document.getId(),
                    document.getFileName(),
                    document.getFilePath(),
                    document.getStatus(),
                    document.getCreatedAt(),
                    document.getExpiresAt(),
                    document.getDownloadedAt(),
                    document.getDemande().getId(),
                    document.getDocumentType().getId(),
                    document.getCreatedBy().getEmail());

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Erreur lors de la génération", "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Erreur interne du serveur", "message", e.getMessage()));
        }
    }

    /**
     * Télécharge un document généré par l'utilisateur connecté
     */
    @GetMapping("/download/{documentId}")
    public ResponseEntity<ByteArrayResource> downloadDocument(@PathVariable Long documentId) {
        try {
            // Vérifier que l'utilisateur a le droit de télécharger ce document
            User currentUser = getCurrentUser();

            // TODO: Ajouter une vérification que le document appartient à l'utilisateur

            byte[] documentBytes = documentGenerationService.downloadDocument(documentId);
            ByteArrayResource resource = new ByteArrayResource(documentBytes);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"document.docx\"")
                    .contentType(MediaType
                            .parseMediaType("application/vnd.openxmlformats-officedocument.wordprocessingml.document"))
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

        // Récupérer l'utilisateur depuis la base de données
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé: " + email));
    }
}