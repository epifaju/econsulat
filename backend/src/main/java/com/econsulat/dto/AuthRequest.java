package com.econsulat.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthRequest {

    @Email(message = "L'adresse email doit être valide")
    @NotBlank(message = "L'adresse email est requise")
    private String email;

    @NotBlank(message = "Le mot de passe est requis")
    private String password;
}