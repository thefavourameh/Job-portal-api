package com.example.Job_Application.controller;


import com.example.Job_Application.config.JwtService;
import com.example.Job_Application.entities.Admin;
import com.example.Job_Application.entities.AppUser;
import com.example.Job_Application.exception.UsernameNotFoundException;
import com.example.Job_Application.payload.request.AuthenticationRequest;
import com.example.Job_Application.payload.request.RegisterAdminRequest;
import com.example.Job_Application.payload.request.RegisterRequest;
import com.example.Job_Application.payload.response.AuthenticationResponse;
import com.example.Job_Application.payload.response.RegisterResponse;
import com.example.Job_Application.service.impl.AdminServiceImpl;
import com.example.Job_Application.service.impl.UserServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final UserServiceImpl userService;

    private final AdminServiceImpl adminService;

    private final JwtService jwtService;

    @PostMapping("/register-user")
    public ResponseEntity<?> register(@Validated
            @RequestBody RegisterRequest registerRequest, BindingResult bindingResult) throws JsonProcessingException {

        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errors.put(error.getField(), error.getDefaultMessage());
            }
            return ResponseEntity.badRequest().body(errors);
        }

        RegisterResponse authenticationResponse = userService.register(registerRequest);
        return ResponseEntity.ok(authenticationResponse);
    }

    @PostMapping("/register-admin")
    public ResponseEntity<?> registerAdmin(@Validated
                                           @RequestBody RegisterAdminRequest registerAdminRequest, BindingResult bindingResult) throws JsonProcessingException {

        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errors.put(error.getField(), error.getDefaultMessage());
            }
            return ResponseEntity.badRequest().body(errors);
        }

        RegisterResponse authenticationResponse = adminService.register(registerAdminRequest);
        return ResponseEntity.ok(authenticationResponse);
    }

    @PostMapping("/login/{id}")
    public ResponseEntity<AuthenticationResponse> authenticate(@PathVariable Long id, @RequestBody AuthenticationRequest request){
            AppUser appUser = userService.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with ID: " + id));

        AuthenticationResponse response = userService.authenticate(appUser, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login-admin/{id}")
    public ResponseEntity<AuthenticationResponse> authenticateAdmin(@PathVariable Long id, @RequestBody AuthenticationRequest request) {
        Admin admin = adminService.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Admin not found with ID: " + id));

        AuthenticationResponse response = adminService.authenticateAdmin(admin, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout/{id}")
    public String logout(@PathVariable Long id) {
       return userService.logout(id);
    }



}
