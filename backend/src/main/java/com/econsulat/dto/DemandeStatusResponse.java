package com.econsulat.dto;

import java.time.LocalDateTime;

public class DemandeStatusResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String documentType;
    private String status;
    private LocalDateTime updatedAt;
    private String message;

    public DemandeStatusResponse() {
    }

    public DemandeStatusResponse(Long id, String firstName, String lastName, String documentType, String status,
            LocalDateTime updatedAt, String message) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.documentType = documentType;
        this.status = status;
        this.updatedAt = updatedAt;
        this.message = message;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}