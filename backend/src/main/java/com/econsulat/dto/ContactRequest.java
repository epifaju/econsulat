package com.econsulat.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ContactRequest {

    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 200)
    private String name;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Format d'email invalide")
    @Size(max = 255)
    private String email;

    @Size(max = 500)
    private String subject;

    @NotBlank(message = "Le message est obligatoire")
    @Size(max = 5000)
    private String message;
}
