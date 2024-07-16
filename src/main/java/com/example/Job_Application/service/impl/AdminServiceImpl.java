package com.example.Job_Application.service.impl;

import com.example.Job_Application.config.JwtAuthenticationFilter;
import com.example.Job_Application.config.JwtService;
import com.example.Job_Application.entities.Admin;
import com.example.Job_Application.entities.AppUser;
import com.example.Job_Application.payload.request.AuthenticationRequest;
import com.example.Job_Application.payload.request.RegisterRequest;
import com.example.Job_Application.payload.request.UpdateUserRequest;
import com.example.Job_Application.payload.response.AuthenticationResponse;
import com.example.Job_Application.payload.response.RegisterResponse;
import com.example.Job_Application.payload.response.UserResponse;
import com.example.Job_Application.repository.AdminRepository;
import com.example.Job_Application.service.AdminService;
import com.example.Job_Application.service.FileUploadService;
import com.example.Job_Application.utils.UserUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final HttpServletRequest httpServletRequest;

    @Override
    public RegisterResponse register(RegisterRequest registerRequest) throws JsonProcessingException {
        String emailRegex = "^(.+)@(.+)$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(registerRequest.getEmail());
        if (!matcher.matches()) {
            return RegisterResponse.builder()
                    .responseCode(UserUtils.INVALID_EMAIL_FORMAT_CODE)
                    .responseMessage(UserUtils.INVALID_EMAIL_FORMAT_MESSAGE)
                    .email(registerRequest.getEmail())
                    .build();
        }

        // Validate email domain
        String[] emailParts = registerRequest.getEmail().split("\\.");
        if (emailParts.length < 2 || emailParts[emailParts.length - 1].length() < 2) {
            System.out.println("Invalid email domain. Email parts: " + Arrays.toString(emailParts));

            return RegisterResponse.builder()
                    .responseCode(UserUtils.INVALID_EMAIL_DOMAIN_CODE)
                    .responseMessage(UserUtils.INVALID_EMAIL_DOMAIN_MESSAGE)
                    .build();
        }

        if (adminRepository.existsByEmail(registerRequest.getEmail())) {
            return RegisterResponse.builder()
                    .responseCode(UserUtils.ACCOUNT_EXISTS_CODE)
                    .responseMessage(UserUtils.ACCOUNT_EXISTS_MESSAGE)
                    .build();
        }

        String encodedPassword = passwordEncoder.encode(registerRequest.getPassword());

        Admin newAdmin = Admin.builder()
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .email(registerRequest.getEmail())
                .password(encodedPassword)
                .isEnabled(true)
                .build();

        Admin savedAdmin = adminRepository.save(newAdmin);

        return RegisterResponse.builder()
                .responseCode(UserUtils.ACCOUNT_CREATION_SUCCESS_CODE)
                .responseMessage(UserUtils.ACCOUNT_CREATION_SUCCESS_MESSAGE)
                .build();
    }

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        Admin admin = adminRepository.findByEmail(request.getEmail()).orElseThrow(() -> new UsernameNotFoundException("Admin not found with email: " + request.getEmail()));

        if (!admin.isEnabled()) {
            throw new DisabledException("Admin is disabled");
        }

        String jwtToken = jwtService.generateToken(admin);
        admin.setToken(jwtToken);

        return AuthenticationResponse.builder()
                .id(admin.getId())
                .responseCode(UserUtils.LOGIN_SUCCESS_CODE)
                .responseMessage(UserUtils.LOGIN_SUCCESS_MESSAGE)
                .email(admin.getEmail())
                .firstName(admin.getFirstName())
                .accessToken(jwtToken)
                .build();
    }

    @Override
    public UserResponse editUser(Long id, UpdateUserRequest updateUserRequest) {
        return null;
    }

    @Override
    public UserResponse viewUser(Long id) {
        return null;
    }

    @Override
    public String resetPassword(String email, String oldPassword, String newPassword) {
        return null;
    }

    @Override
    public String forgotPassword(String email, String newPassword, String confirmPassword) {
        return null;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        return null;
    }

}
