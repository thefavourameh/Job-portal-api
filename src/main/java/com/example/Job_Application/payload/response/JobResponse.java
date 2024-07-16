package com.example.Job_Application.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobResponse {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime deadline;

}
