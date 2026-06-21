package com.gestiontemps.config;

import com.gestiontemps.entite.Utilisateur;
import com.gestiontemps.enums.RoleUtilisateur;
import com.gestiontemps.repository.UtilisateurRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class InitialisationDonnees {

    @Bean
    public CommandLineRunner initialiserBaseDonnees(UtilisateurRepository repositoryUtilisateur,
                                                    PasswordEncoder encodeurMotDePasse) {
        return args -> {
            if (repositoryUtilisateur.count() == 0) {
                // Création de l'Administrateur
                Utilisateur admin = new Utilisateur();
                admin.setEmail("admin@exemple.com");
                admin.setMotDePasse(encodeurMotDePasse.encode("motdepasse123"));
                admin.setPrenom("Jean");
                admin.setNom("Dupont");
                admin.setRole(RoleUtilisateur.ADMIN);
                admin.setActif(true);
                repositoryUtilisateur.save(admin);

                // Création de l'Employé
                Utilisateur employe = new Utilisateur();
                employe.setEmail("employe@exemple.com");
                employe.setMotDePasse(encodeurMotDePasse.encode("motdepasse123"));
                employe.setPrenom("Marie");
                employe.setNom("Martin");
                employe.setRole(RoleUtilisateur.EMPLOYE);
                employe.setActif(true);
                repositoryUtilisateur.save(employe);

                System.out.println(">>> Données de test initialisées avec succès !");
                System.out.println(">>> Admin : admin@exemple.com / motdepasse123");
                System.out.println(">>> Employé : employe@exemple.com / motdepasse123");
            }
        };
    }
}