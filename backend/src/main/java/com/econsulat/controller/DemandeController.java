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
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import com.econsulat.repository.DemandeRepository;

@RestController
@RequestMapping("/api/demandes")
@CrossOrigin(origins = "*")
public class DemandeController {

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
    public ResponseEntity<DemandeResponse> createDemande(@RequestBody DemandeRequest request) {
        String userEmail = getCurrentUserEmail();
        DemandeResponse response = demandeService.createDemande(request, userEmail);
        return ResponseEntity.ok(response);
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

            // Générer le document PDF
            GeneratedDocument generatedDocument = pdfDocumentService.generatePdfDocument(id, 1L, currentUser);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Document généré avec succès",
                    "documentId", generatedDocument.getId(),
                    "fileName", generatedDocument.getFileName()));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur lors de la génération: " + e.getMessage()));
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

            // Log pour debug
            System.out.println("🔍 Recherche document pour demande ID: " + id);
            System.out.println("🔍 Type de document de la demande: " + originalDemande.getDocumentType());
            System.out.println("🔍 Ordinal du type: " + originalDemande.getDocumentType().ordinal());

            // Récupérer le document généré en utilisant le vrai type de document de la
            // demande
            // Essayer d'abord avec l'ordinal + 1, puis avec l'ordinal direct
            GeneratedDocument generatedDocument = null;

            // Première tentative : ordinal + 1 (comme avant)
            try {
                generatedDocument = generatedDocumentRepository
                        .findPdfDocumentByDemandeAndType(id, originalDemande.getDocumentType().ordinal() + 1L)
                        .orElse(null);
                System.out.println("🔍 Première tentative avec ordinal + 1: "
                        + (originalDemande.getDocumentType().ordinal() + 1L));
            } catch (Exception e) {
                System.out.println("⚠️ Erreur première tentative: " + e.getMessage());
            }

            // Si pas trouvé, essayer avec l'ordinal direct
            if (generatedDocument == null) {
                try {
                    generatedDocument = generatedDocumentRepository
                            .findPdfDocumentByDemandeAndType(id, (long) originalDemande.getDocumentType().ordinal())
                            .orElse(null);
                    System.out.println("🔍 Deuxième tentative avec ordinal direct: "
                            + originalDemande.getDocumentType().ordinal());
                } catch (Exception e) {
                    System.out.println("⚠️ Erreur deuxième tentative: " + e.getMessage());
                }
            }

            // Si toujours pas trouvé, essayer de trouver n'importe quel document PDF pour
            // cette demande
            if (generatedDocument == null) {
                try {
                    List<GeneratedDocument> allDocs = generatedDocumentRepository.findByDemandeId(id);
                    System.out.println("🔍 Documents trouvés pour la demande: " + allDocs.size());
                    for (GeneratedDocument doc : allDocs) {
                        System.out.println("  - ID: " + doc.getId() + ", Nom: " + doc.getFileName() + ", Type: "
                                + (doc.getDocumentType() != null ? doc.getDocumentType().getId() : "null"));
                    }

                    // Chercher le premier document PDF
                    generatedDocument = allDocs.stream()
                            .filter(doc -> doc.getFileName() != null
                                    && doc.getFileName().toLowerCase().endsWith(".pdf"))
                            .findFirst()
                            .orElse(null);

                    if (generatedDocument != null) {
                        System.out.println(
                                "✅ Document PDF trouvé par recherche alternative: " + generatedDocument.getFileName());
                    }
                } catch (Exception e) {
                    System.out.println("⚠️ Erreur recherche alternative: " + e.getMessage());
                }
            }

            if (generatedDocument == null) {
                System.out.println("❌ Aucun document PDF trouvé pour la demande " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Aucun document PDF trouvé pour cette demande"));
            }

            System.out.println("✅ Document trouvé: " + generatedDocument.getFileName());

            // Télécharger le document PDF
            byte[] documentBytes = pdfDocumentService.downloadPdfDocument(generatedDocument.getId());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", generatedDocument.getFileName());

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(documentBytes);

        } catch (Exception e) {
            System.err.println("❌ Erreur lors du téléchargement: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur lors du téléchargement: " + e.getMessage()));
        }
    }

    @GetMapping("/civilites")
    public ResponseEntity<List<Civilite>> getCivilites() {
        List<Civilite> civilites = civiliteRepository.findAll();
        return ResponseEntity.ok(civilites);
    }

    @GetMapping("/pays")
    public ResponseEntity<List<Pays>> getPays() {
        List<Pays> pays = paysRepository.findAll();
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
            // En cas d'erreur, retourner l'enum comme fallback
            List<Map<String, String>> types = new ArrayList<>();
            for (Demande.DocumentType type : Demande.DocumentType.values()) {
                Map<String, String> typeMap = new HashMap<>();
                typeMap.put("value", type.name());
                typeMap.put("label", type.getDisplayName());
                types.add(typeMap);
            }
            return ResponseEntity.ok(types);
        }
    }

    private String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
}