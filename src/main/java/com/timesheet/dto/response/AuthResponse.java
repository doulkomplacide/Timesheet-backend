package com.timesheet.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String token;
    private String email;
    private String firstName;
    private String lastName;
    private String fullName;
    private String role;
    private Long userId;
    private String tokenType = "Bearer";
    private Long expiresIn;
}