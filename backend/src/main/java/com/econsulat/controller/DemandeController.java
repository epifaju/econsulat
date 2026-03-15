package com.econsulat.controller;

import com.econsulat.dto.DemandeRequest;
import com.econsulat.dto.DemandeResponse;
import com.econsulat.model.Civilite;
import com.econsulat.model.Demande;
import com.econsulat.model.GeneratedDocument;
import com.econsulat.model.Pays;
import com.econsulat.model.User;
import com.econsulat.model.DocumentType;
import com.econsulat.repository.CiviliteRepository;
import com.econsulat.repository.GeneratedDocumentRepository;
import com.econsulat.repository.PaysRepository;
import com.econsulat.repository.DocumentTypeRepository;
import com.econsulat.repository.UserRepository;
import com.econsulat.service.DemandeService;
import com.econsulat.service.PdfDocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import com.econsulat.repository.DemandeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/demandes")
@CrossOrigin(origins = "*")
public class DemandeController {

    private static final Logger log = LoggerFactory.getLogger(DemandeController.class);

    @Autowired
    private DemandeService demandeService;

    @Autowired
    private PdfDocumentService pdfDocumentService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GeneratedDocumentRepository generatedDocumentRepository;

    @Autowired
    private CiviliteRepository civiliteRepository;

    @Autowired
    private PaysRepository paysRepository;

    @Autowired
    private DocumentTypeRepository documentTypeRepository;

    @Autowired
    private DemandeRepository demandeRepository;

    @PostMapping
    public ResponseEntity<?> createDemande(@RequestBody DemandeRequest request) {
        log.debug("createDemande - documentTypeId: {}", request.getDocumentTypeId());

        try {
            String userEmail = getCurrentUserEmail();
            DemandeResponse response = demandeService.createDemande(request, userEmail);
            log.debug("Demande créée pour utilisateur: {}", userEmail);
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            log.warn("Erreur de validation createDemande: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Erreur de validation");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("timestamp", System.currentTimeMillis());
            errorResponse.put("type", "VALIDATION_ERROR");
            errorResponse.put("path", "/api/demandes");

            return ResponseEntity.badRequest().body(errorResponse); // Retourner la vraie réponse d'erreur

        } catch (Exception e) {
            log.error("Erreur inattendue createDemande", e);
            throw e; // Laisser le GlobalExceptionHandler gérer
        }
    }

    @GetMapping("/my")
    public ResponseEntity<List<DemandeResponse>> getMyDemandes() {
        String userEmail = getCurrentUserEmail();
        List<DemandeResponse> demandes = demandeService.getDemandesByUser(userEmail);
        return ResponseEntity.ok(demandes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DemandeResponse> getDemandeById(@PathVariable Long id) {
        String userEmail = getCurrentUserEmail();
        DemandeResponse demande = demandeService.getDemandeById(id, userEmail);
        return ResponseEntity.ok(demande);
    }

    @GetMapping("/all")
    public ResponseEntity<List<DemandeResponse>> getAllDemandes() {
        List<DemandeResponse> demandes = demandeService.getAllDemandes();
        return ResponseEntity.ok(demandes);
    }

    /**
     * Génère un document pour une demande de l'utilisateur connecté
     */
    @PostMapping("/{id}/generate-document")
    public ResponseEntity<?> generateDocumentForDemande(@PathVariable Long id) {
        try {
            String userEmail = getCurrentUserEmail();

            // Vérifier que la demande appartient à l'utilisateur et est approuvée
            DemandeResponse demande = demandeService.getDemandeById(id, userEmail);

            if (!"APPROVED".equals(demande.getStatus())) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "La demande doit être approuvée pour générer un document"));
            }

            // Récupérer l'utilisateur depuis la base de données
            User currentUser = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

            Locale locale = LocaleContextHolder.getLocale();
            GeneratedDocument generatedDocument = pdfDocumentService.generatePdfDocument(id, 1L, currentUser, locale);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Document généré avec succès",
                    "documentId", generatedDocument.getId(),
                    "fileName", generatedDocument.getFileName()));

        } catch (Exception e) {
            log.error("Erreur génération document pour demande {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur lors de la génération"));
        }
    }

    /**
     * Télécharge un document généré pour une demande de l'utilisateur connecté
     */
    @GetMapping("/{id}/download-document")
    public ResponseEntity<?> downloadDocumentForDemande(@PathVariable Long id) {
        try {
            String userEmail = getCurrentUserEmail();

            // Vérifier que la demande appartient à l'utilisateur
            DemandeResponse demande = demandeService.getDemandeById(id, userEmail);

            // Récupérer la demande originale pour accéder au type de document
            Demande originalDemande = demandeRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Demande non trouvée"));

            log.debug("Recherche document pour demande ID: {}, type: {}", id, originalDemande.getDocumentType().getLibelle());

            GeneratedDocument generatedDocument = null;

            try {
                Long documentTypeId = originalDemande.getDocumentType().getId();
                generatedDocument = generatedDocumentRepository
                        .findPdfDocumentByDemandeAndType(id, documentTypeId)
                        .orElse(null);
            } catch (Exception e) {
                log.debug("Recherche document par type: {}", e.getMessage());
            }

            if (generatedDocument == null) {
                try {
                    List<GeneratedDocument> allDocs = generatedDocumentRepository.findByDemandeId(id);
                    generatedDocument = allDocs.stream()
                            .filter(doc -> doc.getFileName() != null
                                    && doc.getFileName().toLowerCase().endsWith(".pdf"))
                            .findFirst()
                            .orElse(null);
                } catch (Exception e) {
                    log.debug("Recherche alternative documents: {}", e.getMessage());
                }
            }

            if (generatedDocument == null) {
                log.warn("Aucun document PDF trouvé pour la demande {}", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Aucun document PDF trouvé pour cette demande"));
            }

            // Télécharger le document PDF
            byte[] documentBytes = pdfDocumentService.downloadPdfDocument(generatedDocument.getId());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", generatedDocument.getFileName());

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(documentBytes);

        } catch (Exception e) {
            log.error("Erreur téléchargement document demande {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur lors du téléchargement"));
        }
    }

    @GetMapping("/civilites")
    public ResponseEntity<List<Civilite>> getCivilites() {
        List<Civilite> civilites = civiliteRepository.findAll();
        return ResponseEntity.ok(civilites);
    }

    @GetMapping("/pays")
    public ResponseEntity<List<Pays>> getPays() {
        List<Pays> pays = paysRepository.findAllOrdered();
        return ResponseEntity.ok(pays);
    }

    @GetMapping("/document-types")
    public ResponseEntity<List<Map<String, String>>> getDocumentTypes() {
        try {
            // ✅ Récupérer les types de documents depuis la base de données
            List<DocumentType> documentTypes = documentTypeRepository.findByIsActiveTrue();

            List<Map<String, String>> types = new ArrayList<>();
            for (DocumentType docType : documentTypes) {
                Map<String, String> typeMap = new HashMap<>();
                typeMap.put("value", docType.getId().toString());
                typeMap.put("label", docType.getLibelle());
                types.add(typeMap);
            }

            return ResponseEntity.ok(types);
        } catch (Exception e) {
            log.warn("Erreur récupération types de documents: {}", e.getMessage());
            return ResponseEntity.ok(new ArrayList<>());
        }
    }

    /**
     * Met à jour le statut d'une demande (admin/agent seulement)
     */
    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    public ResponseEntity<DemandeResponse> updateDemandeStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> statusUpdate) {
        try {
            String newStatusStr = statusUpdate.get("status");
            if (newStatusStr == null) {
                log.warn("Mise à jour statut - statut manquant pour demande {}", id);
                return ResponseEntity.badRequest().body(null);
            }

            Demande.Status newStatus;
            try {
                newStatus = Demande.Status.valueOf(newStatusStr);
            } catch (IllegalArgumentException e) {
                log.warn("Mise à jour statut - statut invalide: {} pour demande {}", newStatusStr, id);
                return ResponseEntity.badRequest().body(null);
            }

            String adminEmail = getCurrentUserEmail();
            DemandeResponse updatedDemande = demandeService.updateDemandeStatus(id, newStatus, adminEmail);
            log.debug("Statut demande {} mis à jour en {}", id, newStatus);

            return ResponseEntity.ok(updatedDemande);
        } catch (Exception e) {
            log.error("Erreur mise à jour statut demande {}", id, e);
            return ResponseEntity.badRequest().body(null);
        }
    }

    private String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
}