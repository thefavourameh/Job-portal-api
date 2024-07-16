package com.example.Job_Application.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ErrorDetails {

    private String message;
    private String debugMessage;
    private HttpStatus status;
    private LocalDateTime dateTime;
}
