package com.example.Job_Application.controller;

import com.example.Job_Application.config.JwtService;
import com.example.Job_Application.payload.request.AuthenticationRequest;
import com.example.Job_Application.payload.request.RegisterRequest;
import com.example.Job_Application.payload.response.AuthenticationResponse;
import com.example.Job_Application.payload.response.RegisterResponse;
import com.example.Job_Application.repository.AdminRepository;
import com.example.Job_Application.service.AdminService;
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
@RequestMapping("/api/v1/admin")
public class AdminController {
    private final JwtService jwtService;
    private final AdminService adminService;
    private final AdminRepository adminRepository;

    @PostMapping("/register-admin")
    public ResponseEntity<?> register(@Validated
                                      @RequestBody RegisterRequest registerRequest, BindingResult bindingResult) throws JsonProcessingException {

        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errors.put(error.getField(), error.getDefaultMessage());
            }
            return ResponseEntity.badRequest().body(errors);
        }

        RegisterResponse authenticationResponse = adminService.register(registerRequest);
        return ResponseEntity.ok(authenticationResponse);
    }

    @PostMapping("/login-admin")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request){
        return ResponseEntity.ok(adminService.authenticate(request));
    }


    @PutMapping("/reset-password-admin")
    public ResponseEntity<String> resetPassword(@RequestParam String email, String oldPassword, @RequestHeader String newPassword){
        return new ResponseEntity<>(adminService.resetPassword(email,oldPassword, newPassword), HttpStatus.OK);
    }


    @PostMapping("/reset-forgot-password-admin")
    public String resetForgotPassword(@RequestParam("newPassword") String newPassword,
                                      @RequestParam("confirmPassword") String confirmPassword,
                                      @RequestParam("email") String email) {
        return adminService.forgotPassword(email,newPassword, confirmPassword);
    }

    @PostMapping("/refreshToken-admin")
    public ResponseEntity<?> refreshToken(@RequestHeader("Authorization") String refreshTokenHeader) {

        String username = jwtService.extractUsernameFromToken(refreshTokenHeader);

        UserDetails userDetails = adminService.loadUserByUsername(username);

        String newAccessToken = jwtService.generateToken(userDetails);

        return ResponseEntity.ok(newAccessToken);
    }



}

