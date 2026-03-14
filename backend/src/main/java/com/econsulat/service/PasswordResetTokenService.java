package com.econsulat.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class PasswordResetTokenService {

    private final Map<String, ResetToken> tokens = new ConcurrentHashMap<>();
    private final SecureRandom random = new SecureRandom();

    @Value("${app.password-reset.token-expiry-hours:1}")
    private int expiryHours;

    public String createToken(String email) {
        String token = generateRandomToken();
        tokens.put(token, new ResetToken(email.trim().toLowerCase(), LocalDateTime.now()));
        log.info("Token de réinitialisation créé pour : {}", email);
        return token;
    }

    /**
     * Retourne l'email associé au token et invalide le token (usage unique).
     * Retourne null si le token est invalide ou expiré.
     */
    public String getEmailAndConsume(String token) {
        if (token == null || token.isBlank()) {
            return null;
        }
        ResetToken resetToken = tokens.remove(token);
        if (resetToken == null) {
            log.warn("Token de réinitialisation invalide ou déjà utilisé");
            return null;
        }
        if (LocalDateTime.now().isAfter(resetToken.createdAt.plusHours(expiryHours))) {
            log.warn("Token de réinitialisation expiré pour : {}", resetToken.email);
            return null;
        }
        log.info("Token de réinitialisation consommé pour : {}", resetToken.email);
        return resetToken.email;
    }

    private String generateRandomToken() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder token = new StringBuilder();
        for (int i = 0; i < 32; i++) {
            token.append(chars.charAt(random.nextInt(chars.length())));
        }
        return token.toString();
    }

    private static class ResetToken {
        private final String email;
        private final LocalDateTime createdAt;

        ResetToken(String email, LocalDateTime createdAt) {
            this.email = email;
            this.createdAt = createdAt;
        }
    }
}
