package com.econsulat.dto;

import com.econsulat.model.Demande;
import com.econsulat.model.User;

import java.time.LocalDateTime;
import java.util.List;

public class DemandeAdminResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String documentType;
    private String documentTypeDisplay;
    private String status;
    private String statusDisplay;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UserInfo user;
    private List<String> documentFiles;
    private boolean hasGeneratedDocuments;

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
        this.documentType = demande.getDocumentType().name();
        this.documentTypeDisplay = demande.getDocumentType().getDisplayName();
        this.status = demande.getStatus().name();
        this.statusDisplay = demande.getStatus().getDisplayName();
        this.createdAt = demande.getCreatedAt();
        this.updatedAt = demande.getUpdatedAt();
        this.user = new UserInfo(demande.getUser());
        this.documentFiles = demande.getDocumentsPath() != null ? List.of(demande.getDocumentsPath().split(","))
                : List.of();
        this.hasGeneratedDocuments = false; // Sera mis à jour par le service
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
}