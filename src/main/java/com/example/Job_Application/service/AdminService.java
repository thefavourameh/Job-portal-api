package com.example.Job_Application.service;

import com.example.Job_Application.entities.Admin;
import com.example.Job_Application.entities.AppUser;
import com.example.Job_Application.payload.request.AuthenticationRequest;
import com.example.Job_Application.payload.request.RegisterAdminRequest;
import com.example.Job_Application.payload.request.UpdateAdminRequest;
import com.example.Job_Application.payload.request.UpdateUserRequest;
import com.example.Job_Application.payload.response.AdminResponse;
import com.example.Job_Application.payload.response.AuthenticationResponse;
import com.example.Job_Application.payload.response.RegisterResponse;
import com.example.Job_Application.payload.response.UserResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Optional;

public interface AdminService {
    public RegisterResponse register(RegisterAdminRequest registerAdminRequest) throws JsonProcessingException;
    public AuthenticationResponse authenticateAdmin(Admin admin, AuthenticationRequest request);
    AdminResponse viewAdmin(Long id);
    public List<AdminResponse> viewAllAdmins();
    AdminResponse editAdmin(Long id, UpdateAdminRequest updateAdminRequest);
    public String resetPassword(Long id, String email, String oldPassword, String newPassword);
    public UserDetails loadUserByUsername(String username);
    String logoutAdmin(Long id);

    Optional<Admin> findById(Long id);
}
