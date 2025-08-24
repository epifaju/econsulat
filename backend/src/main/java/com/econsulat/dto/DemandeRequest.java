package com.econsulat.dto;

import com.econsulat.model.Demande;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DemandeRequest {

    // Étape 1: Informations personnelles
    private Long civiliteId;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private String birthPlace;
    private Long birthCountryId;

    // Étape 2: Adresse
    private String streetName;
    private String streetNumber;
    private String boxNumber;
    private String postalCode;
    private String city;
    private Long countryId;

    // Étape 3: Filiation - Père
    private String fatherFirstName;
    private String fatherLastName;
    private LocalDate fatherBirthDate;
    private String fatherBirthPlace;
    private Long fatherBirthCountryId;

    // Étape 3: Filiation - Mère
    private String motherFirstName;
    private String motherLastName;
    private LocalDate motherBirthDate;
    private String motherBirthPlace;
    private Long motherBirthCountryId;

    // Étape 4: Type de document - CORRIGÉ : Utiliser l'ID au lieu de l'enum
    private Long documentTypeId; // ID du type de document depuis la table document_types

    // Étape 5: Documents (noms des fichiers uploadés)
    private List<String> documentFiles;
}