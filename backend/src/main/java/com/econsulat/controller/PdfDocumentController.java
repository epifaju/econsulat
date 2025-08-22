package com.econsulat.controller;

import com.econsulat.model.GeneratedDocument;
import com.econsulat.model.User;
import com.econsulat.repository.UserRepository;
import com.econsulat.service.PdfDocumentService;
import com.econsulat.dto.GeneratedDocumentResponse;
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
import java.util.Map;

@RestController
@RequestMapping("/api/admin/pdf-documents")
@PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
@CrossOrigin(origins = "http://localhost:5173")
public class PdfDocumentController {

    @Autowired
    private PdfDocumentService pdfDocumentService;

    @Autowired
    private UserRepository userRepository;

    /**
     * Génère un document PDF avec filigrane pour une demande
     */
    @PostMapping("/generate")
    public ResponseEntity<?> generatePdfDocument(
            @RequestParam Long demandeId,
            @RequestParam Long documentTypeId) {

        try {
            User currentUser = getCurrentUser();
            GeneratedDocument document = pdfDocumentService.generatePdfDocument(
                    demandeId, documentTypeId, currentUser);

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
                    document.getDocumentType() != null ? document.getDocumentType().getId() : documentTypeId,
                    document.getCreatedBy().getEmail());

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            System.err.println("Erreur lors de la génération PDF: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Erreur lors de la génération", "message", e.getMessage()));
        } catch (Exception e) {
            System.err.println("Erreur interne lors de la génération PDF: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Erreur interne du serveur", "message", e.getMessage()));
        }
    }

    /**
     * Télécharge un document PDF généré
     */
    @GetMapping("/download/{documentId}")
    public ResponseEntity<ByteArrayResource> downloadPdfDocument(@PathVariable Long documentId) {
        try {
            byte[] documentBytes = pdfDocumentService.downloadPdfDocument(documentId);
            ByteArrayResource resource = new ByteArrayResource(documentBytes);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"document.pdf\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .contentLength(documentBytes.length)
                    .body(resource);

        } catch (IOException e) {
            System.err.println("Erreur lors du téléchargement PDF: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            System.err.println("Erreur inattendue lors du téléchargement PDF: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
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