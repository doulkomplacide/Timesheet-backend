package com.gestiontemps.securite;

import com.gestiontemps.entite.Utilisateur;
import com.gestiontemps.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UtilisateurRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Utilisateur user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé avec l'email: " + email));

        if (!user.isActive()) {
            throw new UsernameNotFoundException("Compte désactivé");
        }

        return user;
    }

    @Transactional(readOnly = true)
    public Utilisateur loadUserEntityByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé avec l'email: " + email));
    }
}