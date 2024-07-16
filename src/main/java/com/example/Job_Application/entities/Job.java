package com.example.Job_Application.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_Id")
    private AppUser user;


}
