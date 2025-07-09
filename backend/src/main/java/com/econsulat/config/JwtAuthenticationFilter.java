package com.econsulat.config;

import com.econsulat.service.JwtService;
import com.econsulat.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserService userService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        // Debug: Log de la requête
        System.out.println("🔍 JWT Filter - URL: " + request.getRequestURI());
        System.out.println("🔍 JWT Filter - Authorization header: " + authHeader);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("❌ JWT Filter - Pas de token Bearer trouvé");
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);

        // Vérifier que le token n'est pas vide
        if (jwt == null || jwt.trim().isEmpty()) {
            System.out.println("❌ JWT Filter - Token vide");
            filterChain.doFilter(request, response);
            return;
        }

        try {
            username = jwtService.extractUsername(jwt);
            System.out.println("🔍 JWT Filter - Username extrait: " + username);
        } catch (Exception e) {
            System.out.println("❌ JWT Filter - Erreur lors de l'extraction du token: " + e.getMessage());
            filterChain.doFilter(request, response);
            return;
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userService.loadUserByUsername(username);

            if (jwtService.isTokenValid(jwt, userDetails)) {
                System.out.println("✅ JWT Filter - Token valide pour: " + username);
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            } else {
                System.out.println("❌ JWT Filter - Token invalide pour: " + username);
            }
        } else {
            System.out.println("❌ JWT Filter - Username null ou déjà authentifié");
        }
        filterChain.doFilter(request, response);
    }
}