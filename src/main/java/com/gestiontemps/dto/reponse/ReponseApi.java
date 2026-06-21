package com.gestiontemps.dto.reponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReponseApi<T> {
    private boolean succes;
    private String message;
    private T donnees;
    private String horodatage;

    public ReponseApi(boolean succes, String message, T donnees) {
        this.succes = succes;
        this.message = message;
        this.donnees = donnees;
        this.horodatage = LocalDateTime.now().toString();
    }

    public static <T> ReponseApi<T> succes(T donnees) {
        return new ReponseApi<>(true, "Opération réussie", donnees);
    }

    public static <T> ReponseApi<T> succes(String message, T donnees) {
        return new ReponseApi<>(true, message, donnees);
    }

    public static <T> ReponseApi<T> erreur(String message) {
        return new ReponseApi<>(false, message, null);
    }

    public static <T> ReponseApi<T> erreur(String message, T donnees) {
        return new ReponseApi<>(false, message, donnees);
    }
}