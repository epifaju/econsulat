package com.econsulat.dto;

import com.econsulat.model.User;
import java.time.LocalDateTime;

public class UserAdminResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String role;
    private String roleDisplay;
    private Boolean emailVerified;
    private LocalDateTime createdAt;
    private int demandesCount;

    public UserAdminResponse() {
    }

    public UserAdminResponse(User user) {
        this.id = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.role = user.getRole().name();
        this.roleDisplay = getRoleDisplayName(user.getRole());
        this.emailVerified = user.getEmailVerified();
        this.createdAt = user.getCreatedAt();
        this.demandesCount = 0; // Sera mis à jour par le service
    }

    private String getRoleDisplayName(User.Role role) {
        switch (role) {
            case ADMIN:
                return "Administrateur";
            case AGENT:
                return "Agent";
            case USER:
                return "Utilisateur";
            case CITIZEN:
                return "Citoyen";
            default:
                return role.name();
        }
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getRoleDisplay() {
        return roleDisplay;
    }

    public void setRoleDisplay(String roleDisplay) {
        this.roleDisplay = roleDisplay;
    }

    public Boolean getEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(Boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public int getDemandesCount() {
        return demandesCount;
    }

    public void setDemandesCount(int demandesCount) {
        this.demandesCount = demandesCount;
    }
}