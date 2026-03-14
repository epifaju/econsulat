package com.econsulat.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResetPasswordRequest {

    @NotBlank(message = "validation.token.required")
    private String token;

    @NotBlank(message = "validation.password.required")
    @Size(min = 6, message = "validation.password.minLength")
    private String newPassword;

    @NotBlank(message = "validation.password.required")
    private String confirmPassword;
}
