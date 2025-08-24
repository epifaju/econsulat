package com.econsulat.controller;

import com.econsulat.dto.AuthRequest;
import com.econsulat.dto.AuthResponse;
import com.econsulat.dto.UserRequest;
import com.econsulat.service.AuthenticationService;
import com.econsulat.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.econsulat.model.User;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
@Slf4j
public class AuthController {

    private final AuthenticationService authenticationService;
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        return ResponseEntity.ok(authenticationService.login(request));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserRequest request) {
        log.info("🔐 Tentative d'inscription pour l'email: {}", request.getEmail());
        log.info("📝 Données reçues: firstName={}, lastName={}, email={}, role={}",
                request.getFirstName(), request.getLastName(), request.getEmail(), request.getRole());

        try {
            // Validation des données reçues
            if (request.getFirstName() == null || request.getFirstName().trim().isEmpty()) {
                log.error("❌ Erreur de validation: firstName est vide ou null");
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Le prénom est obligatoire"));
            }

            if (request.getLastName() == null || request.getLastName().trim().isEmpty()) {
                log.error("❌ Erreur de validation: lastName est vide ou null");
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Le nom est obligatoire"));
            }

            if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
                log.error("❌ Erreur de validation: email est vide ou null");
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "L'email est obligatoire"));
            }

            if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
                log.error("❌ Erreur de validation: password est vide ou null");
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Le mot de passe est obligatoire"));
            }

            if (request.getPassword().length() < 6) {
                log.error("❌ Erreur de validation: password trop court ({} caractères)",
                        request.getPassword().length());
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Le mot de passe doit contenir au moins 6 caractères"));
            }

            // Vérifier si l'email existe déjà
            if (userService.findByEmail(request.getEmail()).isPresent()) {
                log.warn("⚠️ Tentative d'inscription avec un email existant: {}", request.getEmail());
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Un utilisateur avec cet email existe déjà"));
            }

            // Créer l'utilisateur avec le rôle USER par défaut
            request.setRole("USER");
            log.info("✅ Création de l'utilisateur avec le rôle: {}", request.getRole());

            User user = userService.createUser(request);
            log.info("✅ Utilisateur créé avec succès, ID: {}", user.getId());

            // Retourner une réponse de succès
            Map<String, Object> response = new HashMap<>();
            response.put("message",
                    "Compte créé avec succès. Veuillez vérifier votre email pour activer votre compte.");
            response.put("userId", user.getId());
            response.put("email", user.getEmail());
            response.put("firstName", user.getFirstName());
            response.put("lastName", user.getLastName());
            response.put("role", user.getRole().name());

            log.info("✅ Inscription réussie pour l'utilisateur: {} {} ({})",
                    user.getFirstName(), user.getLastName(), user.getEmail());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            log.error("❌ Erreur lors de la création du compte pour l'email {}: {}",
                    request.getEmail(), e.getMessage(), e);

            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Erreur lors de la création du compte: " + e.getMessage()));
        }
    }

    @GetMapping("/confirm")
    public ResponseEntity<String> confirmEmail(@RequestParam String token) {
        try {
            boolean verified = userService.verifyEmail(token);
            if (verified) {
                return ResponseEntity.ok("Email vérifié avec succès. Vous pouvez maintenant vous connecter.");
            } else {
                return ResponseEntity.badRequest().body("Token invalide ou expiré.");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur lors de la vérification: " + e.getMessage());
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Non authentifié"));
            }

            String email = authentication.getName();
            User user = userService.findByEmail(email).orElse(null);

            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Utilisateur non trouvé"));
            }

            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", user.getId());
            userInfo.put("email", user.getEmail());
            userInfo.put("firstName", user.getFirstName());
            userInfo.put("lastName", user.getLastName());
            userInfo.put("role", user.getRole().name());
            userInfo.put("emailVerified", user.getEmailVerified());
            userInfo.put("authorities", authentication.getAuthorities().stream()
                    .map(Object::toString)
                    .collect(Collectors.toList()));

            return ResponseEntity.ok(userInfo);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error",
                            "Erreur lors de la récupération des informations utilisateur: " + e.getMessage()));
        }
    }

    // Gestionnaire d'erreurs de validation
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
            log.error("❌ Erreur de validation pour le champ {}: {}", fieldName, errorMessage);
        });

        log.error("❌ Erreurs de validation détectées: {}", errors);
        return ResponseEntity.badRequest().body(errors);
    }

    // Gestionnaire d'erreurs général
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneralExceptions(Exception ex) {
        log.error("❌ Erreur générale dans AuthController: {}", ex.getMessage(), ex);
        Map<String, String> error = new HashMap<>();
        error.put("error", "Une erreur interne s'est produite: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}