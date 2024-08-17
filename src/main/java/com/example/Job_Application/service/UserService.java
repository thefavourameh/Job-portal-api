package com.example.Job_Application.service;

import com.example.Job_Application.entities.Admin;
import com.example.Job_Application.entities.AppUser;
import com.example.Job_Application.payload.request.AuthenticationRequest;
import com.example.Job_Application.payload.request.RegisterRequest;
import com.example.Job_Application.payload.request.UpdateUserRequest;
import com.example.Job_Application.payload.response.AuthenticationResponse;
import com.example.Job_Application.payload.response.RegisterResponse;
import com.example.Job_Application.payload.response.UserResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public interface UserService {
    public RegisterResponse register(RegisterRequest registerRequest) throws JsonProcessingException;
    public AuthenticationResponse authenticate(AppUser appUser, AuthenticationRequest request);
    UserResponse editUser(Long id, UpdateUserRequest updateUserRequest);
    UserResponse viewUser(Long id);
    public List<UserResponse> viewAllUsers();
    public String resetPassword(Long id, String email, String oldPassword, String newPassword);
    public UserDetails loadUserByUsername(String username);
    ResponseEntity<UserResponse<String>> uploadCurriculumVitae(Long id, MultipartFile multipartFile);
    Optional<AppUser> findById(Long id);
    String logout(Long id);
}
