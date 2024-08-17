package com.example.Job_Application.utils;

import com.example.Job_Application.entities.Admin;
import com.example.Job_Application.entities.AppUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class CustomUserDetails implements UserDetails {
    private final Object principal; // Can be either User or Admin
    private final String role;

    public CustomUserDetails(Object principal, String role) {
        this.principal = principal;
        this.role = role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(role));
    }

    @Override
    public String getPassword() {
        if (principal instanceof AppUser<?>) {
            return ((AppUser) principal).getPassword();
        } else if (principal instanceof Admin) {
            return ((Admin) principal).getPassword();
        }
        return null;
    }

    @Override
    public String getUsername() {
        if (principal instanceof AppUser<?>) {
            return ((AppUser) principal).getEmail();
        } else if (principal instanceof Admin) {
            return ((Admin) principal).getEmail();
        }
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}