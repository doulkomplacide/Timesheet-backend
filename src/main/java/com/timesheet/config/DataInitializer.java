package com.timesheet.config;

import com.timesheet.entity.User;
import com.timesheet.enums.UserRole;
import com.timesheet.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initDatabase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // 1. On vérifie si la base de données contient déjà des utilisateurs
            if (userRepository.count() == 0) {

                // 2. Création de l'Administrateur
                User admin = new User();
                admin.setEmail("admin@example.com");
                // On utilise le passwordEncoder injecté pour chiffrer proprement en BCrypt
                admin.setPassword(passwordEncoder.encode("password123"));
                admin.setFirstName("John");
                admin.setLastName("Doe");
                admin.setRole(UserRole.ADMIN);
                admin.setActive(true);

                userRepository.save(admin);

                // 3. Création de l'Employé
                User employee = new User();
                employee.setEmail("employee@example.com");
                employee.setPassword(passwordEncoder.encode("password123"));
                employee.setFirstName("Jane");
                employee.setLastName("Smith");
                employee.setRole(UserRole.EMPLOYEE);
                employee.setActive(true);

                userRepository.save(employee);

                System.out.println(">>> Données de test initialisées avec succès !");
                System.out.println(">>> Admin : admin@example.com / password123");
                System.out.println(">>> Employé : employee@example.com / password123");
            } else {
                System.out.println(">>> Des utilisateurs existent déjà en base. Pas d'initialisation requise.");
            }
        };
    }
}