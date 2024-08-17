package com.example.Job_Application.config;


import com.example.Job_Application.exception.UserNotFoundException;
import com.example.Job_Application.repository.AdminRepository;
import com.example.Job_Application.repository.UserRepository;
import com.example.Job_Application.utils.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {
    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            // First, try to find the user
            return userRepository.findByEmail(username)
                    .map(user -> new CustomUserDetails(user, "ROLE_USER"))
                    .orElseGet(() -> adminRepository.findByEmail(username)
                            .map(admin -> new CustomUserDetails(admin, "ROLE_ADMIN"))
                            .orElseThrow(() -> new UserNotFoundException("User or Admin not found")));
        };
    }
    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

}
