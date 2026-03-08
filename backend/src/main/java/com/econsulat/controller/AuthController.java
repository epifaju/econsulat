package com.econsulat.controller;

import com.econsulat.dto.AuthRequest;
import com.econsulat.dto.AuthResponse;
import com.econsulat.dto.UserRequest;
import com.econsulat.service.AuthenticationService;
import com.econsulat.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.econsulat.model.User;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.context.i18n.LocaleContextHolder;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
@Slf4j
public class AuthController {

    private static final List<Locale> SUPPORTED = List.of(Locale.FRENCH, new Locale("pt"));

    private final AuthenticationService authenticationService;
    private final UserService userService;
    private final MessageSource messageSource;

    private Locale getLocale() {
        Locale requestLocale = LocaleContextHolder.getLocale();
        if (SUPPORTED.stream().anyMatch(l -> l.getLanguage().equals(requestLocale.getLanguage()))) {
            return requestLocale.getLanguage().equals("pt") ? new Locale("pt") : Locale.FRENCH;
        }
        return Locale.FRENCH;
    }

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
            Locale locale = getLocale();

            if (request.getFirstName() == null || request.getFirstName().trim().isEmpty()) {
                log.error("❌ Erreur de validation: firstName est vide ou null");
                return ResponseEntity.badRequest()
                        .body(Map.of("error", messageSource.getMessage("validation.firstName.required", null, locale)));
            }

            if (request.getLastName() == null || request.getLastName().trim().isEmpty()) {
                log.error("❌ Erreur de validation: lastName est vide ou null");
                return ResponseEntity.badRequest()
                        .body(Map.of("error", messageSource.getMessage("validation.lastName.required", null, locale)));
            }

            if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
                log.error("❌ Erreur de validation: email est vide ou null");
                return ResponseEntity.badRequest()
                        .body(Map.of("error", messageSource.getMessage("validation.email.required", null, locale)));
            }

            if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
                log.error("❌ Erreur de validation: password est vide ou null");
                return ResponseEntity.badRequest()
                        .body(Map.of("error", messageSource.getMessage("validation.password.required", null, locale)));
            }

            if (request.getPassword().length() < 6) {
                log.error("❌ Erreur de validation: password trop court ({} caractères)",
                        request.getPassword().length());
                return ResponseEntity.badRequest()
                        .body(Map.of("error", messageSource.getMessage("validation.password.minLength", null, locale)));
            }

            // Vérifier si l'email existe déjà
            if (userService.findByEmail(request.getEmail()).isPresent()) {
                log.warn("⚠️ Tentative d'inscription avec un email existant: {}", request.getEmail());
                return ResponseEntity.badRequest()
                        .body(Map.of("error", messageSource.getMessage("auth.emailAlreadyExists", null, locale)));
            }

            // Créer l'utilisateur avec le rôle USER par défaut
            request.setRole("USER");
            log.info("✅ Création de l'utilisateur avec le rôle: {}", request.getRole());

            User user = userService.createUser(request);
            log.info("✅ Utilisateur créé avec succès, ID: {}", user.getId());

            // Retourner une réponse de succès
            Map<String, Object> response = new HashMap<>();
            response.put("message", messageSource.getMessage("auth.registerSuccess", null, getLocale()));
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
            String msg = messageSource.getMessage("auth.registerError", null, getLocale()) + " " + e.getMessage();
            return ResponseEntity.badRequest().body(Map.of("error", msg));
        }
    }

    @GetMapping("/confirm")
    public ResponseEntity<String> confirmEmail(@RequestParam String token) {
        Locale locale = getLocale();
        try {
            boolean verified = userService.verifyEmail(token);
            if (verified) {
                return ResponseEntity.ok(messageSource.getMessage("auth.confirmEmailSuccess", null, locale));
            } else {
                return ResponseEntity.badRequest().body(messageSource.getMessage("auth.confirmEmailInvalidToken", null, locale));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(messageSource.getMessage("auth.confirmEmailError", null, locale) + " " + e.getMessage());
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        Locale locale = getLocale();
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", messageSource.getMessage("auth.unauthorized", null, locale)));
            }

            String email = authentication.getName();
            User user = userService.findByEmail(email).orElse(null);

            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", messageSource.getMessage("auth.userNotFound", null, locale)));
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
                    .body(Map.of("error", messageSource.getMessage("auth.getUserError", null, getLocale()) + " " + e.getMessage()));
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
        error.put("error", messageSource.getMessage("error.generic", null, getLocale()) + " " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}