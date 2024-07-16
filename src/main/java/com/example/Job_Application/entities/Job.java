package com.example.Job_Application.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "jobs_tbl")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Job extends BaseClass{

    private String title;

    private String description;

    private String Responsibilities;

    private LocalDateTime datePosted;

    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL)
    private List<AppUser> users;

    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL)
    private List<Admin> admin;


}
