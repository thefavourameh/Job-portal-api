package com.example.Job_Application.service.impl;

import com.example.Job_Application.config.JwtAuthenticationFilter;
import com.example.Job_Application.config.JwtService;
import com.example.Job_Application.entities.Admin;
import com.example.Job_Application.entities.AppUser;
import com.example.Job_Application.exception.InvalidAccessException;
import com.example.Job_Application.exception.PasswordNotFoundException;
import com.example.Job_Application.exception.UserNotFoundException;
import com.example.Job_Application.payload.request.AuthenticationRequest;
import com.example.Job_Application.payload.request.RegisterRequest;
import com.example.Job_Application.payload.request.UpdateUserRequest;
import com.example.Job_Application.payload.response.AdminResponse;
import com.example.Job_Application.payload.response.AuthenticationResponse;
import com.example.Job_Application.payload.response.RegisterResponse;
import com.example.Job_Application.payload.response.UserResponse;
import com.example.Job_Application.repository.UserRepository;
import com.example.Job_Application.service.FileUploadService;
import com.example.Job_Application.service.UserService;
import com.example.Job_Application.utils.UserUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
//@Slf4j
public class UserServiceImpl implements UserService {


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final HttpServletRequest httpServletRequest;
    private final FileUploadService fileUploadService;


    private final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Override
    public RegisterResponse register(@Valid RegisterRequest registerRequest) throws JsonProcessingException {

        // Validate email format
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

        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            return RegisterResponse.builder()
                    .responseCode(UserUtils.ACCOUNT_EXISTS_CODE)
                    .responseMessage(UserUtils.ACCOUNT_EXISTS_MESSAGE)
                    .build();
        }

        String encodedPassword = passwordEncoder.encode(registerRequest.getPassword());

        AppUser newUser = AppUser.builder()
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .email(registerRequest.getEmail())
                .password(encodedPassword)
                .dateOfBirth(registerRequest.getDateOfBirth())
                .curriculumVitae(registerRequest.getCurriculumVitae())
                .isEnabled(true)
                .build();

        AppUser savedUser = userRepository.save(newUser);

        return RegisterResponse.builder()
                .responseCode(UserUtils.ACCOUNT_CREATION_SUCCESS_CODE)
                .responseMessage(UserUtils.ACCOUNT_CREATION_SUCCESS_MESSAGE)
                .email(savedUser.getEmail())
                .build();
    }

    public AuthenticationResponse authenticate(AppUser appUser, AuthenticationRequest request) {
        logger.info("Starting authentication for user with email: {}", request.getEmail());

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
            logger.info("Authentication successful for user with email: {}", request.getEmail());
        } catch (AuthenticationException e) {
            logger.error("Authentication failed for user with email: {}", request.getEmail(), e);
            throw e;
        }

        AppUser user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    logger.error("User not found with email: {}", request.getEmail());
                    return new UsernameNotFoundException("User not found with email: " + request.getEmail());
                });

        if (!user.isEnabled()) {
            logger.error("User account is disabled for email: {}", request.getEmail());
            throw new DisabledException("User is disabled");
        }

        String jwtToken = jwtService.generateToken(user);
        logger.info("Generated JWT token for user with email: {}", request.getEmail());

        user.setToken(jwtToken);

        return AuthenticationResponse.builder()
                .id(user.getId())
                .responseCode(UserUtils.LOGIN_SUCCESS_CODE)
                .responseMessage(UserUtils.LOGIN_SUCCESS_MESSAGE)
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .accessToken(jwtToken)
                .build();
    }


    @Override
    public UserResponse editUser(Long id, UpdateUserRequest updateUserRequest) {
        // Get the currently authenticated user's email
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new InvalidAccessException("You must be logged in to edit user information.");
        }

        // Validate JWT token if applicable
        String token = jwtAuthenticationFilter.getTokenFromRequest(httpServletRequest);
        if (token != null && !jwtService.validateToken(token)) {
            throw new InvalidAccessException("Invalid or expired token. Please log in again.");
        }

        String currentUserEmail = authentication.getName();

        // Find the user associated with the provided ID
        AppUser appUser = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("No User associated with " + id));

        // Ensure the logged-in user is editing their own information
        if (!appUser.getEmail().equals(currentUserEmail)) {
            throw new InvalidAccessException("You are not allowed to edit this user's information.");
        }

        // Update user details
        appUser.setFirstName(updateUserRequest.getFirstName());
        appUser.setLastName(updateUserRequest.getLastName());
        appUser.setDateOfBirth(updateUserRequest.getDateOfBirth());

        // Save updated user information
        userRepository.save(appUser);

        // Build and return the response
        return UserResponse.builder()
                .responseMessage(UserUtils.USER_UPDATE_MESSAGE)
                .firstName(appUser.getFirstName())
                .lastName(appUser.getLastName())
                .dateOfBirth(appUser.getDateOfBirth())
                .curriculumVitae(appUser.getCurriculumVitae())
                .build();
    }

    @Override
    public UserResponse viewUser(Long id) {
        AppUser viewUser = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return UserResponse.builder()
                .responseMessage(UserUtils.USER_DETAILS_MESSAGE)
                .id(viewUser.getId())
                .firstName(viewUser.getFirstName())
                .lastName(viewUser.getLastName())
                .dateOfBirth(viewUser.getDateOfBirth())
                .curriculumVitae(viewUser.getCurriculumVitae())
                .email(viewUser.getEmail())
                .build();
    }

    @Override
    public List<UserResponse> viewAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(user -> UserResponse.builder()
                        .responseMessage(UserUtils.ALL_USER_DETAILS_MESSAGE)
                        .id(user.getId())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .email(user.getEmail())
                        .dateOfBirth(user.getDateOfBirth())
                        .curriculumVitae(user.getCurriculumVitae())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public String resetPassword(Long id, String email, String oldPassword, String newPassword) {
        AppUser user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("No User with this ID: " + id));

        if (!user.getEmail().equals(email)) {
            throw new UserNotFoundException("Email does not match the provided Admin ID.");
        }

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new PasswordNotFoundException("Old password does not match the current password!");
        } else {
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            return "Your Password has been reset successfully, login with the new password.";
        }
    }


    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<AppUser> optionalUser = userRepository.findByEmail(username);
        if (optionalUser.isEmpty()) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        AppUser user = optionalUser.get();

        return User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .accountExpired(!user.isAccountNonExpired())
                .accountLocked(!user.isAccountNonLocked())
                .credentialsExpired(!user.isCredentialsNonExpired())
                .disabled(!user.isEnabled())
                .build();
    }

    @Override
    public ResponseEntity<UserResponse<String>> uploadCurriculumVitae(Long id, MultipartFile curriculumVitae) {
        Optional<AppUser> appUserOptional = userRepository.findById(id);

        String fileUrl = null;

        try {
            if (appUserOptional.isPresent()) {
                fileUrl = fileUploadService.uploadFile(curriculumVitae);
                AppUser appUser = appUserOptional.get();
                appUser.setCurriculumVitae(fileUrl);
                userRepository.save(appUser);
            } else {
                return ResponseEntity.notFound().build();
            }

        } catch (IOException e) {
            throw new RuntimeException("File upload failed: " + e.getMessage(), e);
        }
        return ResponseEntity.ok(
                new UserResponse<>(
                        "Upload successfully",
                        fileUrl
                )
        );
    }

    @Override
    public Optional<AppUser> findById(Long id) {
        return userRepository.findById(id);
    }

    public String logout(Long id) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();

        if (authentication != null) {
            String email = authentication.getName();
            Optional<AppUser> appUser = userRepository.findByEmail(email);
            if (appUser.isPresent()) {
                AppUser existingUser = appUser.get();
                existingUser.setToken(null); // Clear any stored JWT token
                userRepository.save(existingUser);

                // Clear the security context and invalidate session
                securityContext.setAuthentication(null);
                SecurityContextHolder.clearContext();
                HttpSession session = httpServletRequest.getSession(false);
                if (session != null) {
                    session.invalidate();
                }

                return "Logout successful";
            } else {
                throw new InvalidAccessException("Invalid User");
            }
        }

        throw new InvalidAccessException("Invalid access");
    }
}

