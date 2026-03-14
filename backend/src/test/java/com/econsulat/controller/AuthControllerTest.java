package com.econsulat.controller;

import com.econsulat.dto.AuthRequest;
import com.econsulat.dto.AuthResponse;
import com.econsulat.dto.UserRequest;
import com.econsulat.model.User;
import com.econsulat.service.AuthenticationService;
import com.econsulat.service.JwtService;
import com.econsulat.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("AuthController")
class AuthControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthenticationService authenticationService;

    @MockBean
    private UserService userService;

    @MockBean
    private MessageSource messageSource;

    @MockBean
    private JwtService jwtService;

    @Nested
    @DisplayName("POST /api/auth/login")
    class Login {

        @Test
        void retourne_200_et_token_quand_credentials_valides() throws Exception {
            AuthRequest request = new AuthRequest();
            request.setEmail("user@test.com");
            request.setPassword("password123");
            AuthResponse response = new AuthResponse("jwt.token.here", 1L, "Jean", "Dupont", "user@test.com", User.Role.USER, "OK");

            when(authenticationService.login(any(AuthRequest.class))).thenReturn(response);

            mvc.perform(post("/api/auth/login")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token").value("jwt.token.here"))
                    .andExpect(jsonPath("$.email").value("user@test.com"));
        }

        @Test
        void retourne_erreur_quand_login_echoue() throws Exception {
            AuthRequest request = new AuthRequest();
            request.setEmail("user@test.com");
            request.setPassword("wrong");
            when(authenticationService.login(any(AuthRequest.class)))
                    .thenThrow(new RuntimeException("Identifiants invalides"));

            mvc.perform(post("/api/auth/login")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().is4xxClientError());
        }
    }

    @Nested
    @DisplayName("POST /api/auth/register")
    class Register {

        @Test
        void retourne_400_quand_email_deja_utilise() throws Exception {
            when(messageSource.getMessage(eq("auth.emailAlreadyExists"), isNull(), any())).thenReturn("Email déjà utilisé");
            when(userService.findByEmail("existing@test.com")).thenReturn(Optional.of(new User()));

            String body = """
                    {"firstName":"Jean","lastName":"Dupont","email":"existing@test.com","password":"Pass123"}
                    """;

            mvc.perform(post("/api/auth/register")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").exists());
        }

        @Test
        void retourne_201_quand_inscription_reussie() throws Exception {
            when(userService.findByEmail("new@test.com")).thenReturn(Optional.empty());
            when(messageSource.getMessage(eq("auth.registerSuccess"), isNull(), any())).thenReturn("Inscription réussie");
            User created = new User();
            created.setId(2L);
            created.setEmail("new@test.com");
            created.setFirstName("Marie");
            created.setLastName("Martin");
            created.setRole(User.Role.USER);
            when(userService.createUser(any(UserRequest.class))).thenReturn(created);

            String body = """
                    {"firstName":"Marie","lastName":"Martin","email":"new@test.com","password":"Pass123"}
                    """;

            mvc.perform(post("/api/auth/register")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.userId").value(2))
                    .andExpect(jsonPath("$.email").value("new@test.com"));
        }
    }

    @Nested
    @DisplayName("GET /api/auth/confirm")
    class ConfirmEmail {

        @Test
        void retourne_200_quand_token_valide() throws Exception {
            when(messageSource.getMessage(eq("auth.confirmEmailSuccess"), isNull(), any())).thenReturn("Email confirmé");
            when(userService.verifyEmail("valid-token")).thenReturn(true);

            mvc.perform(get("/api/auth/confirm").param("token", "valid-token"))
                    .andExpect(status().isOk())
                    .andExpect(content().string(org.hamcrest.Matchers.containsString("confirmé")));
        }

        @Test
        void retourne_400_quand_token_invalide() throws Exception {
            when(messageSource.getMessage(eq("auth.confirmEmailInvalidToken"), isNull(), any())).thenReturn("Token invalide");
            when(userService.verifyEmail("invalid-token")).thenReturn(false);

            mvc.perform(get("/api/auth/confirm").param("token", "invalid-token"))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("GET /api/auth/me")
    class GetCurrentUser {

        @Test
        void retourne_401_quand_non_authentifie() throws Exception {
            when(messageSource.getMessage(eq("auth.unauthorized"), isNull(), any())).thenReturn("Non autorisé");

            mvc.perform(get("/api/auth/me"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @WithMockUser(username = "user@test.com")
        void retourne_200_et_infos_utilisateur_quand_authentifie() throws Exception {
            when(messageSource.getMessage(eq("auth.userNotFound"), isNull(), any())).thenReturn("Utilisateur non trouvé");
            User user = new User();
            user.setId(1L);
            user.setEmail("user@test.com");
            user.setFirstName("Jean");
            user.setLastName("Dupont");
            user.setRole(User.Role.USER);
            user.setEmailVerified(true);
            when(userService.findByEmail("user@test.com")).thenReturn(Optional.of(user));

            mvc.perform(get("/api/auth/me"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.email").value("user@test.com"))
                    .andExpect(jsonPath("$.firstName").value("Jean"));
        }
    }
}
