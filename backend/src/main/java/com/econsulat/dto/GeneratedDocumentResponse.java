package com.econsulat.dto;

import java.time.LocalDateTime;

public class GeneratedDocumentResponse {
    private Long id;
    private String fileName;
    private String filePath;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private LocalDateTime downloadedAt;
    private Long demandeId;
    private Long documentTypeId;
    private String createdByEmail;

    public GeneratedDocumentResponse() {
    }

    public GeneratedDocumentResponse(Long id, String fileName, String filePath, String status,
            LocalDateTime createdAt, LocalDateTime expiresAt, LocalDateTime downloadedAt,
            Long demandeId, Long documentTypeId, String createdByEmail) {
        this.id = id;
        this.fileName = fileName;
        this.filePath = filePath;
        this.status = status;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.downloadedAt = downloadedAt;
        this.demandeId = demandeId;
        this.documentTypeId = documentTypeId;
        this.createdByEmail = createdByEmail;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public LocalDateTime getDownloadedAt() {
        return downloadedAt;
    }

    public void setDownloadedAt(LocalDateTime downloadedAt) {
        this.downloadedAt = downloadedAt;
    }

    public Long getDemandeId() {
        return demandeId;
    }

    public void setDemandeId(Long demandeId) {
        this.demandeId = demandeId;
    }

    public Long getDocumentTypeId() {
        return documentTypeId;
    }

    public void setDocumentTypeId(Long documentTypeId) {
        this.documentTypeId = documentTypeId;
    }

    public String getCreatedByEmail() {
        return createdByEmail;
    }

    public void setCreatedByEmail(String createdByEmail) {
        this.createdByEmail = createdByEmail;
    }
}