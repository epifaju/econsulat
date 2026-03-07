package com.econsulat.service;

import com.econsulat.dto.AuthRequest;
import com.econsulat.dto.AuthResponse;
import com.econsulat.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse login(AuthRequest request) {
        log.info("🔐 Début de l'authentification pour l'email: {}", request.getEmail());

        try {
            // Étape 1: Recherche de l'utilisateur
            log.info("📋 Étape 1: Recherche de l'utilisateur dans la base...");
            User user = userService.findByEmail(request.getEmail())
                    .orElseThrow(() -> {
                        log.error("❌ Utilisateur non trouvé avec l'email: {}", request.getEmail());
                        return new RuntimeException("Utilisateur non trouvé avec l'email : " + request.getEmail());
                    });

            log.info("✅ Utilisateur trouvé: ID={}, Email={}, Role={}, Enabled={}",
                    user.getId(), user.getEmail(), user.getRole(), user.getEmailVerified());

            // Étape 2: Vérification de l'email
            log.info("📧 Étape 2: Vérification de l'email...");
            if (!user.getEmailVerified()) {
                log.error("❌ Email non vérifié pour l'utilisateur: {}", user.getEmail());
                throw new RuntimeException("Veuillez vérifier votre adresse email avant de vous connecter");
            }
            log.info("✅ Email vérifié");

            // Étape 3: Authentification Spring Security
            log.info("🔑 Étape 3: Authentification Spring Security...");
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
            log.info("✅ Authentification Spring Security réussie");

            // Étape 4: Chargement des détails utilisateur
            log.info("👤 Étape 4: Chargement des détails utilisateur...");
            UserDetails userDetails = userService.loadUserByUsername(request.getEmail());
            log.info("✅ Détails utilisateur chargés: {}", userDetails.getUsername());

            // Étape 5: Génération du token JWT
            log.info("🎫 Étape 5: Génération du token JWT...");
            String token = jwtService.generateToken(userDetails);
            log.info("✅ Token JWT généré: {}...", token.substring(0, Math.min(50, token.length())));

            log.info("🎉 Authentification complète réussie pour: {}", user.getEmail());
            return new AuthResponse(
                    token,
                    user.getId(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getEmail(),
                    user.getRole(),
                    "Connexion réussie");

        } catch (Exception e) {
            log.error("💥 Erreur lors de l'authentification pour {}: {}", request.getEmail(), e.getMessage(), e);
            throw e;
        }
    }

    public AuthResponse register(AuthRequest request) {
        // Cette méthode n'est plus utilisée car la création d'utilisateur se fait via
        // l'admin
        throw new UnsupportedOperationException(
                "L'inscription directe n'est pas autorisée. Contactez un administrateur.");
    }
}