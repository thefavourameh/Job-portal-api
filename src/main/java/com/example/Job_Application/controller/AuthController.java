package com.example.Job_Application.controller;


import com.example.Job_Application.config.JwtService;
import com.example.Job_Application.payload.request.AuthenticationRequest;
import com.example.Job_Application.payload.request.RegisterRequest;
import com.example.Job_Application.payload.response.AuthenticationResponse;
import com.example.Job_Application.payload.response.RegisterResponse;
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

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request){
        return ResponseEntity.ok(userService.authenticate(request));
    }


    @PutMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String email,  String oldPassword,@RequestHeader String newPassword){
        return new ResponseEntity<>(userService.resetPassword(email,oldPassword, newPassword), HttpStatus.OK);
    }


    @PostMapping("/reset-forgot-password")
    public String resetForgotPassword(@RequestParam("newPassword") String newPassword,
                                @RequestParam("confirmPassword") String confirmPassword,
                                @RequestParam("email") String email) {
        return userService.forgotPassword(email,newPassword, confirmPassword);
    }

    @PostMapping("/refreshToken")
    public ResponseEntity<?> refreshToken(@RequestHeader("Authorization") String refreshTokenHeader) {

        String username = jwtService.extractUsernameFromToken(refreshTokenHeader);

        UserDetails userDetails = userService.loadUserByUsername(username);

        String newAccessToken = jwtService.generateToken(userDetails);

        return ResponseEntity.ok(newAccessToken);
    }
    @PostMapping("/logout")
    public String logout() {
       return userService.logout();
    }



}
