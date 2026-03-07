package com.econsulat.service;

import com.econsulat.dto.DemandeRequest;
import com.econsulat.dto.DemandeResponse;
import com.econsulat.model.*;
import com.econsulat.repository.*;
import com.econsulat.service.EmailNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

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

        @Autowired
        private EmailNotificationService emailNotificationService;

        @Autowired
        private TransactionTemplate transactionTemplate;

        @Autowired
        private DocumentTypeRepository documentTypeRepository;

        public DemandeResponse createDemande(DemandeRequest request, String userEmail) {
                try {
                        // ✅ VALIDATION PRÉALABLE DES DONNÉES
                        validateDemandeRequest(request);

                        User user = userRepository.findByEmail(userEmail)
                                        .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

                        // Récupérer les entités liées avec validation
                        Civilite civilite = civiliteRepository.findById(request.getCiviliteId())
                                        .orElseThrow(() -> new RuntimeException(
                                                        "Civilité non trouvée avec l'ID: " + request.getCiviliteId()));

                        Pays birthCountry = paysRepository.findById(request.getBirthCountryId())
                                        .orElseThrow(() -> new RuntimeException(
                                                        "Pays de naissance non trouvé avec l'ID: "
                                                                        + request.getBirthCountryId()));

                        Pays country = paysRepository.findById(request.getCountryId())
                                        .orElseThrow(() -> new RuntimeException(
                                                        "Pays non trouvé avec l'ID: " + request.getCountryId()));

                        Pays fatherBirthCountry = paysRepository.findById(request.getFatherBirthCountryId())
                                        .orElseThrow(() -> new RuntimeException(
                                                        "Pays de naissance du père non trouvé avec l'ID: "
                                                                        + request.getFatherBirthCountryId()));

                        Pays motherBirthCountry = paysRepository.findById(request.getMotherBirthCountryId())
                                        .orElseThrow(() -> new RuntimeException(
                                                        "Pays de naissance de la mère non trouvé avec l'ID: "
                                                                        + request.getMotherBirthCountryId()));

                        DocumentType documentType = documentTypeRepository.findById(request.getDocumentTypeId())
                                        .orElseThrow(() -> new RuntimeException(
                                                        "Type de document non trouvé avec l'ID: "
                                                                        + request.getDocumentTypeId()));

                        // Créer l'adresse avec validation
                        Adresse adresse = new Adresse();
                        adresse.setStreetName(request.getStreetName().trim());
                        adresse.setStreetNumber(request.getStreetNumber().trim());
                        adresse.setBoxNumber(request.getBoxNumber() != null ? request.getBoxNumber().trim() : null);
                        adresse.setPostalCode(request.getPostalCode().trim());
                        adresse.setCity(request.getCity().trim());
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
                        demande.setDocumentType(documentType);
                        demande.setDocumentsPath(request.getDocumentFiles() != null
                                        ? String.join(",", request.getDocumentFiles())
                                        : "");
                        demande.setUser(user);
                        demande.setStatus(Demande.Status.PENDING_PAYMENT);

                        Demande savedDemande = demandeRepository.save(demande);
                        return convertToResponse(savedDemande);

                } catch (Exception e) {
                        // Log de l'erreur pour debugging
                        System.err.println("Erreur lors de la création de la demande: " + e.getMessage());
                        e.printStackTrace();
                        throw e;
                }
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

        /**
         * Met à jour le statut d'une demande et envoie une notification par email
         */
        @Transactional
        public DemandeResponse updateDemandeStatus(Long demandeId, Demande.Status newStatus, String adminEmail) {
                System.out.println("🔍 DemandeService - Début updateDemandeStatus");
                System.out.println("🔍 DemandeService - Demande ID: " + demandeId);
                System.out.println("🔍 DemandeService - Nouveau statut: " + newStatus);
                System.out.println("🔍 DemandeService - Email admin: " + adminEmail);

                // Vérifier que l'utilisateur est admin ou agent
                User admin = userRepository.findByEmail(adminEmail)
                                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé: " + adminEmail));

                System.out.println("👤 DemandeService - Utilisateur trouvé: " + admin.getEmail());
                System.out.println("👤 DemandeService - Rôle: " + admin.getRole());
                System.out.println("👤 DemandeService - Email vérifié: " + admin.getEmailVerified());

                if (!admin.getRole().equals(User.Role.ADMIN) && !admin.getRole().equals(User.Role.AGENT)) {
                        System.err.println("❌ DemandeService - Rôle insuffisant: " + admin.getRole());
                        throw new RuntimeException(
                                        "Seuls les administrateurs et agents peuvent modifier le statut des demandes");
                }

                if (!admin.getEmailVerified()) {
                        System.err.println("❌ DemandeService - Email non vérifié pour: " + admin.getEmail());
                        throw new RuntimeException("L'email de l'utilisateur doit être vérifié");
                }

                System.out.println("✅ DemandeService - Vérifications de rôle et email OK");

                // Récupérer la demande avec toutes ses relations initialisées
                Demande demande = demandeRepository.findByIdWithAllRelations(demandeId);
                if (demande == null) {
                        throw new RuntimeException("Demande non trouvée: " + demandeId);
                }

                System.out.println("📋 DemandeService - Demande trouvée, statut actuel: " + demande.getStatus());

                // Vérifier si le statut a réellement changé
                if (demande.getStatus().equals(newStatus)) {
                        System.out.println("ℹ️ DemandeService - Statut identique, pas de changement nécessaire");
                        return convertToResponse(demande);
                }

                // Sauvegarder l'ancien statut pour la notification
                Demande.Status oldStatus = demande.getStatus();

                // Mettre à jour le statut
                demande.setStatus(newStatus);
                demande.setUpdatedAt(java.time.LocalDateTime.now());

                Demande updatedDemande = demandeRepository.save(demande);
                System.out.println("✅ DemandeService - Statut mis à jour en base: " + oldStatus + " → " + newStatus);

                // Envoyer la notification par email de manière asynchrone pour éviter les
                // conflits de transaction
                // La notification sera envoyée après la validation de la transaction principale
                try {
                        // Utiliser une transaction séparée pour la notification
                        transactionTemplate.executeWithoutResult(status -> {
                                try {
                                        // Utiliser la demande déjà chargée avec toutes ses relations
                                        emailNotificationService.sendStatusChangeNotification(updatedDemande,
                                                        demande.getUser(), newStatus);
                                        System.out.println("📧 DemandeService - Notification email envoyée");
                                } catch (Exception e) {
                                        System.err.println(
                                                        "⚠️ DemandeService - Erreur lors de l'envoi de la notification : "
                                                                        + e.getMessage());
                                        // Ne pas faire échouer la transaction de notification
                                }
                        });
                } catch (Exception e) {
                        System.err.println(
                                        "⚠️ DemandeService - Erreur lors de l'exécution de la transaction de notification : "
                                                        + e.getMessage());
                        // Ne pas faire échouer la transaction principale
                }

                return convertToResponse(updatedDemande);
        }

        // ✅ NOUVELLE MÉTHODE DE VALIDATION
        private void validateDemandeRequest(DemandeRequest request) {
                if (request.getDocumentTypeId() == null) {
                        throw new RuntimeException("L'ID du type de document est obligatoire");
                }

                if (request.getStreetName() == null || request.getStreetName().trim().isEmpty()) {
                        throw new RuntimeException("Le nom de rue est obligatoire");
                }

                if (request.getStreetNumber() == null || request.getStreetNumber().trim().isEmpty()) {
                        throw new RuntimeException("Le numéro de rue est obligatoire");
                }

                if (request.getPostalCode() == null || request.getPostalCode().trim().isEmpty()) {
                        throw new RuntimeException("Le code postal est obligatoire");
                }

                if (request.getCity() == null || request.getCity().trim().isEmpty()) {
                        throw new RuntimeException("La ville est obligatoire");
                }

                if (request.getFirstName() == null || request.getFirstName().trim().isEmpty()) {
                        throw new RuntimeException("Le prénom est obligatoire");
                }

                if (request.getLastName() == null || request.getLastName().trim().isEmpty()) {
                        throw new RuntimeException("Le nom de famille est obligatoire");
                }

                if (request.getBirthDate() == null) {
                        throw new RuntimeException("La date de naissance est obligatoire");
                }

                if (request.getBirthPlace() == null || request.getBirthPlace().trim().isEmpty()) {
                        throw new RuntimeException("Le lieu de naissance est obligatoire");
                }
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

                // ✅ CORRIGÉ : Utiliser les méthodes de l'entité DocumentType
                response.setDocumentType(demande.getDocumentType().getId().toString());
                response.setDocumentTypeDisplay(demande.getDocumentType().getLibelle());

                response.setStatus(demande.getStatus().name());
                response.setStatusDisplay(demande.getStatus().getDisplayName());
                response.setPaid(demande.getStatus() != Demande.Status.PENDING_PAYMENT);
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