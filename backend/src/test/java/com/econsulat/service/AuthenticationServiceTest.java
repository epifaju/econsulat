package com.econsulat.service;

import com.econsulat.dto.AuthRequest;
import com.econsulat.dto.AuthResponse;
import com.econsulat.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthenticationService")
class AuthenticationServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthenticationService authenticationService;

    private User user;
    private AuthRequest authRequest;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setFirstName("Jean");
        user.setLastName("Dupont");
        user.setEmail("jean@test.com");
        user.setPassword("encoded");
        user.setRole(User.Role.USER);
        user.setEmailVerified(true);

        authRequest = new AuthRequest();
        authRequest.setEmail("jean@test.com");
        authRequest.setPassword("password123");
    }

    @Nested
    @DisplayName("login")
    class Login {

        @Test
        void retourne_AuthResponse_avec_token_quand_credentials_valides() {
            when(userService.findByEmail("jean@test.com")).thenReturn(Optional.of(user));
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenReturn(mock(Authentication.class));
            when(userService.loadUserByUsername("jean@test.com")).thenReturn(user);
            when(jwtService.generateToken(user)).thenReturn("jwt.token.here");

            AuthResponse result = authenticationService.login(authRequest);

            assertThat(result.getToken()).isEqualTo("jwt.token.here");
            assertThat(result.getEmail()).isEqualTo("jean@test.com");
            assertThat(result.getFirstName()).isEqualTo("Jean");
            assertThat(result.getLastName()).isEqualTo("Dupont");
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getRole()).isEqualTo(User.Role.USER);
            verify(jwtService).generateToken(user);
        }

        @Test
        void throw_quand_utilisateur_non_trouve() {
            when(userService.findByEmail("absent@test.com")).thenReturn(Optional.empty());
            authRequest.setEmail("absent@test.com");

            assertThatThrownBy(() -> authenticationService.login(authRequest))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Utilisateur non trouvé");
            verify(authenticationManager, never()).authenticate(any());
            verify(jwtService, never()).generateToken(any());
        }

        @Test
        void throw_quand_email_non_verifie() {
            user.setEmailVerified(false);
            when(userService.findByEmail("jean@test.com")).thenReturn(Optional.of(user));

            assertThatThrownBy(() -> authenticationService.login(authRequest))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Veuillez vérifier votre adresse email");
            verify(jwtService, never()).generateToken(any());
        }
    }

    @Nested
    @DisplayName("register")
    class Register {

        @Test
        void throw_UnsupportedOperationException() {
            assertThatThrownBy(() -> authenticationService.register(authRequest))
                    .isInstanceOf(UnsupportedOperationException.class)
                    .hasMessageContaining("inscription directe n'est pas autorisée");
        }
    }
}
