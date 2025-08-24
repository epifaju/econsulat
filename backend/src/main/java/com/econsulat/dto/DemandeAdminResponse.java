package com.econsulat.dto;

import com.econsulat.model.Demande;
import com.econsulat.model.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class DemandeAdminResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String documentType;
    private String documentTypeDisplay;
    private Long documentTypeId; // ID numérique du type de document en base
    private String status;
    private String statusDisplay;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UserInfo user;
    private List<String> documentFiles;
    private boolean hasGeneratedDocuments;

    // Champs pour l'édition
    private Long civiliteId;
    private String civilite;
    private LocalDate birthDate;
    private String birthPlace;
    private Long birthCountryId;
    private String birthCountry;
    private String streetName;
    private String streetNumber;
    private String boxNumber;
    private String postalCode;
    private String city;
    private Long countryId;
    private String country;
    private String fatherFirstName;
    private String fatherLastName;
    private LocalDate fatherBirthDate;
    private String fatherBirthPlace;
    private Long fatherBirthCountryId;
    private String fatherBirthCountry;
    private String motherFirstName;
    private String motherLastName;
    private LocalDate motherBirthDate;
    private String motherBirthPlace;
    private Long motherBirthCountryId;
    private String motherBirthCountry;

    public static class UserInfo {
        private Long id;
        private String firstName;
        private String lastName;
        private String email;
        private String role;

        public UserInfo() {
        }

        public UserInfo(User user) {
            this.id = user.getId();
            this.firstName = user.getFirstName();
            this.lastName = user.getLastName();
            this.email = user.getEmail();
            this.role = user.getRole().name();
        }

        // Getters et Setters
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }
    }

    public DemandeAdminResponse() {
    }

    public DemandeAdminResponse(Demande demande) {
        this.id = demande.getId();
        this.firstName = demande.getFirstName();
        this.lastName = demande.getLastName();

        // ✅ NOUVEAU : Utiliser la relation JPA au lieu de l'enum
        this.documentType = demande.getDocumentType().getLibelle(); // Libellé depuis la base
        this.documentTypeDisplay = demande.getDocumentType().getLibelle(); // Même libellé pour l'affichage
        this.documentTypeId = demande.getDocumentType().getId(); // ID direct depuis la relation

        this.status = demande.getStatus().name();
        this.statusDisplay = demande.getStatus().getDisplayName();
        this.createdAt = demande.getCreatedAt();
        this.updatedAt = demande.getUpdatedAt();
        this.user = new UserInfo(demande.getUser());
        this.documentFiles = demande.getDocumentsPath() != null ? List.of(demande.getDocumentsPath().split(","))
                : List.of();
        this.hasGeneratedDocuments = false; // Sera mis à jour par le service

        // SUPPRIMER : Plus besoin de mapping manuel
        // this.documentTypeId = getDocumentTypeIdFromEnum(demande.getDocumentType());

        // Champs pour l'édition
        this.civiliteId = demande.getCivilite().getId();
        this.civilite = demande.getCivilite().getLibelle();
        this.birthDate = demande.getBirthDate();
        this.birthPlace = demande.getBirthPlace();
        this.birthCountryId = demande.getBirthCountry().getId();
        this.birthCountry = demande.getBirthCountry().getLibelle();
        this.streetName = demande.getAdresse().getStreetName();
        this.streetNumber = demande.getAdresse().getStreetNumber();
        this.boxNumber = demande.getAdresse().getBoxNumber();
        this.postalCode = demande.getAdresse().getPostalCode();
        this.city = demande.getAdresse().getCity();
        this.countryId = demande.getAdresse().getCountry().getId();
        this.country = demande.getAdresse().getCountry().getLibelle();
        this.fatherFirstName = demande.getFatherFirstName();
        this.fatherLastName = demande.getFatherLastName();
        this.fatherBirthDate = demande.getFatherBirthDate();
        this.fatherBirthPlace = demande.getFatherBirthPlace();
        this.fatherBirthCountryId = demande.getFatherBirthCountry().getId();
        this.fatherBirthCountry = demande.getFatherBirthCountry().getLibelle();
        this.motherFirstName = demande.getMotherFirstName();
        this.motherLastName = demande.getMotherLastName();
        this.motherBirthDate = demande.getMotherBirthDate();
        this.motherBirthPlace = demande.getMotherBirthPlace();
        this.motherBirthCountryId = demande.getMotherBirthCountry().getId();
        this.motherBirthCountry = demande.getMotherBirthCountry().getLibelle();
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getDocumentTypeDisplay() {
        return documentTypeDisplay;
    }

    public void setDocumentTypeDisplay(String documentTypeDisplay) {
        this.documentTypeDisplay = documentTypeDisplay;
    }

    public Long getDocumentTypeId() {
        return documentTypeId;
    }

    public void setDocumentTypeId(Long documentTypeId) {
        this.documentTypeId = documentTypeId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusDisplay() {
        return statusDisplay;
    }

    public void setStatusDisplay(String statusDisplay) {
        this.statusDisplay = statusDisplay;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public UserInfo getUser() {
        return user;
    }

    public void setUser(UserInfo user) {
        this.user = user;
    }

    public List<String> getDocumentFiles() {
        return documentFiles;
    }

    public void setDocumentFiles(List<String> documentFiles) {
        this.documentFiles = documentFiles;
    }

    public boolean isHasGeneratedDocuments() {
        return hasGeneratedDocuments;
    }

    public void setHasGeneratedDocuments(boolean hasGeneratedDocuments) {
        this.hasGeneratedDocuments = hasGeneratedDocuments;
    }

    // Getters et Setters pour les champs d'édition
    public Long getCiviliteId() {
        return civiliteId;
    }

    public void setCiviliteId(Long civiliteId) {
        this.civiliteId = civiliteId;
    }

    public String getCivilite() {
        return civilite;
    }

    public void setCivilite(String civilite) {
        this.civilite = civilite;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getBirthPlace() {
        return birthPlace;
    }

    public void setBirthPlace(String birthPlace) {
        this.birthPlace = birthPlace;
    }

    public Long getBirthCountryId() {
        return birthCountryId;
    }

    public void setBirthCountryId(Long birthCountryId) {
        this.birthCountryId = birthCountryId;
    }

    public String getBirthCountry() {
        return birthCountry;
    }

    public void setBirthCountry(String birthCountry) {
        this.birthCountry = birthCountry;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public String getStreetNumber() {
        return streetNumber;
    }

    public void setStreetNumber(String streetNumber) {
        this.streetNumber = streetNumber;
    }

    public String getBoxNumber() {
        return boxNumber;
    }

    public void setBoxNumber(String boxNumber) {
        this.boxNumber = boxNumber;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getFatherFirstName() {
        return fatherFirstName;
    }

    public void setFatherFirstName(String fatherFirstName) {
        this.fatherFirstName = fatherFirstName;
    }

    public String getFatherLastName() {
        return fatherLastName;
    }

    public void setFatherLastName(String fatherLastName) {
        this.fatherLastName = fatherLastName;
    }

    public LocalDate getFatherBirthDate() {
        return fatherBirthDate;
    }

    public void setFatherBirthDate(LocalDate fatherBirthDate) {
        this.fatherBirthDate = fatherBirthDate;
    }

    public String getFatherBirthPlace() {
        return fatherBirthPlace;
    }

    public void setFatherBirthPlace(String fatherBirthPlace) {
        this.fatherBirthPlace = fatherBirthPlace;
    }

    public Long getFatherBirthCountryId() {
        return fatherBirthCountryId;
    }

    public void setFatherBirthCountryId(Long fatherBirthCountryId) {
        this.fatherBirthCountryId = fatherBirthCountryId;
    }

    public String getFatherBirthCountry() {
        return fatherBirthCountry;
    }

    public void setFatherBirthCountry(String fatherBirthCountry) {
        this.fatherBirthCountry = fatherBirthCountry;
    }

    public String getMotherFirstName() {
        return motherFirstName;
    }

    public void setMotherFirstName(String motherFirstName) {
        this.motherFirstName = motherFirstName;
    }

    public String getMotherLastName() {
        return motherLastName;
    }

    public void setMotherLastName(String motherLastName) {
        this.motherLastName = motherLastName;
    }

    public LocalDate getMotherBirthDate() {
        return motherBirthDate;
    }

    public void setMotherBirthDate(LocalDate motherBirthDate) {
        this.motherBirthDate = motherBirthDate;
    }

    public String getMotherBirthPlace() {
        return motherBirthPlace;
    }

    public void setMotherBirthPlace(String motherBirthPlace) {
        this.motherBirthPlace = motherBirthPlace;
    }

    public Long getMotherBirthCountryId() {
        return motherBirthCountryId;
    }

    public void setMotherBirthCountryId(Long motherBirthCountryId) {
        this.motherBirthCountryId = motherBirthCountryId;
    }

    public String getMotherBirthCountry() {
        return motherBirthCountry;
    }

    public void setMotherBirthCountry(String motherBirthCountry) {
        this.motherBirthCountry = motherBirthCountry;
    }
}