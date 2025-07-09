package com.econsulat.config;

import com.econsulat.model.User;
import com.econsulat.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Créer un admin par défaut s'il n'existe pas
        if (!userRepository.existsByEmail("admin@econsulat.com")) {
            User admin = new User();
            admin.setFirstName("Administrateur");
            admin.setLastName("Système");
            admin.setEmail("admin@econsulat.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(User.Role.ADMIN);
            admin.setEmailVerified(true);
            admin.setCreatedAt(LocalDateTime.now());
            userRepository.save(admin);
            System.out.println("✅ Utilisateur admin créé: admin@econsulat.com / admin123");
        }

        // Créer un utilisateur par défaut s'il n'existe pas
        if (!userRepository.existsByEmail("user@econsulat.com")) {
            User user = new User();
            user.setFirstName("Utilisateur");
            user.setLastName("Test");
            user.setEmail("user@econsulat.com");
            user.setPassword(passwordEncoder.encode("user123"));
            user.setRole(User.Role.USER);
            user.setEmailVerified(true);
            user.setCreatedAt(LocalDateTime.now());
            userRepository.save(user);
            System.out.println("✅ Utilisateur citoyen créé: user@econsulat.com / user123");
        }

        System.out.println("🚀 eConsulat démarré avec succès!");
        System.out.println("📱 Frontend: http://localhost:5173");
        System.out.println("🔧 Backend: http://localhost:8080");
    }
}