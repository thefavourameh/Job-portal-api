package com.example.Job_Application.controller;

import com.example.Job_Application.entities.AppUser;
import com.example.Job_Application.exception.AdminNotFoundException;
import com.example.Job_Application.exception.UserNotFoundException;
import com.example.Job_Application.payload.request.UpdateUserRequest;
import com.example.Job_Application.payload.response.AdminResponse;
import com.example.Job_Application.payload.response.UserResponse;
import com.example.Job_Application.repository.UserRepository;
import com.example.Job_Application.service.FileUploadService;
import com.example.Job_Application.service.UserService;
import com.example.Job_Application.utils.AppConstant;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/user")
public class UserController {
    private final UserService userService;
    private final UserRepository userRepository;

    @PreAuthorize("hasRole('USER')")
    @PutMapping("/edit-user/{id}")
    public ResponseEntity<UserResponse> editUser(@PathVariable Long id, @RequestBody UpdateUserRequest updateUserRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();

        // Retrieve the AppUser by email
        Optional<AppUser> currentUserOptional = userRepository.findByEmail(currentUserEmail);

        if (currentUserOptional.isPresent()) {
            AppUser currentUser = currentUserOptional.get(); // Safely retrieve the AppUser object

            if (!currentUser.getId().equals(id)) {
                return ResponseEntity
                        .status(HttpStatus.FORBIDDEN)
                        .body(new UserResponse<>("You are not allowed to perform this action"));
            }

            UserResponse updatedUser = userService.editUser(id, updateUserRequest);
            return new ResponseEntity<>(updatedUser, HttpStatus.CREATED);
        } else {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new UserResponse<>("User not found"));
        }
    }

    @GetMapping("/view-user/{id}")
    public ResponseEntity<UserResponse> viewUser(@PathVariable Long id) {
        UserResponse appUser = userService.viewUser(id);
        return new ResponseEntity<>(appUser, HttpStatus.OK);
    }

    @PutMapping("/curriculum-vitae/{id}")
    public ResponseEntity<UserResponse<String>> curriculumVitaeUpload(@PathVariable Long id, @RequestParam MultipartFile curriculumVitae) {
        if (curriculumVitae.getSize() > AppConstant.MAX_FILE_SIZE) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new UserResponse<>("File size is too large"));
        }
        return userService.uploadCurriculumVitae(id, curriculumVitae);
    }

    @GetMapping("/all-users")
    public ResponseEntity<List<UserResponse>> viewAllUsers() {
        List<UserResponse> users = userService.viewAllUsers();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/reset-password/{id}")
    public ResponseEntity<String> resetPassword(@PathVariable Long id, @RequestParam String email, @RequestParam String oldPassword, @RequestParam String newPassword){
        return new ResponseEntity<>(userService.resetPassword(id, email, oldPassword, newPassword), HttpStatus.OK);
    }

}
