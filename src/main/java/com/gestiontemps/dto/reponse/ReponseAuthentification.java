package com.gestiontemps.dto.reponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReponseAuthentification {

    private String token;
    private String email;
    private String prenom;
    private String nom;
    private String nomComplet;
    private String role;
    private Long utilisateurId;
    private String typeToken = "Bearer";
    private Long expireDans;
}