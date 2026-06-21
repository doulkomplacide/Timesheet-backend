package com.gestiontemps.config;

import com.gestiontemps.securite.ServiceUtilisateurPersonnalise;
import com.gestiontemps.securite.FiltreAuthentificationJwt;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final FiltreAuthentificationJwt filtreAuthentificationJwt;
    private final UserDetailsService serviceUtilisateurDetails;
    private final PasswordEncoder encodeurMotDePasse;

    @Bean
    public AuthenticationManager gestionnaireAuthentification(
            AuthenticationConfiguration configurationAuthentification) throws Exception {
        return configurationAuthentification.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider fournisseurAuthentification() {
        DaoAuthenticationProvider fournisseurAuth = new DaoAuthenticationProvider(serviceUtilisateurDetails);
        fournisseurAuth.setPasswordEncoder(encodeurMotDePasse);
        return fournisseurAuth;
    }

    @Bean
    public CorsConfigurationSource sourceConfigurationCors() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain chaineFiltres(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(sourceConfigurationCors()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/feuillesTemps/soumettre").hasAnyRole("MEMBRE_EQUIPE", "CHEF_PROJET")
                        .requestMatchers("/api/feuillesTemps/valider/**").hasAnyRole("CHEF_PROJET", "RESPONSABLE")
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(fournisseurAuthentification())
                .addFilterBefore(filtreAuthentificationJwt, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}