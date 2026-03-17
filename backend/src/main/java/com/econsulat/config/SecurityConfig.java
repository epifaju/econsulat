package com.econsulat.config;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.util.StringUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

        private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);

        @Value("${app.cors.allowed-origins:http://localhost:5173,http://127.0.0.1:5173}")
        private String corsAllowedOrigins;

        private final JwtAuthenticationFilter jwtAuthFilter;
        private final RateLimitingFilter rateLimitingFilter;
        private final AuthenticationProvider authenticationProvider;

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                                .csrf(csrf -> csrf.disable())
                                .headers(headers -> headers
                                                .frameOptions(frame -> frame.deny())
                                                .contentTypeOptions(content -> {})
                                                .xssProtection(xss -> xss.disable())
                                                .referrerPolicy(ref -> ref.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN))
                                )
                                .exceptionHandling(ex -> ex
                                                .authenticationEntryPoint((request, response, authException) ->
                                                                response.sendError(401, "Unauthorized"))
                                )
                                .authorizeHttpRequests(auth -> auth
                                                // Actuator (health / info pour load balancer)
                                                .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                                                // Endpoints publics (pas d'authentification)
                                                .requestMatchers("/api/auth/**").permitAll()
                                                .requestMatchers("/auth/**").permitAll()
                                                .requestMatchers("/api/demandes/document-types").permitAll()
                                                .requestMatchers("/api/demandes/civilites").permitAll()
                                                .requestMatchers("/api/demandes/pays").permitAll()
                                                .requestMatchers("/api/payment/webhook").permitAll()
                                                .requestMatchers("/api/payment/confirm-session").permitAll()
                                                .requestMatchers("/api/contact").permitAll()
                                                .requestMatchers("/api/faq/**").permitAll()
                                                .requestMatchers("/api/assistant/**").permitAll()
                                                
                                                // Endpoints de demandes (authentification requise)
                                                .requestMatchers("/api/demandes").authenticated()
                                                .requestMatchers("/api/demandes/my").authenticated()
                                                .requestMatchers("/api/demandes/{id}").authenticated()
                                                .requestMatchers("/api/demandes/{id}/generate-document").authenticated()
                                                .requestMatchers("/api/demandes/{id}/download-document").authenticated()
                                                .requestMatchers("/api/payment/create-session").authenticated()
                                                .requestMatchers("/api/users/me").authenticated()
                                                .requestMatchers("/api/me/**").authenticated()
                                                
                                                // Endpoints admin (rôles spécifiques)
                                                .requestMatchers("/api/demandes/*/status").hasAnyRole("ADMIN", "AGENT")
                                                .requestMatchers("/admin/**").hasRole("ADMIN")
                                                .requestMatchers("/api/admin/reports/**").hasRole("ADMIN")
                                                .requestMatchers("/api/admin/**").hasAnyRole("ADMIN", "AGENT")
                                                .requestMatchers("/api/passport/**").hasRole("ADMIN")
                                                .requestMatchers("/api/citizens/my-requests").hasAnyRole("CITIZEN", "USER")
                                                
                                                // Toute autre requête nécessite une authentification
                                                .anyRequest().authenticated())
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .authenticationProvider(authenticationProvider)
                                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                                .addFilterBefore(rateLimitingFilter, UsernamePasswordAuthenticationFilter.class);

                log.debug("Configuration de sécurité initialisée (endpoints publics, demandes, admin, JWT, CORS)");

                return http.build();
        }

        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration configuration = new CorsConfiguration();
                List<String> origins = Arrays.stream(corsAllowedOrigins.split(","))
                                .map(String::trim)
                                .filter(StringUtils::hasText)
                                .collect(Collectors.toList());
                if (origins.isEmpty()) {
                        origins.add("http://localhost:5173");
                }
                configuration.setAllowedOrigins(origins);
                configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                configuration.setAllowedHeaders(Arrays.asList("*"));
                configuration.setAllowCredentials(true);
                configuration.setExposedHeaders(Arrays.asList("Authorization"));

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration);

                log.info("CORS configuré pour les origines: {}", origins);

                return source;
        }
}