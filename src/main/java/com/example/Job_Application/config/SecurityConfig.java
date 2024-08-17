package com.example.Job_Application.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/api/v1/auth/**").permitAll() // Public endpoints
                                .requestMatchers("/api/v1/admin/edit-admin/**").hasRole("ADMIN") // Admin restricted
                                .requestMatchers("/api/v1/admin/logout-admin/**").hasRole("ADMIN") // Admin restricted
                                .requestMatchers("/api/v1/admin/view-admin/**").permitAll() // Admin restricted
                                .requestMatchers("/api/v1/admin/all-admins/**").permitAll() // Admin restricted
                                .requestMatchers("/api/v1/admin/refreshToken-admin/**").hasRole("ADMIN") // Admin restricted
                                .requestMatchers("/api/v1/admin/reset-password-admin/**").permitAll()
                                .requestMatchers("/api/v1/admin/reset-forgot-password-admin/**").permitAll()
                                .requestMatchers("/api/v1/user/edit-user/**").hasRole("USER")
                                .requestMatchers("/api/v1/user/view-user/**").permitAll()
                                .requestMatchers("/api/v1/user/reset-password/**").permitAll()
                                .requestMatchers("/api/v1/user/curriculum-vitae/**").hasRole("USER")
                                .requestMatchers("/api/v1/user/all-users/**").permitAll()
                                .requestMatchers("/api/v1/job/update/**").hasRole("ADMIN")
                                .requestMatchers("/api/v1/job/new-job/**").hasRole("ADMIN")
                                .requestMatchers("/api/v1/job/delete/**").hasRole("ADMIN")
                                .requestMatchers("/api/v1/job/view/**").permitAll()
                                .requestMatchers("/api/v1/job/all-jobs").permitAll()
                                .anyRequest().authenticated()
                )
                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}