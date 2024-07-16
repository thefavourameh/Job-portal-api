package com.example.Job_Application.repository;

import com.example.Job_Application.entities.AppUser;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByEmail(String email);
    Boolean existsByEmail(String email);

    @Modifying
    @Transactional
    @Query("UPDATE AppUser user SET user.password = :newPassword WHERE user.email = :email")
    void updateUserPassword(@Param("email") String email, @Param("newPassword") String newPassword);
}

