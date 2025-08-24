// CORRECTION AUTOMATIQUE - Ajout des règles pour /api/demandes 
// Ajouter ces lignes dans SecurityConfig.java si elles manquent: 
// .requestMatchers("/api/demandes").hasAnyRole("USER", "ADMIN", "AGENT") 
// .requestMatchers("/api/demandes/**").hasAnyRole("USER", "ADMIN", "AGENT") 
