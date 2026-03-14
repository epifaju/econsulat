package com.econsulat.controller;

import com.econsulat.dto.ProfileResponse;
import com.econsulat.dto.ProfileUpdateRequest;
import com.econsulat.model.User;
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
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("UserController")
class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtService jwtService;

    private static User user(long id, String email) {
        User u = new User();
        u.setId(id);
        u.setEmail(email);
        u.setFirstName("Jean");
        u.setLastName("Dupont");
        u.setRole(User.Role.USER);
        return u;
    }

    private static ProfileResponse profile(long id, String email) {
        ProfileResponse p = new ProfileResponse();
        p.setId(id);
        p.setEmail(email);
        p.setFirstName("Jean");
        p.setLastName("Dupont");
        p.setRole(User.Role.USER);
        return p;
    }

    @Nested
    @DisplayName("GET /api/users/me")
    class GetMyProfile {

        @Test
        @WithMockUser(username = "user@test.com")
        void retourne_200_et_profil() throws Exception {
            when(userService.getProfileByEmail("user@test.com")).thenReturn(profile(1L, "user@test.com"));

            mvc.perform(get("/api/users/me"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.email").value("user@test.com"));
            verify(userService).getProfileByEmail("user@test.com");
        }
    }

    @Nested
    @DisplayName("PUT /api/users/me")
    class UpdateMyProfile {

        @Test
        @WithMockUser(username = "user@test.com")
        void retourne_200_et_profil_mis_a_jour() throws Exception {
            ProfileUpdateRequest request = new ProfileUpdateRequest();
            request.setFirstName("Marie");
            when(userService.updateProfileByEmail(eq("user@test.com"), any(ProfileUpdateRequest.class)))
                    .thenReturn(profile(1L, "user@test.com"));

            mvc.perform(put("/api/users/me")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk());
            verify(userService).updateProfileByEmail(eq("user@test.com"), any(ProfileUpdateRequest.class));
        }
    }

    @Nested
    @DisplayName("GET /api/users")
    class GetAllUsers {

        @Test
        @WithMockUser(roles = "ADMIN")
        void retourne_200_et_liste_utilisateurs() throws Exception {
            when(userService.getAllUsers()).thenReturn(List.of(user(1L, "admin@test.com"), user(2L, "user@test.com")));

            mvc.perform(get("/api/users"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2));
        }
    }

    @Nested
    @DisplayName("GET /api/users/{id}")
    class GetUserById {

        @Test
        @WithMockUser(roles = "ADMIN")
        void retourne_200_quand_trouve() throws Exception {
            when(userService.getUserById(1L)).thenReturn(Optional.of(user(1L, "user@test.com")));

            mvc.perform(get("/api/users/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.email").value("user@test.com"));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void retourne_404_quand_absent() throws Exception {
            when(userService.getUserById(99L)).thenReturn(Optional.empty());

            mvc.perform(get("/api/users/99"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("DELETE /api/users/{id}")
    class DeleteUser {

        @Test
        @WithMockUser(roles = "ADMIN")
        void retourne_200_et_appelle_service() throws Exception {
            mvc.perform(delete("/api/users/1").with(csrf()))
                    .andExpect(status().isOk());
            verify(userService).deleteUser(1L);
        }
    }
}
