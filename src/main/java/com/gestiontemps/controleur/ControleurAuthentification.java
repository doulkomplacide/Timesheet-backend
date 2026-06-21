package com.gestiontemps.controleur;

import com.gestiontemps.dto.requete.RequeteAuthentification;
import com.gestiontemps.dto.requete.RequeteInscription;
import com.gestiontemps.dto.reponse.ReponseApi;
import com.gestiontemps.dto.reponse.ReponseAuthentification;
import com.gestiontemps.service.ServiceAuthentification;
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

    private final ServiceAuthentification authService;

    /**
     * Endpoint de connexion
     * POST /api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<ReponseApi<ReponseAuthentification>> login(
            @Valid @RequestBody RequeteAuthentification request) {
        ReponseAuthentification response = authService.login(request);
        return ResponseEntity.ok(
            ReponseApi.success("Connexion réussie", response)
        );
    }

    /**
     * Endpoint d'inscription
     * POST /api/auth/register
     */
    @PostMapping("/register")
    public ResponseEntity<ReponseApi<ReponseAuthentification>> register(
            @Valid @RequestBody RequeteInscription request) {
        ReponseAuthentification response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ReponseApi.success("Inscription réussie", response));
    }

    /**
     * Endpoint de rafraîchissement du token
     * POST /api/auth/refresh
     */
    @PostMapping("/refresh")
    public ResponseEntity<ReponseApi<ReponseAuthentification>> refreshToken(
            @RequestHeader("Authorization") String authorizationHeader) {
        
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest()
                .body(ReponseApi.error("Header Authorization invalide"));
        }

        String refreshToken = authorizationHeader.substring(7);
        ReponseAuthentification response = authService.refreshToken(refreshToken);
        return ResponseEntity.ok(
            ReponseApi.success("Token rafraîchi avec succès", response)
        );
    }

    /**
     * Endpoint de déconnexion
     * POST /api/auth/logout
     */
    @PostMapping("/logout")
    public ResponseEntity<ReponseApi<Void>> logout() {
        authService.logout();
        return ResponseEntity.ok(
            ReponseApi.success("Déconnexion réussie", null)
        );
    }

    /**
     * Vérification de l'état de l'authentification
     * GET /api/auth/check
     */
    @GetMapping("/check")
    public ResponseEntity<ReponseApi<Boolean>> checkAuth() {
        boolean isAuthenticated = authService.isAuthenticated();
        return ResponseEntity.ok(
            ReponseApi.success(isAuthenticated)
        );
    }

    /**
     * Récupération du profil utilisateur courant
     * GET /api/auth/me
     */
    @GetMapping("/me")
    public ResponseEntity<ReponseApi<ReponseAuthentification>> getCurrentUser() {
        // Implémenter la récupération du profil
        // Retourner les infos de l'utilisateur authentifié
        return ResponseEntity.ok(
            ReponseApi.success("Utilisateur authentifié", null)
        );
    }
}