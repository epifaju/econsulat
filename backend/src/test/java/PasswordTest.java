package test.java;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordTest {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        System.out.println("🔍 Test des mots de passe BCrypt...\n");

        // Test admin
        String adminPassword = "admin123";
        String adminHash = "$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa";
        boolean adminValid = encoder.matches(adminPassword, adminHash);

        System.out.println("Admin:");
        System.out.println("  Mot de passe: " + adminPassword);
        System.out.println("  Hash: " + adminHash);
        System.out.println("  Valide: " + (adminValid ? "✅ OUI" : "❌ NON"));
        System.out.println();

        // Test user
        String userPassword = "user123";
        String userHash = "$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a";
        boolean userValid = encoder.matches(userPassword, userHash);

        System.out.println("User:");
        System.out.println("  Mot de passe: " + userPassword);
        System.out.println("  Hash: " + userHash);
        System.out.println("  Valide: " + (userValid ? "✅ OUI" : "❌ NON"));
        System.out.println();

        // Générer de nouveaux hashes
        System.out.println("🔄 Génération de nouveaux hashes...\n");
        String newAdminHash = encoder.encode(adminPassword);
        String newUserHash = encoder.encode(userPassword);

        System.out.println("Nouveau hash admin: " + newAdminHash);
        System.out.println("Nouveau hash user: " + newUserHash);
    }
}