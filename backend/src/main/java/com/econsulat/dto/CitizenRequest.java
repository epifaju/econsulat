package com.econsulat.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class CitizenRequest {
    
    @NotBlank(message = "Le prénom est requis")
    private String firstName;
    
    @NotBlank(message = "Le nom de famille est requis")
    private String lastName;
    
    @NotNull(message = "La date de naissance est requise")
    private LocalDate birthDate;
    
    @NotBlank(message = "Le lieu de naissance est requis")
    private String birthPlace;
    
    @NotBlank(message = "Le type de document est requis")
    private String documentType;
} 