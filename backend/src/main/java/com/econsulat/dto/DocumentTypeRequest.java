package com.econsulat.dto;

public class DocumentTypeRequest {
    private String libelle;
    private String description;
    private String templatePath;
    private Boolean isActive;

    public DocumentTypeRequest() {
    }

    public DocumentTypeRequest(String libelle, String description, String templatePath) {
        this.libelle = libelle;
        this.description = description;
        this.templatePath = templatePath;
        this.isActive = true;
    }

    // Getters et Setters
    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTemplatePath() {
        return templatePath;
    }

    public void setTemplatePath(String templatePath) {
        this.templatePath = templatePath;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}