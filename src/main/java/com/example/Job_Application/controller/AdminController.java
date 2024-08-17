package com.example.Job_Application.controller;

import com.example.Job_Application.config.JwtService;
import com.example.Job_Application.entities.Admin;
import com.example.Job_Application.entities.Job;
import com.example.Job_Application.exception.AdminNotFoundException;
import com.example.Job_Application.exception.JobNotFoundException;
import com.example.Job_Application.exception.UserNotFoundException;
import com.example.Job_Application.payload.request.*;
import com.example.Job_Application.payload.response.*;
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
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin")
public class AdminController {
    private final JwtService jwtService;
    private final AdminService adminService;

    @PutMapping("/edit-admin/{id}")
    public ResponseEntity<AdminResponse> editAdmin(@PathVariable Long id, @RequestBody UpdateAdminRequest updateAdminRequest) {
        AdminResponse updatedAdmin = adminService.editAdmin(id, updateAdminRequest);
        return new ResponseEntity<>(updatedAdmin, HttpStatus.CREATED);
    }

    @PutMapping("/reset-password-admin/{id}")
    public ResponseEntity<String> resetPassword(@PathVariable Long id, @RequestParam String email, @RequestParam String oldPassword, @RequestParam String newPassword){
        return new ResponseEntity<>(adminService.resetPassword(id, email, oldPassword, newPassword), HttpStatus.OK);
    }


    @PostMapping("/refreshToken-admin/{id}")
    public ResponseEntity<?> refreshToken(@PathVariable Long id, @RequestHeader("Authorization") String refreshTokenHeader) {
        String username = jwtService.extractUsernameFromToken(refreshTokenHeader);
        Admin admin = adminService.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Admin not found with ID: " + id));

        if (!username.equals(admin.getEmail())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token does not belong to this admin");
        }
        UserDetails userDetails = adminService.loadUserByUsername(username);

        String newAccessToken = jwtService.generateToken(userDetails);

        return ResponseEntity.ok(newAccessToken);
    }

    @GetMapping("/view-admin/{id}")
    public ResponseEntity<AdminResponse> viewAdmin(@PathVariable Long id) {
        try {
            AdminResponse adminResponse = adminService.viewAdmin(id);
            return ResponseEntity.ok(adminResponse);
        } catch (AdminNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/all-admins")
    public ResponseEntity<List<AdminResponse>> viewAllAdmins() {
        List<AdminResponse> admins = adminService.viewAllAdmins();
        return ResponseEntity.ok(admins);
    }

    @PostMapping("/logout-admin/{id}")
    public String logoutAdmin(@PathVariable Long id) {
        return adminService.logoutAdmin(id);
    }

}

