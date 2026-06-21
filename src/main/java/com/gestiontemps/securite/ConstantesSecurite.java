package com.gestiontemps.securite;

public class SecurityConstants {
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final String[] PUBLIC_URLS = {
        "/api/auth/login",
        "/api/auth/register",
        "/api/auth/refresh",
        "/swagger-ui/**",
        "/v3/api-docs/**",
        "/api/public/**"
    };
}