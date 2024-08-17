package com.example.Job_Application.service.impl;



import com.example.Job_Application.entities.Admin;
import com.example.Job_Application.entities.AppUser;
import com.example.Job_Application.entities.Job;
import com.example.Job_Application.exception.JobNotFoundException;
import com.example.Job_Application.payload.request.JobRequest;
import com.example.Job_Application.payload.response.JobResponse;
import com.example.Job_Application.payload.response.UserResponse;
import com.example.Job_Application.repository.AdminRepository;
import com.example.Job_Application.repository.JobRepository;
import com.example.Job_Application.service.JobService;
import com.example.Job_Application.utils.UserUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class JobServiceImpl implements JobService {
    private final JobRepository jobRepository;
    private final AdminRepository adminRepository;


    @Override
    public JobRequest createJob(Long adminId, JobRequest createRequest) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new UsernameNotFoundException("Admin with ID " + adminId + " not found"));

        Job job = new Job();
        job.setTitle(createRequest.getTitle());
        job.setDescription(createRequest.getDescription() != null ? createRequest.getDescription() : "");
        job.setResponsibilities(createRequest.getResponsibilities() != null ? createRequest.getResponsibilities() : null);
        job.setDeadline(createRequest.getDeadline() != null ? createRequest.getDeadline() : null);
        job.setAdmin(admin);

        jobRepository.save(job);

        return createRequest;
    }

    @Override
    public void deleteJob(Long id) {
        Job job = jobRepository.findById(id).orElseThrow(
                () -> new JobNotFoundException("Job Not Found"));

        jobRepository.delete(job);
    }

    @Override
    public void updateJob(Long id, JobRequest updateRequest) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new JobNotFoundException("Job not found with id: " + id));

        if (updateRequest.getTitle() != null) job.setTitle(updateRequest.getTitle());
        if (updateRequest.getDescription() != null) job.setDescription(updateRequest.getDescription());
        if (updateRequest.getResponsibilities() != null) job.setResponsibilities(updateRequest.getResponsibilities());

        jobRepository.save(job);
    }

    @Override
    public JobResponse viewJob(Long id) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Job not found"));

        return JobResponse.builder()
                .id(job.getId())
                .title(job.getTitle())
                .description(job.getDescription())
                .responsibilities(job.getResponsibilities())
                .deadline(job.getDeadline())
                .build();
    }

    @Override
    public List<JobResponse> viewAllJobs() {
        return jobRepository.findAll()
                .stream()
                .map(job -> JobResponse.builder()
                        .id(job.getId())
                        .title(job.getTitle())
                        .description(job.getDescription())
                        .responsibilities(job.getResponsibilities())
                        .deadline(job.getDeadline())
                        .build())
                .collect(Collectors.toList());
    }


}

