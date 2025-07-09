package com.econsulat.dto;

import com.econsulat.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserRequest {

    @NotBlank(message = "Le prénom est requis")
    private String firstName;

    @NotBlank(message = "Le nom est requis")
    private String lastName;

    @Email(message = "L'adresse email doit être valide")
    @NotBlank(message = "L'adresse email est requise")
    private String email;

    private String password; // Optionnel pour la mise à jour

    @NotNull(message = "Le rôle est requis")
    private User.Role role;

    private Boolean emailVerified = false;
}