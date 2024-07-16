package com.example.Job_Application.controller;

import com.example.Job_Application.payload.request.UpdateUserRequest;
import com.example.Job_Application.payload.response.UserResponse;
import com.example.Job_Application.service.FileUploadService;
import com.example.Job_Application.service.UserService;
import com.example.Job_Application.utils.AppConstant;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/user")
public class UserController {
    private final UserService userService;
    private final FileUploadService fileUploadService;

    @PutMapping("/{id}/edit-user")
    public ResponseEntity<UserResponse> editUser(@PathVariable Long id, @RequestBody UpdateUserRequest updateUserRequest) {
        UserResponse updatedUser = userService.editUser(id, updateUserRequest);
        return new ResponseEntity<>(updatedUser, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> viewUser(@PathVariable Long id) {
        UserResponse appUser = userService.viewUser(id);
        return new ResponseEntity<>(appUser, HttpStatus.OK);
    }

    @PutMapping("/curriculum-vitae")
    public ResponseEntity<UserResponse<String>> curriculumVitaeUpload(@RequestParam MultipartFile curriculumVitae) {
        if (curriculumVitae.getSize() > AppConstant.MAX_FILE_SIZE) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new UserResponse<>("File size is too large"));
        }

        return userService.uploadCurriculumVitae(curriculumVitae);

    }
}
