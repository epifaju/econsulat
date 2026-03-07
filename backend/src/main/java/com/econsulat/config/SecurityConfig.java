package com.econsulat.config;

import com.econsulat.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

        private final JwtAuthenticationFilter jwtAuthFilter;
        private final AuthenticationProvider authenticationProvider;

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                                .csrf(csrf -> csrf.disable())
                                .authorizeHttpRequests(auth -> auth
                                                // Endpoints publics (pas d'authentification)
                                                .requestMatchers("/api/auth/**").permitAll()
                                                .requestMatchers("/auth/**").permitAll()
                                                .requestMatchers("/api/demandes/document-types").permitAll()
                                                .requestMatchers("/api/demandes/civilites").permitAll()
                                                .requestMatchers("/api/demandes/pays").permitAll()
                                                .requestMatchers("/api/payment/webhook").permitAll()
                                                .requestMatchers("/api/payment/confirm-session").permitAll()
                                                
                                                // Endpoints de demandes (authentification requise)
                                                .requestMatchers("/api/demandes").authenticated()
                                                .requestMatchers("/api/demandes/my").authenticated()
                                                .requestMatchers("/api/demandes/{id}").authenticated()
                                                .requestMatchers("/api/demandes/{id}/generate-document").authenticated()
                                                .requestMatchers("/api/demandes/{id}/download-document").authenticated()
                                                .requestMatchers("/api/payment/create-session").authenticated()
                                                
                                                // Endpoints admin (rôles spécifiques)
                                                .requestMatchers("/api/demandes/*/status").hasAnyRole("ADMIN", "AGENT")
                                                .requestMatchers("/admin/**").hasRole("ADMIN")
                                                .requestMatchers("/api/admin/**").hasAnyRole("ADMIN", "AGENT")
                                                .requestMatchers("/api/passport/**").hasRole("ADMIN")
                                                .requestMatchers("/api/citizens/my-requests").hasAnyRole("CITIZEN", "USER")
                                                
                                                // Toute autre requête nécessite une authentification
                                                .anyRequest().authenticated())
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .authenticationProvider(authenticationProvider)
                                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

                // Ajouter des logs de débogage détaillés
                System.out.println("🔧 SecurityConfig - Configuration de sécurité créée");
                System.out.println("🔧 SecurityConfig - Endpoints publics configurés");
                System.out.println("🔧 SecurityConfig - Endpoints demandes configurés avec .authenticated()");
                System.out.println("🔧 SecurityConfig - Endpoints admin configurés avec hasAnyRole('ADMIN', 'AGENT')");
                System.out.println("🔧 SecurityConfig - JWT Filter configuré");
                System.out.println("🔧 SecurityConfig - CORS configuré");
                System.out.println("🔧 SecurityConfig - CSRF désactivé");

                return http.build();
        }

        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration configuration = new CorsConfiguration();
                // Autoriser les origines du frontend (Vite dev server)
                configuration.setAllowedOrigins(Arrays.asList(
                                "http://localhost:5173",
                                "http://127.0.0.1:5173",
                                "http://localhost:3000",
                                "http://127.0.0.1:3000"));
                configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                configuration.setAllowedHeaders(Arrays.asList("*"));
                configuration.setAllowCredentials(true);
                configuration.setExposedHeaders(Arrays.asList("Authorization"));

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration);
                
                System.out.println("🔧 SecurityConfig - CORS configuré pour les origines: " + configuration.getAllowedOrigins());
                
                return source;
        }
}