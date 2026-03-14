package com.econsulat.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("JwtService")
class JwtServiceTest {

    /** Clé d'au moins 256 bits pour HS256. */
    private static final String TEST_SECRET = "test-jwt-secret-key-min-32-chars-for-hs256-algorithm";
    private static final long EXPIRATION_MS = 86400000L;

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKey", TEST_SECRET);
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", EXPIRATION_MS);
    }

    private UserDetails userDetails(String username) {
        UserDetails ud = mock(UserDetails.class);
        when(ud.getUsername()).thenReturn(username);
        return ud;
    }

    @Nested
    @DisplayName("extractUsername")
    class ExtractUsername {

        @Test
        void retourne_le_subject_du_token() {
            UserDetails user = userDetails("user@econsulat.com");
            String token = jwtService.generateToken(user);
            assertThat(jwtService.extractUsername(token)).isEqualTo("user@econsulat.com");
        }
    }

    @Nested
    @DisplayName("generateToken")
    class GenerateToken {

        @Test
        void generateToken_sans_extra_claims_produit_un_token_valide() {
            UserDetails user = userDetails("admin@test.com");
            String token = jwtService.generateToken(user);
            assertThat(token).isNotBlank();
            assertThat(token).contains(".");
            assertThat(jwtService.extractUsername(token)).isEqualTo("admin@test.com");
        }

        @Test
        void generateToken_avec_extra_claims_inclut_le_subject() {
            UserDetails user = userDetails("citizen@test.com");
            String token = jwtService.generateToken(Map.of("role", "CITIZEN"), user);
            assertThat(jwtService.extractUsername(token)).isEqualTo("citizen@test.com");
        }
    }

    @Nested
    @DisplayName("isTokenValid")
    class IsTokenValid {

        @Test
        void retourne_true_quand_meme_utilisateur_et_token_non_expire() {
            UserDetails user = userDetails("user@econsulat.com");
            String token = jwtService.generateToken(user);
            assertThat(jwtService.isTokenValid(token, user)).isTrue();
        }

        @Test
        void retourne_false_quand_utilisateur_different() {
            UserDetails user = userDetails("user@econsulat.com");
            String token = jwtService.generateToken(user);
            UserDetails other = userDetails("other@econsulat.com");
            assertThat(jwtService.isTokenValid(token, other)).isFalse();
        }

        @Test
        void lance_ExpiredJwtException_quand_token_expire() throws InterruptedException {
            JwtService shortExpiry = new JwtService();
            ReflectionTestUtils.setField(shortExpiry, "secretKey", TEST_SECRET);
            ReflectionTestUtils.setField(shortExpiry, "jwtExpiration", 1L); // 1 ms
            UserDetails user = userDetails("user@econsulat.com");
            String token = shortExpiry.generateToken(user);
            Thread.sleep(15);
            assertThatThrownBy(() -> jwtService.isTokenValid(token, user))
                    .isInstanceOf(io.jsonwebtoken.ExpiredJwtException.class);
        }
    }
}
