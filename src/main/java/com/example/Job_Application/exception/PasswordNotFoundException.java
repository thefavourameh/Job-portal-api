package com.example.Job_Application.exception;

public class PasswordNotFoundException extends RuntimeException {
    public PasswordNotFoundException(String message) {
        super(message);
    }
}
