package com.gestiontemps.service;

import com.gestiontemps.dto.requete.RequeteAuthentification;
import com.gestiontemps.dto.requete.RequeteInscription;
import com.gestiontemps.dto.reponse.ReponseAuthentification;
import com.gestiontemps.entite.Departement;
import com.gestiontemps.entite.ServiceEntite;
import com.gestiontemps.entite.Utilisateur;
import com.gestiontemps.enums.RoleUtilisateur;
import com.gestiontemps.exception.BadRequestException;
import com.gestiontemps.exception.ResourceNotFoundException;
import com.gestiontemps.exception.UnauthorizedException;
import com.gestiontemps.repository.DepartmentRepository;
import com.gestiontemps.repository.ServiceRepository;
import com.gestiontemps.repository.UserRepository;
import com.gestiontemps.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final ServiceRepository serviceRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Authentification d'un utilisateur
     */
    public ReponseAuthentification login(RequeteAuthentification request) {
        try {
            // Authentification avec Spring Security
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getEmail(),
                    request.getPassword()
                )
            );

            // Définir l'authentification dans le contexte
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Récupérer l'utilisateur
            Utilisateur user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Utilisateur non trouvé"));

            // Vérifier si le compte est actif
            if (!user.isActive()) {
                throw new UnauthorizedException("Compte désactivé. Contactez l'administrateur.");
            }

            // Générer le token JWT
            String token = jwtUtil.generateToken(user.getEmail());

            // Construire la réponse
            return ReponseAuthentification.builder()
                .token(token)
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .fullName(user.getFullName())
                .role(user.getRole().name())
                .userId(user.getId())
                .tokenType("Bearer")
                .expiresIn(jwtUtil.extractExpiration(token).getTime())
                .build();

        } catch (Exception e) {
            throw new UnauthorizedException("Email ou mot de passe incorrect");
        }
    }

    /**
     * Inscription d'un nouvel utilisateur
     */
    @Transactional
    public ReponseAuthentification register(RequeteInscription request) {
        // Vérifier si l'email existe déjà
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Cet email est déjà utilisé");
        }

        // Créer le nouvel utilisateur
        Utilisateur user = new Utilisateur();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());

        // Définir le rôle
        RoleUtilisateur role = RoleUtilisateur.EMPLOYEE;
        if (request.getRole() != null) {
            try {
                role = RoleUtilisateur.valueOf(request.getRole().toUpperCase());
            } catch (IllegalArgumentException e) {
                // Rôle invalide, on garde EMPLOYEE
            }
        }
        user.setRole(role);

        // Assigner le département
        if (request.getDepartmentId() != null) {
            Departement department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Département", request.getDepartmentId()));
            user.setDepartment(department);
        }

        // Assigner le service
        if (request.getServiceId() != null) {
            ServiceEntite service = serviceRepository.findById(request.getServiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Service", request.getServiceId()));
            user.setService(service);
        }

        user.setActive(true);
        Utilisateur savedUser = userRepository.save(user);

        // Générer le token JWT
        String token = jwtUtil.generateToken(savedUser.getEmail());

        // Construire la réponse
        return ReponseAuthentification.builder()
            .token(token)
            .email(savedUser.getEmail())
            .firstName(savedUser.getFirstName())
            .lastName(savedUser.getLastName())
            .fullName(savedUser.getFullName())
            .role(savedUser.getRole().name())
            .userId(savedUser.getId())
            .tokenType("Bearer")
            .expiresIn(jwtUtil.extractExpiration(token).getTime())
            .build();
    }

    /**
     * Rafraîchissement du token JWT
     */
    public ReponseAuthentification refreshToken(String refreshToken) {
        try {
            // Extraire l'email du token
            String email = jwtUtil.extractUsername(refreshToken);

            // Vérifier si le token est valide
            if (!jwtUtil.validateToken(refreshToken)) {
                throw new UnauthorizedException("Token invalide ou expiré");
            }

            // Récupérer l'utilisateur
            Utilisateur user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Utilisateur non trouvé"));

            if (!user.isActive()) {
                throw new UnauthorizedException("Compte désactivé");
            }

            // Générer un nouveau token
            String newToken = jwtUtil.generateToken(user.getEmail());

            return ReponseAuthentification.builder()
                .token(newToken)
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .fullName(user.getFullName())
                .role(user.getRole().name())
                .userId(user.getId())
                .tokenType("Bearer")
                .expiresIn(jwtUtil.extractExpiration(newToken).getTime())
                .build();

        } catch (Exception e) {
            throw new UnauthorizedException("Token invalide");
        }
    }

    /**
     * Récupérer l'utilisateur actuellement authentifié
     */
    public Utilisateur getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("Utilisateur non authentifié");
        }

        String email = authentication.getName();
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new UnauthorizedException("Utilisateur non trouvé"));
    }

    /**
     * Récupérer l'ID de l'utilisateur actuellement authentifié
     */
    public Long getCurrentUserId() {
        return getCurrentUser().getId();
    }

    /**
     * Vérifier si l'utilisateur est authentifié
     */
    public boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated()
            && !"anonymousUser".equals(authentication.getPrincipal());
    }

    /**
     * Déconnexion (nettoyage du contexte)
     */
    public void logout() {
        SecurityContextHolder.clearContext();
    }
}