package com.timesheet.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskRequest {
    
    @NotBlank(message = "Le code est requis")
    @Size(max = 50, message = "Le code ne peut pas dépasser 50 caractères")
    private String code;
    
    @NotBlank(message = "Le nom est requis")
    @Size(max = 100, message = "Le nom ne peut pas dépasser 100 caractères")
    private String name;
    
    @Size(max = 500, message = "La description ne peut pas dépasser 500 caractères")
    private String description;
    
    private String priority;
    private String status;
    
    @NotNull(message = "L'ID du projet est requis")
    private Long projectId;
    
    private Long assignedTo;
}