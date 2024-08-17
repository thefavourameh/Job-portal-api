package com.example.Job_Application.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateAdminRequest {
    @Size(min = 2, max = 125, message = "Firstname must be at least 2 characters long")
    @NotBlank(message = "Firstname must not be empty")
    private String firstName;

    @Size(min = 2, max = 125, message = "Lastname must be at least 2 characters long")
    @NotBlank(message = "Lastname must not be empty")
    private String lastName;

    @NotBlank(message = "Email must not be empty")
    @Email
    private String email;

    @Size(min = 2, max = 125, message = "Company name must be at least 2 characters long")
    @NotBlank(message = "Company Name must not be empty")
    private String companyName;

    @NotBlank(message = "Identity Number must not be empty")
    private String identityNumber;


}
