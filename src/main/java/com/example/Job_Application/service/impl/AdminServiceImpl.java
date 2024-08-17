package com.example.Job_Application.service.impl;

import com.example.Job_Application.config.JwtAuthenticationFilter;
import com.example.Job_Application.config.JwtService;
import com.example.Job_Application.entities.Admin;
import com.example.Job_Application.entities.AppUser;
import com.example.Job_Application.exception.InvalidAccessException;
import com.example.Job_Application.exception.PasswordNotFoundException;
import com.example.Job_Application.exception.UserNotFoundException;
import com.example.Job_Application.payload.request.*;
import com.example.Job_Application.payload.response.*;
import com.example.Job_Application.repository.AdminRepository;
import com.example.Job_Application.service.AdminService;
import com.example.Job_Application.service.FileUploadService;
import com.example.Job_Application.utils.UserUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    private  final Logger logger = LoggerFactory.getLogger(AdminServiceImpl.class);


    @Override
    public RegisterResponse register(RegisterAdminRequest registerAdminRequest) throws JsonProcessingException {
        String emailRegex = "^(.+)@(.+)$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(registerAdminRequest.getEmail());
        if (!matcher.matches()) {
            return RegisterResponse.builder()
                    .responseCode(UserUtils.INVALID_EMAIL_FORMAT_CODE)
                    .responseMessage(UserUtils.INVALID_EMAIL_FORMAT_MESSAGE)
                    .email(registerAdminRequest.getEmail())
                    .build();
        }

        // Validate email domain
        String[] emailParts = registerAdminRequest.getEmail().split("\\.");
        if (emailParts.length < 2 || emailParts[emailParts.length - 1].length() < 2) {
            System.out.println("Invalid email domain. Email parts: " + Arrays.toString(emailParts));

            return RegisterResponse.builder()
                    .responseCode(UserUtils.INVALID_EMAIL_DOMAIN_CODE)
                    .responseMessage(UserUtils.INVALID_EMAIL_DOMAIN_MESSAGE)
                    .build();
        }

        if (adminRepository.existsByEmail(registerAdminRequest.getEmail())) {
            return RegisterResponse.builder()
                    .responseCode(UserUtils.ACCOUNT_EXISTS_CODE)
                    .responseMessage(UserUtils.ACCOUNT_EXISTS_MESSAGE)
                    .build();
        }

        String encodedPassword = passwordEncoder.encode(registerAdminRequest.getPassword());

        Admin newAdmin = Admin.builder()
                .firstName(registerAdminRequest.getFirstName())
                .lastName(registerAdminRequest.getLastName())
                .email(registerAdminRequest.getEmail())
                .companyName(registerAdminRequest.getCompanyName())
                .identityNumber(registerAdminRequest.getIdentityNumber())
                .password(encodedPassword)
                .isEnabled(true)
                .build();

        Admin savedAdmin = adminRepository.save(newAdmin);

        return RegisterResponse.builder()
                .responseCode(UserUtils.ACCOUNT_CREATION_SUCCESS_CODE)
                .responseMessage(UserUtils.ACCOUNT_CREATION_SUCCESS_MESSAGE)
                .email(savedAdmin.getEmail())
                .build();
    }

    @Override
    public AuthenticationResponse authenticateAdmin(Admin admin, AuthenticationRequest request) {
        logger.info("Starting authentication for admin with email: {}", request.getEmail());

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
            logger.info("Authentication successful for admin with email: {}", request.getEmail());
        } catch (AuthenticationException e) {
            logger.error("Authentication failed for admin with email: {}", request.getEmail(), e);
            throw e;
        }

        Admin admin1 = adminRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    logger.error("Admin not found with email: {}", request.getEmail());
                    return new UsernameNotFoundException("Admin not found with email: " + request.getEmail());
                });

        if (!admin1.isEnabled()) {
            logger.error("Admin account is disabled for email: {}", request.getEmail());
            throw new DisabledException("Admin is disabled");
        }

        String jwtToken = jwtService.generateToken(admin1);
        logger.info("Generated JWT token for admin with email: {}", request.getEmail());

        admin.setToken(jwtToken);

        return AuthenticationResponse.builder()
                .id(admin1.getId())
                .responseCode(UserUtils.LOGIN_SUCCESS_CODE)
                .responseMessage(UserUtils.LOGIN_SUCCESS_MESSAGE)
                .email(admin1.getEmail())
                .firstName(admin1.getFirstName())
                .accessToken(jwtToken)
                .build();
    }

    @Override
    public AdminResponse viewAdmin(Long id) {
        Admin viewAdmin = adminRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Admin not found"));

        return AdminResponse.builder()
                .responseMessage(UserUtils.SINGLE_ADMIN_DETAILS_MESSAGE)
                .id(viewAdmin.getId())
                .firstName(viewAdmin.getFirstName())
                .lastName(viewAdmin.getLastName())
                .email(viewAdmin.getEmail())
                .companyName(viewAdmin.getCompanyName())
                .identityNumber(viewAdmin.getIdentityNumber())
                .build();
    }

    @Override
    public List<AdminResponse> viewAllAdmins() {
        return adminRepository.findAll()
                .stream()
                .map(admin -> AdminResponse.builder()
                        .responseMessage(UserUtils.ADMIN_DETAILS_MESSAGE)
                        .id(admin.getId())
                        .firstName(admin.getFirstName())
                        .lastName(admin.getLastName())
                        .email(admin.getEmail())
                        .companyName(admin.getCompanyName())
                        .identityNumber(admin.getIdentityNumber())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public AdminResponse editAdmin(Long id, UpdateAdminRequest updateAdminRequest) {
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("No Admin associated with " + id));
        admin.setFirstName(updateAdminRequest.getFirstName());
        admin.setLastName(updateAdminRequest.getLastName());
        admin.setEmail(updateAdminRequest.getEmail());
        admin.setCompanyName(updateAdminRequest.getCompanyName());
        admin.setIdentityNumber(updateAdminRequest.getIdentityNumber());
        adminRepository.save(admin);
        return AdminResponse.builder()
                .responseMessage(UserUtils.ADMIN_UPDATE_MESSAGE)
                .firstName(admin.getFirstName())
                .lastName(admin.getLastName())
                .email(admin.getEmail())
                .companyName(admin.getCompanyName())
                .identityNumber(admin.getIdentityNumber())
                .build();
    }


    @Override
    public String resetPassword(Long id, String email, String oldPassword, String newPassword) {
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("No Admin with this ID: " + id));

        if (!admin.getEmail().equals(email)) {
            throw new UserNotFoundException("Email does not match the provided Admin ID.");
        }

        if (!passwordEncoder.matches(oldPassword, admin.getPassword())) {
            throw new PasswordNotFoundException("Old password does not match the current password!");
        } else {
            admin.setPassword(passwordEncoder.encode(newPassword));
            adminRepository.save(admin);
            return "Your Password has been reset successfully, login with the new password.";
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Admin> optionalAdmin = adminRepository.findByEmail(username);
        if (optionalAdmin.isEmpty()) {
            throw new UsernameNotFoundException("Admin not found with username: " + username);
        }
        return optionalAdmin.get();
    }

    public String logoutAdmin(Long id) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        if (authentication != null) {
            String email = authentication.getName();
            Optional<Admin> adminOptional = adminRepository.findById(id);

            if (adminOptional.isPresent()) {
                Admin admin = adminOptional.get();

                // Ensure the logged-in admin matches the provided ID
                if (!admin.getEmail().equals(email)) {
                    throw new InvalidAccessException("Invalid Admin ID for the logged-in user");
                }

                admin.setToken(null);
                adminRepository.save(admin);
                securityContext.setAuthentication(null);
                SecurityContextHolder.clearContext();
                return "Logout successful";
            } else {
                throw new InvalidAccessException("Admin not found");
            }
        }
        throw new InvalidAccessException("Invalid access");
    }

    @Override
    public Optional<Admin> findById(Long id) {
            return adminRepository.findById(id);
        }
}


