package com.econsulat.service;

import com.econsulat.dto.DemandeAdminResponse;
import com.econsulat.dto.DemandeRequest;
import com.econsulat.dto.UserAdminResponse;
import com.econsulat.model.*;
import com.econsulat.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {

    @Autowired
    private DemandeRepository demandeRepository;

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

    // Gestion des demandes
    public Page<DemandeAdminResponse> getAllDemandes(Pageable pageable) {
        Page<Demande> demandes = demandeRepository.findAll(pageable);
        return demandes.map(this::convertToDemandeAdminResponse);
    }

    public Page<DemandeAdminResponse> getDemandesByStatus(String status, Pageable pageable) {
        Page<Demande> demandes = demandeRepository.findByStatus(Demande.Status.valueOf(status.toUpperCase()), pageable);
        return demandes.map(this::convertToDemandeAdminResponse);
    }

    public Page<DemandeAdminResponse> searchDemandes(String searchTerm, Pageable pageable) {
        Page<Demande> demandes = demandeRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
                searchTerm, searchTerm, pageable);
        return demandes.map(this::convertToDemandeAdminResponse);
    }

    public DemandeAdminResponse getDemandeById(Long id) {
        Demande demande = demandeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Demande non trouvée"));
        return convertToDemandeAdminResponse(demande);
    }

    public Demande updateDemandeStatus(Long id, String status) {
        Demande demande = demandeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Demande non trouvée"));
        demande.setStatus(Demande.Status.valueOf(status.toUpperCase()));
        return demandeRepository.save(demande);
    }

    @Transactional
    public DemandeAdminResponse updateDemande(Long id, DemandeRequest request) {
        Demande demande = demandeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Demande non trouvée"));

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

        // Mettre à jour l'adresse
        Adresse adresse = demande.getAdresse();
        adresse.setStreetName(request.getStreetName());
        adresse.setStreetNumber(request.getStreetNumber());
        adresse.setBoxNumber(request.getBoxNumber());
        adresse.setPostalCode(request.getPostalCode());
        adresse.setCity(request.getCity());
        adresse.setCountry(country);

        // Mettre à jour la demande
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

        Demande savedDemande = demandeRepository.save(demande);
        return convertToDemandeAdminResponse(savedDemande);
    }

    @Transactional
    public void deleteDemande(Long id) {
        Demande demande = demandeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Demande non trouvée"));

        // Supprimer d'abord les documents générés associés
        List<GeneratedDocument> generatedDocs = generatedDocumentRepository.findByDemandeId(id);
        generatedDocumentRepository.deleteAll(generatedDocs);

        // Supprimer la demande
        demandeRepository.delete(demande);
    }

    // Gestion des utilisateurs
    public Page<UserAdminResponse> getAllUsers(Pageable pageable) {
        Page<User> users = userRepository.findAll(pageable);
        return users.map(this::convertToUserAdminResponse);
    }

    public Page<UserAdminResponse> searchUsers(String searchTerm, Pageable pageable) {
        Page<User> users = userRepository
                .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                        searchTerm, searchTerm, searchTerm, pageable);
        return users.map(this::convertToUserAdminResponse);
    }

    public UserAdminResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        return convertToUserAdminResponse(user);
    }

    // Statistiques
    public long getTotalDemandes() {
        return demandeRepository.count();
    }

    public long getDemandesByStatus(String status) {
        return demandeRepository.countByStatus(Demande.Status.valueOf(status.toUpperCase()));
    }

    public long getTotalUsers() {
        return userRepository.count();
    }

    public long getTotalGeneratedDocuments() {
        return generatedDocumentRepository.count();
    }

    // Méthodes de conversion
    private DemandeAdminResponse convertToDemandeAdminResponse(Demande demande) {
        DemandeAdminResponse response = new DemandeAdminResponse(demande);

        // Vérifier s'il y a des documents générés
        List<GeneratedDocument> generatedDocs = generatedDocumentRepository.findByDemandeId(demande.getId());
        response.setHasGeneratedDocuments(!generatedDocs.isEmpty());

        return response;
    }

    private UserAdminResponse convertToUserAdminResponse(User user) {
        UserAdminResponse response = new UserAdminResponse(user);

        // Compter les demandes de l'utilisateur
        long demandesCount = demandeRepository.countByUser(user);
        response.setDemandesCount((int) demandesCount);

        return response;
    }
}