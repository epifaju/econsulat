package com.econsulat.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "demandes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Demande {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Informations personnelles (Étape 1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "civilite_id", nullable = false)
    private Civilite civilite;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(name = "birth_place", nullable = false)
    private String birthPlace;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "birth_country_id", nullable = false)
    private Pays birthCountry;

    // Adresse (Étape 2)
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "adresse_id", nullable = false)
    private Adresse adresse;

    // Filiation (Étape 3) - Père
    @Column(name = "father_first_name", nullable = false)
    private String fatherFirstName;

    @Column(name = "father_last_name", nullable = false)
    private String fatherLastName;

    @Column(name = "father_birth_date", nullable = false)
    private LocalDate fatherBirthDate;

    @Column(name = "father_birth_place", nullable = false)
    private String fatherBirthPlace;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "father_birth_country_id", nullable = false)
    private Pays fatherBirthCountry;

    // Filiation (Étape 3) - Mère
    @Column(name = "mother_first_name", nullable = false)
    private String motherFirstName;

    @Column(name = "mother_last_name", nullable = false)
    private String motherLastName;

    @Column(name = "mother_birth_date", nullable = false)
    private LocalDate motherBirthDate;

    @Column(name = "mother_birth_place", nullable = false)
    private String motherBirthPlace;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mother_birth_country_id", nullable = false)
    private Pays motherBirthCountry;

    // Type de document (Étape 4)
    @Enumerated(EnumType.STRING)
    @Column(name = "document_type", nullable = false)
    private DocumentType documentType;

    // Fichiers uploadés (Étape 5)
    @Column(name = "documents_path")
    private String documentsPath;

    // Statut et métadonnées
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status = Status.PENDING;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relation avec l'utilisateur
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    public enum DocumentType {
        PASSEPORT("Passeport"),
        ACTE_NAISSANCE("Acte de naissance"),
        CERTIFICAT_MARIAGE("Certificat de mariage"),
        CARTE_IDENTITE("Carte d'identité"),
        AUTRE("Autre");

        private final String displayName;

        DocumentType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum Status {
        PENDING("En attente"),
        APPROVED("Approuvé"),
        REJECTED("Rejeté"),
        COMPLETED("Terminé");

        private final String displayName;

        Status(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}