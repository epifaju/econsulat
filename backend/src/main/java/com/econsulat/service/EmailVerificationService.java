package com.econsulat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailVerificationService {

    private final Map<String, VerificationToken> tokens = new ConcurrentHashMap<>();
    private final SecureRandom random = new SecureRandom();

    public String generateVerificationToken(String email) {
        String token = generateRandomToken();
        tokens.put(token, new VerificationToken(email, LocalDateTime.now()));
        log.info("Token de validation généré pour : {}", email);
        return token;
    }

    public boolean verifyToken(String token) {
        VerificationToken verificationToken = tokens.get(token);
        if (verificationToken == null) {
            log.warn("Token invalide : {}", token);
            return false;
        }

        // Vérifier si le token n'a pas expiré (24 heures)
        if (LocalDateTime.now().isAfter(verificationToken.createdAt.plusHours(24))) {
            tokens.remove(token);
            log.warn("Token expiré : {}", token);
            return false;
        }

        tokens.remove(token);
        log.info("Token validé avec succès pour : {}", verificationToken.email);
        return true;
    }

    public String getEmailFromToken(String token) {
        VerificationToken verificationToken = tokens.get(token);
        return verificationToken != null ? verificationToken.email : null;
    }

    private String generateRandomToken() {
        StringBuilder token = new StringBuilder();
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        for (int i = 0; i < 32; i++) {
            token.append(chars.charAt(random.nextInt(chars.length())));
        }
        return token.toString();
    }

    private static class VerificationToken {
        private final String email;
        private final LocalDateTime createdAt;

        public VerificationToken(String email, LocalDateTime createdAt) {
            this.email = email;
            this.createdAt = createdAt;
        }
    }
}