package com.gestiontemps.service;

import com.gestiontemps.dto.requete.RequeteUtilisateur;
import com.gestiontemps.dto.reponse.ReponseUtilisateur;
import com.gestiontemps.entite.Departement;
import com.gestiontemps.entite.ServiceEntite;
import com.gestiontemps.entite.Utilisateur;
import com.gestiontemps.enums.RoleUtilisateur;
import com.gestiontemps.exception.BadRequestException;
import com.gestiontemps.exception.ResourceNotFoundException;
import com.gestiontemps.repository.DepartmentRepository;
import com.gestiontemps.repository.ServiceRepository;
import com.gestiontemps.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final ServiceRepository serviceRepository;
    private final PasswordEncoder passwordEncoder;

    public Page<ReponseUtilisateur> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
            .map(this::convertToResponse);
    }

    public ReponseUtilisateur getUserById(Long id) {
        Utilisateur user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", id));
        return convertToResponse(user);
    }

    public Utilisateur getUserEntityById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", id));
    }

    @Transactional
    public ReponseUtilisateur createUser(RequeteUtilisateur request) {
        // Vérifier si l'email existe déjà
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Cet email est déjà utilisé");
        }

        Utilisateur user = new Utilisateur();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        
        // Définir le rôle
        if (request.getRole() != null) {
            try {
                user.setRole(RoleUtilisateur.valueOf(request.getRole().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("Rôle invalide: " + request.getRole());
            }
        }

        // Définir le département
        if (request.getDepartmentId() != null) {
            Departement department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Département", request.getDepartmentId()));
            user.setDepartment(department);
        }

        // Définir le service
        if (request.getServiceId() != null) {
            ServiceEntite service = serviceRepository.findById(request.getServiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Service", request.getServiceId()));
            user.setService(service);
        }

        user.setActive(true);
        Utilisateur savedUser = userRepository.save(user);
        return convertToResponse(savedUser);
    }

    @Transactional
    public ReponseUtilisateur updateUser(Long id, RequeteUtilisateur request) {
        Utilisateur user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", id));

        // Vérifier si l'email est déjà utilisé par un autre utilisateur
        if (!user.getEmail().equals(request.getEmail()) && 
            userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Cet email est déjà utilisé");
        }

        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());

        // Mettre à jour le mot de passe si fourni
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        // Mettre à jour le rôle
        if (request.getRole() != null) {
            try {
                user.setRole(RoleUtilisateur.valueOf(request.getRole().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("Rôle invalide: " + request.getRole());
            }
        }

        // Mettre à jour le département
        if (request.getDepartmentId() != null) {
            Departement department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Département", request.getDepartmentId()));
            user.setDepartment(department);
        }

        // Mettre à jour le service
        if (request.getServiceId() != null) {
            ServiceEntite service = serviceRepository.findById(request.getServiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Service", request.getServiceId()));
            user.setService(service);
        }

        Utilisateur updatedUser = userRepository.save(user);
        return convertToResponse(updatedUser);
    }

    @Transactional
    public void deleteUser(Long id) {
        Utilisateur user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", id));
        user.setActive(false);
        userRepository.save(user);
    }

    @Transactional
    public void activateUser(Long id) {
        Utilisateur user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", id));
        user.setActive(true);
        userRepository.save(user);
    }

    private ReponseUtilisateur convertToResponse(Utilisateur user) {
        ReponseUtilisateur response = new ReponseUtilisateur();
        response.setId(user.getId());
        response.setEmail(user.getEmail());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setFullName(user.getFullName());
        response.setRole(user.getRole().name());
        response.setDepartmentName(user.getDepartment() != null ? user.getDepartment().getName() : null);
        response.setServiceName(user.getService() != null ? user.getService().getName() : null);
        response.setActive(user.isActive());
        response.setCreatedAt(user.getCreatedAt());
        return response;
    }
}