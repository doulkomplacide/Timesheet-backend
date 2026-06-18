package com.timesheet.service;

import com.timesheet.dto.request.AuthRequest;
import com.timesheet.dto.request.RegisterRequest;
import com.timesheet.dto.response.AuthResponse;
import com.timesheet.entity.Department;
import com.timesheet.entity.ServiceEntity;
import com.timesheet.entity.User;
import com.timesheet.enums.UserRole;
import com.timesheet.exception.BadRequestException;
import com.timesheet.exception.ResourceNotFoundException;
import com.timesheet.exception.UnauthorizedException;
import com.timesheet.repository.DepartmentRepository;
import com.timesheet.repository.ServiceRepository;
import com.timesheet.repository.UserRepository;
import com.timesheet.security.JwtUtil;
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
    public AuthResponse login(AuthRequest request) {
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
            User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Utilisateur non trouvé"));

            // Vérifier si le compte est actif
            if (!user.isActive()) {
                throw new UnauthorizedException("Compte désactivé. Contactez l'administrateur.");
            }

            // Générer le token JWT
            String token = jwtUtil.generateToken(user.getEmail());

            // Construire la réponse
            return AuthResponse.builder()
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
    public AuthResponse register(RegisterRequest request) {
        // Vérifier si l'email existe déjà
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Cet email est déjà utilisé");
        }

        // Créer le nouvel utilisateur
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());

        // Définir le rôle
        UserRole role = UserRole.EMPLOYEE;
        if (request.getRole() != null) {
            try {
                role = UserRole.valueOf(request.getRole().toUpperCase());
            } catch (IllegalArgumentException e) {
                // Rôle invalide, on garde EMPLOYEE
            }
        }
        user.setRole(role);

        // Assigner le département
        if (request.getDepartmentId() != null) {
            Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Département", request.getDepartmentId()));
            user.setDepartment(department);
        }

        // Assigner le service
        if (request.getServiceId() != null) {
            ServiceEntity service = serviceRepository.findById(request.getServiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Service", request.getServiceId()));
            user.setService(service);
        }

        user.setActive(true);
        User savedUser = userRepository.save(user);

        // Générer le token JWT
        String token = jwtUtil.generateToken(savedUser.getEmail());

        // Construire la réponse
        return AuthResponse.builder()
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
    public AuthResponse refreshToken(String refreshToken) {
        try {
            // Extraire l'email du token
            String email = jwtUtil.extractUsername(refreshToken);

            // Vérifier si le token est valide
            if (!jwtUtil.validateToken(refreshToken)) {
                throw new UnauthorizedException("Token invalide ou expiré");
            }

            // Récupérer l'utilisateur
            User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Utilisateur non trouvé"));

            if (!user.isActive()) {
                throw new UnauthorizedException("Compte désactivé");
            }

            // Générer un nouveau token
            String newToken = jwtUtil.generateToken(user.getEmail());

            return AuthResponse.builder()
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
    public User getCurrentUser() {
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