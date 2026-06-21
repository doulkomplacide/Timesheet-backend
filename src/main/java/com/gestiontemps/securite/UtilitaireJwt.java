package com.gestiontemps.securite;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    /**
     * Récupère la clé de signature
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Extrait l'email (username) du token
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extrait la date d'expiration du token
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extrait un claim spécifique du token
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extrait tous les claims du token
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
    }

    /**
     * Vérifie si le token est expiré
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Génère un token JWT pour un utilisateur
     */
    public String generateToken(String email) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "access_token");
        claims.put("created_at", new Date());
        return createToken(claims, email);
    }

    /**
     * Génère un token JWT avec des claims personnalisés
     */
    public String generateTokenWithClaims(String email, Map<String, Object> extraClaims) {
        Map<String, Object> claims = new HashMap<>(extraClaims);
        claims.put("type", "access_token");
        claims.put("created_at", new Date());
        return createToken(claims, email);
    }

    /**
     * Crée le token JWT
     */
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
            .setClaims(claims)
            .setSubject(subject)
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + expiration))
            .signWith(getSigningKey(), SignatureAlgorithm.HS256)
            .compact();
    }

    /**
     * Valide le token JWT
     */
    public Boolean validateToken(String token, String email) {
        final String username = extractUsername(token);
        return (username.equals(email) && !isTokenExpired(token));
    }

    /**
     * Valide le token sans vérifier l'email
     */
    public Boolean validateToken(String token) {
        return !isTokenExpired(token);
    }

    /**
     * Récupère la date de création du token
     */
    public Date getIssuedAt(String token) {
        return extractClaim(token, Claims::getIssuedAt);
    }

    /**
     * Vérifie si le token peut être rafraîchi
     */
    public Boolean canRefreshToken(String token) {
        return validateToken(token);
    }

    /**
     * Génère un token de rafraîchissement
     */
    public String generateRefreshToken(String email) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "refresh_token");
        claims.put("created_at", new Date());

        // Durée plus longue pour le refresh token (7 jours)
        long refreshExpiration = expiration * 7;

        return Jwts.builder()
            .setClaims(claims)
            .setSubject(email)
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + refreshExpiration))
            .signWith(getSigningKey(), SignatureAlgorithm.HS256)
            .compact();
    }
}