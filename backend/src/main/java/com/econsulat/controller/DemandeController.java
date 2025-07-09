package com.econsulat.controller;

import com.econsulat.dto.DemandeRequest;
import com.econsulat.dto.DemandeResponse;
import com.econsulat.model.Civilite;
import com.econsulat.model.Demande;
import com.econsulat.model.Pays;
import com.econsulat.repository.CiviliteRepository;
import com.econsulat.repository.PaysRepository;
import com.econsulat.service.DemandeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/demandes")
@CrossOrigin(origins = "*")
public class DemandeController {

    @Autowired
    private DemandeService demandeService;

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