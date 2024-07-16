package com.example.Job_Application.exception;

public class JobNotFoundException extends RuntimeException{
    public JobNotFoundException(String message) {
        super(message);
    }
}
