package com.timesheet.controller;

import com.timesheet.dto.request.AuthRequest;
import com.timesheet.dto.request.RegisterRequest;
import com.timesheet.dto.response.ApiResponse;
import com.timesheet.dto.response.AuthResponse;
import com.timesheet.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Endpoint de connexion
     * POST /api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody AuthRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(
            ApiResponse.success("Connexion réussie", response)
        );
    }

    /**
     * Endpoint d'inscription
     * POST /api/auth/register
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Inscription réussie", response));
    }

    /**
     * Endpoint de rafraîchissement du token
     * POST /api/auth/refresh
     */
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(
            @RequestHeader("Authorization") String authorizationHeader) {
        
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Header Authorization invalide"));
        }

        String refreshToken = authorizationHeader.substring(7);
        AuthResponse response = authService.refreshToken(refreshToken);
        return ResponseEntity.ok(
            ApiResponse.success("Token rafraîchi avec succès", response)
        );
    }

    /**
     * Endpoint de déconnexion
     * POST /api/auth/logout
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout() {
        authService.logout();
        return ResponseEntity.ok(
            ApiResponse.success("Déconnexion réussie", null)
        );
    }

    /**
     * Vérification de l'état de l'authentification
     * GET /api/auth/check
     */
    @GetMapping("/check")
    public ResponseEntity<ApiResponse<Boolean>> checkAuth() {
        boolean isAuthenticated = authService.isAuthenticated();
        return ResponseEntity.ok(
            ApiResponse.success(isAuthenticated)
        );
    }

    /**
     * Récupération du profil utilisateur courant
     * GET /api/auth/me
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<AuthResponse>> getCurrentUser() {
        // Implémenter la récupération du profil
        // Retourner les infos de l'utilisateur authentifié
        return ResponseEntity.ok(
            ApiResponse.success("Utilisateur authentifié", null)
        );
    }
}