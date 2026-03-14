package com.econsulat.config;

import com.econsulat.model.User;
import com.econsulat.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Admin par défaut : créer ou mettre à jour le mot de passe (admin123)
        userRepository.findByEmail("admin@econsulat.com").ifPresentOrElse(
            admin -> {
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setFirstName("Administrateur");
                admin.setLastName("Système");
                admin.setEmailVerified(true);
                userRepository.save(admin);
                log.info("Utilisateur admin par défaut mis à jour: admin@econsulat.com");
            },
            () -> {
                User admin = new User();
                admin.setFirstName("Administrateur");
                admin.setLastName("Système");
                admin.setEmail("admin@econsulat.com");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRole(User.Role.ADMIN);
                admin.setEmailVerified(true);
                admin.setCreatedAt(LocalDateTime.now());
                userRepository.save(admin);
                log.info("Utilisateur admin par défaut créé: admin@econsulat.com");
            }
        );

        // User par défaut : créer ou mettre à jour le mot de passe (user123)
        userRepository.findByEmail("user@econsulat.com").ifPresentOrElse(
            user -> {
                user.setPassword(passwordEncoder.encode("user123"));
                user.setFirstName("Utilisateur");
                user.setLastName("Test");
                user.setEmailVerified(true);
                userRepository.save(user);
                log.info("Utilisateur test par défaut mis à jour: user@econsulat.com");
            },
            () -> {
                User user = new User();
                user.setFirstName("Utilisateur");
                user.setLastName("Test");
                user.setEmail("user@econsulat.com");
                user.setPassword(passwordEncoder.encode("user123"));
                user.setRole(User.Role.USER);
                user.setEmailVerified(true);
                user.setCreatedAt(LocalDateTime.now());
                userRepository.save(user);
                log.info("Utilisateur test par défaut créé: user@econsulat.com");
            }
        );

        log.info("eConsulat démarré - Frontend: http://localhost:5173, Backend: http://localhost:8080");
    }
}