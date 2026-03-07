package com.econsulat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Requête de mise à jour du profil (prénom, nom, email, changement de mot de passe).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileUpdateRequest {

    private String firstName;
    private String lastName;
    private String email;

    /** Mot de passe actuel (requis si newPassword est fourni). */
    private String currentPassword;
    /** Nouveau mot de passe (optionnel). */
    private String newPassword;
}
