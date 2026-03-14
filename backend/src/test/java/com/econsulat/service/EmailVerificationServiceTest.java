package com.econsulat.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("EmailVerificationService")
class EmailVerificationServiceTest {

    private EmailVerificationService emailVerificationService;

    @BeforeEach
    void setUp() {
        emailVerificationService = new EmailVerificationService();
    }

    @Nested
    @DisplayName("generateVerificationToken")
    class GenerateVerificationToken {

        @Test
        void retourne_token_non_vide() {
            String token = emailVerificationService.generateVerificationToken("user@test.com");
            assertThat(token).isNotBlank().hasSize(32);
        }

        @Test
        void tokens_differents_pour_deux_emails() {
            String token1 = emailVerificationService.generateVerificationToken("user1@test.com");
            String token2 = emailVerificationService.generateVerificationToken("user2@test.com");
            assertThat(token1).isNotEqualTo(token2);
        }
    }

    @Nested
    @DisplayName("getEmailFromToken")
    class GetEmailFromToken {

        @Test
        void retourne_email_apres_generation() {
            String token = emailVerificationService.generateVerificationToken("user@test.com");
            assertThat(emailVerificationService.getEmailFromToken(token)).isEqualTo("user@test.com");
        }

        @Test
        void retourne_null_pour_token_inconnu() {
            assertThat(emailVerificationService.getEmailFromToken("token-inconnu")).isNull();
        }

        @Test
        void retourne_null_apres_verification_consomme_le_token() {
            String token = emailVerificationService.generateVerificationToken("user@test.com");
            emailVerificationService.verifyToken(token);
            assertThat(emailVerificationService.getEmailFromToken(token)).isNull();
        }
    }

    @Nested
    @DisplayName("verifyToken")
    class VerifyToken {

        @Test
        void retourne_true_pour_token_valide() {
            String token = emailVerificationService.generateVerificationToken("user@test.com");
            assertThat(emailVerificationService.verifyToken(token)).isTrue();
        }

        @Test
        void retourne_false_pour_token_inconnu() {
            assertThat(emailVerificationService.verifyToken("token-invalide")).isFalse();
        }

        @Test
        void retourne_false_au_deuxieme_appel_meme_token() {
            String token = emailVerificationService.generateVerificationToken("user@test.com");
            assertThat(emailVerificationService.verifyToken(token)).isTrue();
            assertThat(emailVerificationService.verifyToken(token)).isFalse();
        }
    }
}
