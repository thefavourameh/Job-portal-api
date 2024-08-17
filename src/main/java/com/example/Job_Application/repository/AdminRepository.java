package com.example.Job_Application.repository;


import com.example.Job_Application.entities.Admin;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
    Optional<Admin> findByEmail(String email);

    Boolean existsByEmail(String email);

    @Modifying
    @Transactional
    @Query("UPDATE Admin admin SET admin.password = :newPassword WHERE admin.email = :email")
    void updateAdminPassword(@Param("email") String email, @Param("newPassword") String newPassword);
}




