package com.example.Job_Application.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthenticationRequest {
    @NotBlank(message = "Email must not be empty")
    private String email;
    @Size(min = 6, max = 16, message = "password must be at least 2 characters long")
    @NotBlank(message = "Password must not be empty")
    private String password;
}
