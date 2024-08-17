package com.example.Job_Application.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JobResponse {
    private Long id;
    private String title;
    private String description;
    private String responsibilities;
    private LocalDate deadline;

}
