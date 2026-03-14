package com.econsulat.controller;

import com.econsulat.dto.AuthRequest;
import com.econsulat.dto.AuthResponse;
import com.econsulat.dto.ForgotPasswordRequest;
import com.econsulat.dto.ResetPasswordRequest;
import com.econsulat.dto.UserRequest;
import com.econsulat.service.AuthenticationService;
import com.econsulat.service.EmailService;
import com.econsulat.service.PasswordResetTokenService;
import com.econsulat.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.econsulat.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
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
    private final PasswordResetTokenService passwordResetTokenService;
    private final EmailService emailService;

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
        log.info("Tentative d'inscription pour l'email: {}", request.getEmail());

        try {
            Locale locale = getLocale();

            if (userService.findByEmail(request.getEmail()).isPresent()) {
                log.warn("Tentative d'inscription avec un email existant: {}", request.getEmail());
                return ResponseEntity.badRequest()
                        .body(Map.of("error", messageSource.getMessage("auth.emailAlreadyExists", null, locale)));
            }

            request.setRole("USER");
            User user = userService.createUser(request);
            log.info("Utilisateur créé avec succès, ID: {}", user.getId());

            Map<String, Object> response = new HashMap<>();
            response.put("message", messageSource.getMessage("auth.registerSuccess", null, getLocale()));
            response.put("userId", user.getId());
            response.put("email", user.getEmail());
            response.put("firstName", user.getFirstName());
            response.put("lastName", user.getLastName());
            response.put("role", user.getRole().name());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            log.error("Erreur création compte pour email {}: {}", request.getEmail(), e.getMessage(), e);
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

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        Locale locale = getLocale();
        String email = request.getEmail().trim().toLowerCase();
        userService.findByEmail(email).ifPresent(user -> {
            String token = passwordResetTokenService.createToken(email);
            emailService.sendPasswordResetEmail(user.getEmail(), token, locale);
        });
        String message = messageSource.getMessage("auth.forgotPasswordSuccess", null, locale);
        return ResponseEntity.ok(Map.of("message", message));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        Locale locale = getLocale();
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", messageSource.getMessage("auth.passwordsDoNotMatch", null, locale)));
        }
        try {
            userService.resetPasswordWithToken(request.getToken(), request.getNewPassword(), passwordResetTokenService);
            return ResponseEntity.ok(Map.of("message", messageSource.getMessage("auth.resetPasswordSuccess", null, locale)));
        } catch (RuntimeException e) {
            String key = e.getMessage().contains("invalide") || e.getMessage().contains("expiré")
                    ? "auth.resetPasswordInvalidToken"
                    : "auth.resetPasswordError";
            return ResponseEntity.badRequest()
                    .body(Map.of("error", messageSource.getMessage(key, null, locale)));
        }
    }
}
