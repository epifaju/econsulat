package com.econsulat.dto;

import com.econsulat.model.Demande;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DemandeResponse {

    private Long id;

    // Informations personnelles
    private String civilite;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private String birthPlace;
    private String birthCountry;

    // Adresse
    private String streetName;
    private String streetNumber;
    private String boxNumber;
    private String postalCode;
    private String city;
    private String country;

    // Filiation - Père
    private String fatherFirstName;
    private String fatherLastName;
    private LocalDate fatherBirthDate;
    private String fatherBirthPlace;
    private String fatherBirthCountry;

    // Filiation - Mère
    private String motherFirstName;
    private String motherLastName;
    private LocalDate motherBirthDate;
    private String motherBirthPlace;
    private String motherBirthCountry;

    // Type de document
    private String documentType;
    private String documentTypeDisplay;

    // Documents
    private List<String> documentFiles;

    // Statut et métadonnées
    private String status;
    private String statusDisplay;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Informations utilisateur
    private String userEmail;
    private String userName;
}