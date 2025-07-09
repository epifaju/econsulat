package com.econsulat.service;

import com.econsulat.dto.AuthRequest;
import com.econsulat.dto.AuthResponse;
import com.econsulat.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse login(AuthRequest request) {
        // Vérifier si l'utilisateur existe et si son email est vérifié
        User user = userService.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'email : " + request.getEmail()));
        if (!user.getEmailVerified()) {
            throw new RuntimeException("Veuillez vérifier votre adresse email avant de vous connecter");
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        UserDetails userDetails = userService.loadUserByUsername(request.getEmail());

        String token = jwtService.generateToken(userDetails);

        return new AuthResponse(token, user.getEmail(), user.getRole(), "Connexion réussie");
    }

    public AuthResponse register(AuthRequest request) {
        // Cette méthode n'est plus utilisée car la création d'utilisateur se fait via
        // l'admin
        throw new UnsupportedOperationException(
                "L'inscription directe n'est pas autorisée. Contactez un administrateur.");
    }
}