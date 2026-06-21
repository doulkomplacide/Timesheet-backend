package com.gestiontemps.dto.requete;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;

@Data
public class TimesheetEntryRequest {
    
    @NotNull(message = "L'ID de la tâche est requis")
    private Long taskId;
    
    @NotNull(message = "La date est requise")
    private LocalDate entryDate;
    
    @NotNull(message = "Les heures sont requises")
    @DecimalMin(value = "0.5", message = "Minimum 0.5 heures")
    @DecimalMax(value = "24", message = "Maximum 24 heures")
    private Double hoursWorked;
    
    @Size(max = 500, message = "La description ne peut pas dépasser 500 caractères")
    private String description;
}