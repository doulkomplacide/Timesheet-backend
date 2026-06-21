package com.gestiontemps.dto.response;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ReponseProjet {
    private Long id;
    private String code;
    private String name;
    private String description;
    private String status;
    privatpackage com.gestiontemps.dto.reponse;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

    @Data
    public class ReponseProjet {
        private Long id;
        private String code;
        private String nom;
        private String description;
        private String statut;
        private String nomService;
        private LocalDate dateDebut;
        private LocalDate dateFin;
        private Integer nombreTaches;
        private LocalDateTime dateCreation;
    }e String serviceName;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer taskCount;
    private LocalDateTime createdAt;
}