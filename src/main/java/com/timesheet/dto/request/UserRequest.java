package com.timesheet.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UserRequest {
    
    @NotBlank(message = "L'email est requis")
    @Email(message = "Format d'email invalide")
    private String email;
    
    @NotBlank(message = "Le mot de passe est requis")
    @Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caractères")
    private String password;
    
    @NotBlank(message = "Le prénom est requis")
    @Size(max = 50, message = "Le prénom ne peut pas dépasser 50 caractères")
    private String firstName;
    
    @NotBlank(message = "Le nom est requis")
    @Size(max = 50, message = "Le nom ne peut pas dépasser 50 caractères")
    private String lastName;
    
    private String role;
    private Long departmentId;
    private Long serviceId;
}