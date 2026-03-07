package com.econsulat.service;

import com.econsulat.dto.ProfileResponse;
import com.econsulat.dto.ProfileUpdateRequest;
import com.econsulat.model.User;
import com.econsulat.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info("🔍 loadUserByUsername appelé avec l'email: '{}'", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé: " + email));

        log.info("✅ Utilisateur chargé pour loadUserByUsername: ID={}, Email={}, Role={}",
                user.getId(), user.getEmail(), user.getRole());

        // Retourner directement notre utilisateur car il implémente déjà UserDetails
        // avec la méthode getAuthorities() qui retourne les bonnes autorités
        return user;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByEmail(String email) {
        log.info("🔍 findByEmail appelé avec l'email: '{}'", email);
        log.info("🔍 Longueur de l'email: {}", email.length());
        log.info("🔍 Caractères de l'email: {}",
                email.chars().mapToObj(c -> String.format("'%c'(%d)", (char) c, c)).toList());

        Optional<User> user = userRepository.findByEmail(email);

        if (user.isPresent()) {
            User foundUser = user.get();
            log.info("✅ Utilisateur trouvé dans findByEmail: ID={}, Email='{}', Role={}, Enabled={}",
                    foundUser.getId(), foundUser.getEmail(), foundUser.getRole(), foundUser.getEmailVerified());
        } else {
            log.warn("⚠️ Aucun utilisateur trouvé avec l'email: '{}'", email);

            // Debug: afficher tous les utilisateurs existants
            List<User> allUsers = userRepository.findAll();
            log.info("📋 Tous les utilisateurs en base ({}):", allUsers.size());
            allUsers.forEach(u -> log.info("   - ID={}, Email='{}', Role={}, Enabled={}",
                    u.getId(), u.getEmail(), u.getRole(), u.getEmailVerified()));
        }

        return user;
    }

    public User createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User createUser(com.econsulat.dto.UserRequest userRequest) {
        User user = new User();
        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());
        user.setEmail(userRequest.getEmail());
        user.setPassword(userRequest.getPassword());
        user.setRole(userRequest.getRoleEnum());
        user.setEmailVerified(userRequest.getEmailVerified() != null ? userRequest.getEmailVerified() : false);

        return createUser(user);
    }

    public User updateUser(Long id, User userDetails) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setFirstName(userDetails.getFirstName());
        user.setLastName(userDetails.getLastName());
        user.setEmail(userDetails.getEmail());
        user.setRole(userDetails.getRole());

        if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        }

        return userRepository.save(user);
    }

    public User updateUser(Long id, com.econsulat.dto.UserRequest userRequest) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());
        user.setEmail(userRequest.getEmail());
        user.setRole(userRequest.getRoleEnum());

        if (userRequest.getEmailVerified() != null) {
            user.setEmailVerified(userRequest.getEmailVerified());
        }

        if (userRequest.getPassword() != null && !userRequest.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        }

        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public long getTotalUsers() {
        return userRepository.count();
    }

    public boolean verifyEmail(String token) {
        // Pour l'instant, on simule une vérification d'email
        // TODO: Implémenter la vraie logique de vérification d'email
        return true;
    }

    /**
     * Retourne le profil de l'utilisateur (sans mot de passe).
     */
    public ProfileResponse getProfileByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        return ProfileResponse.from(user);
    }

    /**
     * Met à jour le profil de l'utilisateur connecté (prénom, nom, email, mot de passe).
     */
    public ProfileResponse updateProfileByEmail(String email, ProfileUpdateRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        if (request.getFirstName() != null && !request.getFirstName().isBlank()) {
            user.setFirstName(request.getFirstName().trim());
        }
        if (request.getLastName() != null && !request.getLastName().isBlank()) {
            user.setLastName(request.getLastName().trim());
        }
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            String newEmail = request.getEmail().trim();
            if (!newEmail.equalsIgnoreCase(user.getEmail()) && userRepository.existsByEmailAndIdNot(newEmail, user.getId())) {
                throw new RuntimeException("Un autre compte utilise déjà cette adresse email");
            }
            user.setEmail(newEmail);
        }

        if (request.getNewPassword() != null && !request.getNewPassword().isBlank()) {
            if (request.getCurrentPassword() == null || request.getCurrentPassword().isBlank()) {
                throw new RuntimeException("Le mot de passe actuel est requis pour en définir un nouveau");
            }
            if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
                throw new RuntimeException("Mot de passe actuel incorrect");
            }
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        }

        user = userRepository.save(user);
        return ProfileResponse.from(user);
    }
}