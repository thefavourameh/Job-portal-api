package com.example.Job_Application.payload.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate deadline;

}
