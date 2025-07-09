package com.econsulat.controller;

import com.econsulat.model.Citizen;
import com.econsulat.service.CitizenService;
import com.econsulat.service.PassportDocumentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/passport")
@RequiredArgsConstructor
@Slf4j
public class PassportController {

    private final PassportDocumentService passportDocumentService;
    private final CitizenService citizenService;

    /**
     * Génère un document de passeport Word
     */
    @PostMapping("/generate/{citizenId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> generatePassportDocument(@PathVariable Long citizenId) {
        try {
            // Récupérer le citoyen
            Citizen citizen = citizenService.getCitizenByIdOrThrow(citizenId);

            // Générer le document
            String filename = passportDocumentService.generatePassportDocument(citizen);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Document de passeport généré avec succès");
            response.put("filename", filename);
            response.put("citizenId", citizenId);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Erreur lors de la génération du document de passeport", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", "Erreur lors de la génération du document: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Génère un document de passeport PDF
     */
    @PostMapping("/generate/{citizenId}/pdf")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> generatePassportDocumentPdf(@PathVariable Long citizenId) {
        try {
            // Récupérer le citoyen
            Citizen citizen = citizenService.getCitizenByIdOrThrow(citizenId);

            // Pour l'instant, générer un document Word puis le convertir en PDF
            // TODO: Implémenter la conversion PDF
            String filename = passportDocumentService.generatePassportDocument(citizen);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Document de passeport PDF généré avec succès");
            response.put("filename", filename);
            response.put("citizenId", citizenId);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Erreur lors de la génération du document de passeport PDF", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", "Erreur lors de la génération du document PDF: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Télécharge un document de passeport
     */
    @GetMapping("/download/{filename}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> downloadPassportDocument(@PathVariable String filename) {
        try {
            byte[] documentBytes = passportDocumentService.getDocument(filename);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", filename);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(documentBytes);

        } catch (IOException e) {
            log.error("Erreur lors du téléchargement du document de passeport", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Supprime un document de passeport
     */
    @DeleteMapping("/delete/{filename}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> deletePassportDocument(@PathVariable String filename) {
        try {
            passportDocumentService.deleteDocument(filename);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Document supprimé avec succès");
            response.put("filename", filename);

            return ResponseEntity.ok(response);

        } catch (IOException e) {
            log.error("Erreur lors de la suppression du document de passeport", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", "Erreur lors de la suppression du document: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Test simple pour vérifier que l'endpoint fonctionne
     */
    @GetMapping("/test")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> testEndpoint() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Endpoint de passeport fonctionnel");
        response.put("timestamp", java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
        return ResponseEntity.ok(response);
    }
}