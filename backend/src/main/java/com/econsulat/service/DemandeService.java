package com.econsulat.service;

import com.econsulat.dto.DemandeRequest;
import com.econsulat.dto.DemandeResponse;
import com.econsulat.model.*;
import com.econsulat.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class DemandeService {

    @Autowired
    private DemandeRepository demandeRepository;

    @Autowired
    private CiviliteRepository civiliteRepository;

    @Autowired
    private PaysRepository paysRepository;

    @Autowired
    private UserRepository userRepository;

    public DemandeResponse createDemande(DemandeRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Récupérer les entités liées
        Civilite civilite = civiliteRepository.findById(request.getCiviliteId())
                .orElseThrow(() -> new RuntimeException("Civilité non trouvée"));

        Pays birthCountry = paysRepository.findById(request.getBirthCountryId())
                .orElseThrow(() -> new RuntimeException("Pays de naissance non trouvé"));

        Pays country = paysRepository.findById(request.getCountryId())
                .orElseThrow(() -> new RuntimeException("Pays non trouvé"));

        Pays fatherBirthCountry = paysRepository.findById(request.getFatherBirthCountryId())
                .orElseThrow(() -> new RuntimeException("Pays de naissance du père non trouvé"));

        Pays motherBirthCountry = paysRepository.findById(request.getMotherBirthCountryId())
                .orElseThrow(() -> new RuntimeException("Pays de naissance de la mère non trouvé"));

        // Créer l'adresse
        Adresse adresse = new Adresse();
        adresse.setStreetName(request.getStreetName());
        adresse.setStreetNumber(request.getStreetNumber());
        adresse.setBoxNumber(request.getBoxNumber());
        adresse.setPostalCode(request.getPostalCode());
        adresse.setCity(request.getCity());
        adresse.setCountry(country);

        // Créer la demande
        Demande demande = new Demande();
        demande.setCivilite(civilite);
        demande.setFirstName(request.getFirstName());
        demande.setLastName(request.getLastName());
        demande.setBirthDate(request.getBirthDate());
        demande.setBirthPlace(request.getBirthPlace());
        demande.setBirthCountry(birthCountry);
        demande.setAdresse(adresse);
        demande.setFatherFirstName(request.getFatherFirstName());
        demande.setFatherLastName(request.getFatherLastName());
        demande.setFatherBirthDate(request.getFatherBirthDate());
        demande.setFatherBirthPlace(request.getFatherBirthPlace());
        demande.setFatherBirthCountry(fatherBirthCountry);
        demande.setMotherFirstName(request.getMotherFirstName());
        demande.setMotherLastName(request.getMotherLastName());
        demande.setMotherBirthDate(request.getMotherBirthDate());
        demande.setMotherBirthPlace(request.getMotherBirthPlace());
        demande.setMotherBirthCountry(motherBirthCountry);
        demande.setDocumentType(request.getDocumentType());
        demande.setDocumentsPath(String.join(",", request.getDocumentFiles()));
        demande.setUser(user);

        Demande savedDemande = demandeRepository.save(demande);
        return convertToResponse(savedDemande);
    }

    public List<DemandeResponse> getDemandesByUser(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        List<Demande> demandes = demandeRepository.findByUserOrderByCreatedAtDesc(user);
        return demandes.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public DemandeResponse getDemandeById(Long id, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        Demande demande = demandeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Demande non trouvée"));

        // Vérifier que l'utilisateur peut accéder à cette demande
        if (!demande.getUser().getId().equals(user.getId()) && !user.getRole().equals(User.Role.ADMIN)) {
            throw new RuntimeException("Accès non autorisé");
        }

        return convertToResponse(demande);
    }

    public List<DemandeResponse> getAllDemandes() {
        List<Demande> demandes = demandeRepository.findAll();
        return demandes.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    private DemandeResponse convertToResponse(Demande demande) {
        DemandeResponse response = new DemandeResponse();
        response.setId(demande.getId());
        response.setCivilite(demande.getCivilite().getLibelle());
        response.setFirstName(demande.getFirstName());
        response.setLastName(demande.getLastName());
        response.setBirthDate(demande.getBirthDate());
        response.setBirthPlace(demande.getBirthPlace());
        response.setBirthCountry(demande.getBirthCountry().getLibelle());
        response.setStreetName(demande.getAdresse().getStreetName());
        response.setStreetNumber(demande.getAdresse().getStreetNumber());
        response.setBoxNumber(demande.getAdresse().getBoxNumber());
        response.setPostalCode(demande.getAdresse().getPostalCode());
        response.setCity(demande.getAdresse().getCity());
        response.setCountry(demande.getAdresse().getCountry().getLibelle());
        response.setFatherFirstName(demande.getFatherFirstName());
        response.setFatherLastName(demande.getFatherLastName());
        response.setFatherBirthDate(demande.getFatherBirthDate());
        response.setFatherBirthPlace(demande.getFatherBirthPlace());
        response.setFatherBirthCountry(demande.getFatherBirthCountry().getLibelle());
        response.setMotherFirstName(demande.getMotherFirstName());
        response.setMotherLastName(demande.getMotherLastName());
        response.setMotherBirthDate(demande.getMotherBirthDate());
        response.setMotherBirthPlace(demande.getMotherBirthPlace());
        response.setMotherBirthCountry(demande.getMotherBirthCountry().getLibelle());
        response.setDocumentType(demande.getDocumentType().name());
        response.setDocumentTypeDisplay(demande.getDocumentType().getDisplayName());
        response.setStatus(demande.getStatus().name());
        response.setStatusDisplay(demande.getStatus().getDisplayName());
        response.setCreatedAt(demande.getCreatedAt());
        response.setUpdatedAt(demande.getUpdatedAt());
        response.setUserEmail(demande.getUser().getEmail());
        response.setUserName(demande.getUser().getFirstName() + " " + demande.getUser().getLastName());

        if (demande.getDocumentsPath() != null && !demande.getDocumentsPath().isEmpty()) {
            response.setDocumentFiles(List.of(demande.getDocumentsPath().split(",")));
        }

        return response;
    }
}