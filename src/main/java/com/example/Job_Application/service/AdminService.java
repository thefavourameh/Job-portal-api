package com.example.Job_Application.service;

import com.example.Job_Application.payload.request.AuthenticationRequest;
import com.example.Job_Application.payload.request.RegisterRequest;
import com.example.Job_Application.payload.request.UpdateUserRequest;
import com.example.Job_Application.payload.response.AuthenticationResponse;
import com.example.Job_Application.payload.response.RegisterResponse;
import com.example.Job_Application.payload.response.UserResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.security.core.userdetails.UserDetails;

public interface AdminService {
    public RegisterResponse register(RegisterRequest registerRequest) throws JsonProcessingException;
    public AuthenticationResponse authenticate(AuthenticationRequest request);
    UserResponse editUser(Long id, UpdateUserRequest updateUserRequest);

    UserResponse viewUser(Long id);
    public String resetPassword(String email, String oldPassword, String newPassword);
    String forgotPassword(String email, String newPassword, String confirmPassword);
    public UserDetails loadUserByUsername(String username);
}
