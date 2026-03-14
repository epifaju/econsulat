package com.econsulat.service;

import com.econsulat.dto.ProfileResponse;
import com.econsulat.dto.ProfileUpdateRequest;
import com.econsulat.dto.UserRequest;
import com.econsulat.model.User;
import com.econsulat.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user;

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
    }

    @Nested
    @DisplayName("loadUserByUsername")
    class LoadUserByUsername {

        @Test
        void retourne_UserDetails_quand_utilisateur_trouve() {
            when(userRepository.findByEmail("jean@test.com")).thenReturn(Optional.of(user));

            UserDetails result = userService.loadUserByUsername("jean@test.com");

            assertThat(result).isSameAs(user);
            assertThat(result.getUsername()).isEqualTo("jean@test.com");
        }

        @Test
        void throw_UsernameNotFoundException_quand_non_trouve() {
            when(userRepository.findByEmail("absent@test.com")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.loadUserByUsername("absent@test.com"))
                    .isInstanceOf(UsernameNotFoundException.class)
                    .hasMessageContaining("absent@test.com");
        }
    }

    @Nested
    @DisplayName("getAllUsers / getUserById / findByEmail")
    class Queries {

        @Test
        void getAllUsers_retourne_liste_du_repository() {
            List<User> list = List.of(user);
            when(userRepository.findAll()).thenReturn(list);

            assertThat(userService.getAllUsers()).isSameAs(list);
        }

        @Test
        void getUserById_retourne_Optional_vide_ou_present() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(userRepository.findById(99L)).thenReturn(Optional.empty());

            assertThat(userService.getUserById(1L)).contains(user);
            assertThat(userService.getUserById(99L)).isEmpty();
        }

        @Test
        void findByEmail_retourne_Optional_du_repository() {
            when(userRepository.findByEmail("jean@test.com")).thenReturn(Optional.of(user));
            when(userRepository.findByEmail("autre@test.com")).thenReturn(Optional.empty());

            assertThat(userService.findByEmail("jean@test.com")).contains(user);
            assertThat(userService.findByEmail("autre@test.com")).isEmpty();
        }
    }

    @Nested
    @DisplayName("createUser")
    class CreateUser {

        @Test
        void encode_le_mot_de_passe_et_sauvegarde() {
            user.setPassword("plain");
            when(passwordEncoder.encode("plain")).thenReturn("encoded");
            when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

            User result = userService.createUser(user);

            verify(passwordEncoder).encode("plain");
            ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
            verify(userRepository).save(captor.capture());
            assertThat(captor.getValue().getPassword()).isEqualTo("encoded");
        }

        @Test
        void createUser_UserRequest_remplit_entite_et_appelle_createUser_User() {
            UserRequest req = new UserRequest("Marie", "Martin", "marie@test.com", "pass123", "ADMIN");
            req.setEmailVerified(true);

            when(passwordEncoder.encode("pass123")).thenReturn("enc");
            when(userRepository.save(any(User.class))).thenAnswer(inv -> {
                User u = inv.getArgument(0);
                u.setId(2L);
                return u;
            });

            User result = userService.createUser(req);

            assertThat(result.getFirstName()).isEqualTo("Marie");
            assertThat(result.getLastName()).isEqualTo("Martin");
            assertThat(result.getEmail()).isEqualTo("marie@test.com");
            assertThat(result.getRole()).isEqualTo(User.Role.ADMIN);
            assertThat(result.getEmailVerified()).isTrue();
            verify(userRepository).save(any(User.class));
        }
    }

    @Nested
    @DisplayName("updateUser")
    class UpdateUser {

        @Test
        void met_a_jour_et_sauvegarde_quand_id_existe() {
            User updated = new User();
            updated.setFirstName("Jean");
            updated.setLastName("Dupont");
            updated.setEmail("jean2@test.com");
            updated.setRole(User.Role.ADMIN);
            updated.setPassword(null);

            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

            User result = userService.updateUser(1L, updated);

            verify(userRepository).save(user);
            assertThat(user.getEmail()).isEqualTo("jean2@test.com");
            assertThat(user.getRole()).isEqualTo(User.Role.ADMIN);
            verify(passwordEncoder, never()).encode(any());
        }

        @Test
        void encode_nouveau_mot_de_passe_quand_non_vide() {
            User updated = new User();
            updated.setFirstName("Jean");
            updated.setLastName("Dupont");
            updated.setEmail("jean@test.com");
            updated.setRole(User.Role.USER);
            updated.setPassword("newPass");

            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(passwordEncoder.encode("newPass")).thenReturn("encNew");
            when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

            userService.updateUser(1L, updated);

            verify(passwordEncoder).encode("newPass");
            assertThat(user.getPassword()).isEqualTo("encNew");
        }

        @Test
        void throw_quand_id_inexistant() {
            when(userRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.updateUser(99L, user))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("not found");
        }
    }

    @Nested
    @DisplayName("deleteUser / getTotalUsers / verifyEmail")
    class Others {

        @Test
        void deleteUser_appelle_deleteById() {
            userService.deleteUser(1L);
            verify(userRepository).deleteById(1L);
        }

        @Test
        void getTotalUsers_retourne_count_du_repository() {
            when(userRepository.count()).thenReturn(42L);
            assertThat(userService.getTotalUsers()).isEqualTo(42L);
        }

        @Test
        void verifyEmail_retourne_true() {
            assertThat(userService.verifyEmail("any-token")).isTrue();
        }
    }

    @Nested
    @DisplayName("getProfileByEmail / updateProfileByEmail")
    class Profile {

        @Test
        void getProfileByEmail_retourne_ProfileResponse_sans_mot_de_passe() {
            when(userRepository.findByEmail("jean@test.com")).thenReturn(Optional.of(user));

            ProfileResponse result = userService.getProfileByEmail("jean@test.com");

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getEmail()).isEqualTo("jean@test.com");
            assertThat(result.getFirstName()).isEqualTo("Jean");
        }

        @Test
        void getProfileByEmail_throw_quand_utilisateur_absent() {
            when(userRepository.findByEmail("absent@test.com")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.getProfileByEmail("absent@test.com"))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("non trouvé");
        }

        @Test
        void updateProfileByEmail_met_a_jour_nom_et_email_et_sauvegarde() {
            when(userRepository.findByEmail("jean@test.com")).thenReturn(Optional.of(user));
            when(userRepository.existsByEmailAndIdNot(eq("new@test.com"), eq(1L))).thenReturn(false);
            when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

            ProfileUpdateRequest req = new ProfileUpdateRequest();
            req.setFirstName("Jean");
            req.setLastName("Dupuis");
            req.setEmail("new@test.com");

            ProfileResponse result = userService.updateProfileByEmail("jean@test.com", req);

            assertThat(user.getLastName()).isEqualTo("Dupuis");
            assertThat(user.getEmail()).isEqualTo("new@test.com");
            verify(userRepository).save(user);
        }

        @Test
        void updateProfileByEmail_throw_quand_nouvel_email_deja_utilise() {
            when(userRepository.findByEmail("jean@test.com")).thenReturn(Optional.of(user));
            when(userRepository.existsByEmailAndIdNot(eq("taken@test.com"), eq(1L))).thenReturn(true);

            ProfileUpdateRequest req = new ProfileUpdateRequest();
            req.setEmail("taken@test.com");

            assertThatThrownBy(() -> userService.updateProfileByEmail("jean@test.com", req))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("déjà cette adresse");
            verify(userRepository, never()).save(any());
        }

        @Test
        void updateProfileByEmail_throw_quand_nouveau_mot_de_passe_sans_actuel() {
            when(userRepository.findByEmail("jean@test.com")).thenReturn(Optional.of(user));

            ProfileUpdateRequest req = new ProfileUpdateRequest();
            req.setNewPassword("newPass");
            req.setCurrentPassword(null);

            assertThatThrownBy(() -> userService.updateProfileByEmail("jean@test.com", req))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("mot de passe actuel");
        }

        @Test
        void updateProfileByEmail_throw_quand_mot_de_passe_actuel_incorrect() {
            when(userRepository.findByEmail("jean@test.com")).thenReturn(Optional.of(user));
            when(passwordEncoder.matches(eq("wrong"), eq("encoded"))).thenReturn(false);

            ProfileUpdateRequest req = new ProfileUpdateRequest();
            req.setCurrentPassword("wrong");
            req.setNewPassword("newPass");

            assertThatThrownBy(() -> userService.updateProfileByEmail("jean@test.com", req))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("incorrect");
        }

        @Test
        void updateProfileByEmail_met_a_jour_preferredLocale_fr_ou_pt() {
            when(userRepository.findByEmail("jean@test.com")).thenReturn(Optional.of(user));
            when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

            ProfileUpdateRequest req = new ProfileUpdateRequest();
            req.setPreferredLocale("pt");

            ProfileResponse result = userService.updateProfileByEmail("jean@test.com", req);

            assertThat(user.getPreferredLocale()).isEqualTo("pt");
            verify(userRepository).save(user);
        }

        @Test
        void updateProfileByEmail_ignore_locale_invalide() {
            when(userRepository.findByEmail("jean@test.com")).thenReturn(Optional.of(user));
            when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

            ProfileUpdateRequest req = new ProfileUpdateRequest();
            req.setPreferredLocale("de");

            userService.updateProfileByEmail("jean@test.com", req);

            assertThat(user.getPreferredLocale()).isNull();
        }
    }

    @Nested
    @DisplayName("rôles et cas limites")
    class RolesAndEdgeCases {

        @Test
        void createUser_UserRequest_conserve_role_USER() {
            UserRequest req = new UserRequest("A", "B", "a@test.com", "pass", "USER");
            when(passwordEncoder.encode("pass")).thenReturn("enc");
            when(userRepository.save(any(User.class))).thenAnswer(inv -> {
                User u = inv.getArgument(0);
                u.setId(10L);
                return u;
            });

            User result = userService.createUser(req);

            assertThat(result.getRole()).isEqualTo(User.Role.USER);
        }

        @Test
        void getUserById_retourne_empty_quand_utilisateur_inexistant() {
            when(userRepository.findById(999L)).thenReturn(Optional.empty());

            assertThat(userService.getUserById(999L)).isEmpty();
        }
    }
}
