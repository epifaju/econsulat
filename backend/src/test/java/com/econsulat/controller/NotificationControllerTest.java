package com.econsulat.controller;

import com.econsulat.model.Notification;
import com.econsulat.model.User;
import com.econsulat.repository.NotificationRepository;
import com.econsulat.repository.UserRepository;
import com.econsulat.service.EmailNotificationService;
import com.econsulat.service.JwtService;
import com.econsulat.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = NotificationController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("NotificationController")
class NotificationControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private NotificationRepository notificationRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private EmailNotificationService emailNotificationService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserService userService;

    @Nested
    @DisplayName("GET /api/notifications/my")
    class GetMyNotifications {

        @Test
        @WithMockUser(username = "user@test.com")
        void retourne_200_et_liste() throws Exception {
            User user = new User();
            user.setId(1L);
            when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
            when(notificationRepository.findByUtilisateurWithRelationsOrderByDateEnvoiDesc(user))
                    .thenReturn(List.of());

            mvc.perform(get("/api/notifications/my"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(0));
        }
    }

    @Nested
    @DisplayName("GET /api/notifications/demande/{demandeId}")
    class GetNotificationsByDemande {

        @Test
        @WithMockUser(roles = "ADMIN")
        void retourne_200_et_liste() throws Exception {
            when(notificationRepository.findByDemandeOrderByDateEnvoiDesc(1L)).thenReturn(List.of());

            mvc.perform(get("/api/notifications/demande/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(0));
        }
    }

    @Nested
    @DisplayName("GET /api/notifications/all")
    class GetAllNotifications {

        @Test
        @WithMockUser(roles = "ADMIN")
        void retourne_200_et_liste() throws Exception {
            when(notificationRepository.findAll()).thenReturn(List.of());

            mvc.perform(get("/api/notifications/all"))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("POST /api/notifications/{id}/resend")
    class ResendNotification {

        @Test
        @WithMockUser(roles = "ADMIN")
        void retourne_200_quand_succes() throws Exception {
            mvc.perform(post("/api/notifications/1/resend").with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Notification renvoyée avec succès"));
            verify(emailNotificationService).resendNotification(1L);
        }
    }

    @Nested
    @DisplayName("GET /api/notifications/count")
    class GetNotificationCount {

        @Test
        @WithMockUser(username = "user@test.com")
        void retourne_200_et_count() throws Exception {
            User user = new User();
            user.setId(1L);
            when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
            when(notificationRepository.countByUtilisateur(user)).thenReturn(5L);

            mvc.perform(get("/api/notifications/count"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.count").value(5));
        }
    }
}
