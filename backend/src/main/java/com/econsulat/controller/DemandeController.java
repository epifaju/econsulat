package com.econsulat.controller;

import com.econsulat.dto.DemandeRequest;
import com.econsulat.dto.DemandeResponse;
import com.econsulat.model.Civilite;
import com.econsulat.model.Demande;
import com.econsulat.model.GeneratedDocument;
import com.econsulat.model.Pays;
import com.econsulat.model.User;
import com.econsulat.repository.CiviliteRepository;
import com.econsulat.repository.GeneratedDocumentRepository;
import com.econsulat.repository.PaysRepository;
import com.econsulat.repository.UserRepository;
import com.econsulat.service.DemandeService;
import com.econsulat.service.DocumentGenerationService;
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

@RestController
@RequestMapping("/api/demandes")
@CrossOrigin(origins = "*")
public class DemandeController {

    @Autowired
    private DemandeService demandeService;

    @Autowired
    private DocumentGenerationService documentGenerationService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GeneratedDocumentRepository generatedDocumentRepository;

    @Autowired
    private CiviliteRepository civiliteRepository;

    @Autowired
    private PaysRepository paysRepository;

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

            // Générer le document
            GeneratedDocument generatedDocument = documentGenerationService.generateDocument(id, 1L, currentUser);

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

            // Récupérer le document généré
            GeneratedDocument generatedDocument = generatedDocumentRepository
                    .findByDemandeAndDocumentType(id, 1L)
                    .orElse(null);

            if (generatedDocument == null) {
                return ResponseEntity.notFound().build();
            }

            // Télécharger le document
            byte[] documentBytes = documentGenerationService.downloadDocument(generatedDocument.getId());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", generatedDocument.getFileName());

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(documentBytes);

        } catch (Exception e) {
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
    public ResponseEntity<Demande.DocumentType[]> getDocumentTypes() {
        Demande.DocumentType[] types = Demande.DocumentType.values();
        return ResponseEntity.ok(types);
    }

    private String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
}