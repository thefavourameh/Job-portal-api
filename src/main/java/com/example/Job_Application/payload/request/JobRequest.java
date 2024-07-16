package com.example.Job_Application.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobRequest {
    @NotBlank(message = "Job Title must not be empty")
    private String title;
    @NotBlank(message = "Description must not be empty")
    private String description;
    @NotBlank(message = "Responsibilities must not be empty")
    private String Responsibilities;

}
