package com.gestiontemps.controller;

import com.gestiontemps.dto.requete.RequeteUtilisateur;
import com.gestiontemps.dto.reponse.ReponseApi;
import com.gestiontemps.dto.reponse.ReponseUtilisateur;
import com.gestiontemps.service.ServiceUtilisateur;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final ServiceUtilisateur userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ReponseApi<Page<ReponseUtilisateur>>> getAllUsers(
            @PageableDefault(size = 20) Pageable pageable) {
        Page<ReponseUtilisateur> users = userService.getAllUsers(pageable);
        return ResponseEntity.ok(ReponseApi.success(users));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReponseApi<ReponseUtilisateur>> getUserById(@PathVariable Long id) {
        ReponseUtilisateur user = userService.getUserById(id);
        return ResponseEntity.ok(ReponseApi.success(user));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ReponseApi<ReponseUtilisateur>> createUser(@Valid @RequestBody RequeteUtilisateur request) {
        ReponseUtilisateur user = userService.createUser(request);
        return ResponseEntity.ok(ReponseApi.success("Utilisateur créé avec succès", user));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ReponseApi<ReponseUtilisateur>> updateUser(
            @PathVariable Long id, 
            @Valid @RequestBody RequeteUtilisateur request) {
        ReponseUtilisateur user = userService.updateUser(id, request);
        return ResponseEntity.ok(ReponseApi.success("Utilisateur mis à jour avec succès", user));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ReponseApi<Void>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(ReponseApi.success("Utilisateur désactivé avec succès", null));
    }

    @PostMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ReponseApi<Void>> activateUser(@PathVariable Long id) {
        userService.activateUser(id);
        return ResponseEntity.ok(ReponseApi.success("Utilisateur activé avec succès", null));
    }
}