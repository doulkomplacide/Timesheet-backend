package com.gestiontemps.dto.reponse;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserResponse {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String fullName;
    private String role;
    private String departmentName;
    private String serviceName;
    private boolean active;
    private LocalDateTime createdAt;
}