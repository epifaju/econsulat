package com.econsulat.dto;

import com.econsulat.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Réponse du profil utilisateur (sans mot de passe).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileResponse {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private User.Role role;
    private Boolean emailVerified;
    private LocalDateTime createdAt;

    public static ProfileResponse from(User user) {
        if (user == null) return null;
        ProfileResponse r = new ProfileResponse();
        r.setId(user.getId());
        r.setFirstName(user.getFirstName());
        r.setLastName(user.getLastName());
        r.setEmail(user.getEmail());
        r.setRole(user.getRole());
        r.setEmailVerified(user.getEmailVerified());
        r.setCreatedAt(user.getCreatedAt());
        return r;
    }
}
