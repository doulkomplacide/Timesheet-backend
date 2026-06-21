package com.gestiontemps.securite;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final ServiceUtilisateurPersonnalise customUserDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // Récupération du header Authorization
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        // Vérification du header
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extraction du token
        jwt = authHeader.substring(7);
        userEmail = jwtUtil.extractUsername(jwt);

        // Si l'email est extrait et qu'aucune authentification n'est en cours
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Chargement des détails de l'utilisateur
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(userEmail);

            // Validation du token
            if (jwtUtil.validateToken(jwt, userEmail)) {

                // Création du token d'authentification
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
                );

                // Ajout des détails de la requête
                authToken.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // Définition de l'authentification dans le contexte Spring Security
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // Continuer le filtre
        filterChain.doFilter(request, response);
    }
}