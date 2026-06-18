package com.timesheet.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;

@Data
public class ProjectRequest {
    
    @NotBlank(message = "Le code est requis")
    @Size(max = 50, message = "Le code ne peut pas dépasser 50 caractères")
    private String code;
    
    @NotBlank(message = "Le nom est requis")
    @Size(max = 100, message = "Le nom ne peut pas dépasser 100 caractères")
    private String name;
    
    @Size(max = 500, message = "La description ne peut pas dépasser 500 caractères")
    private String description;
    
    private String status;
    
    @NotNull(message = "L'ID du service est requis")
    private Long serviceId;
    
    private LocalDate startDate;
    private LocalDate endDate;
}