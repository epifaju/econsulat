package com.econsulat.service;

import com.econsulat.dto.DemandeAdminResponse;
import com.econsulat.dto.UserAdminResponse;
import com.econsulat.model.Demande;
import com.econsulat.model.GeneratedDocument;
import com.econsulat.model.User;
import com.econsulat.repository.DemandeRepository;
import com.econsulat.repository.GeneratedDocumentRepository;
import com.econsulat.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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