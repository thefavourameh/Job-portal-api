package com.example.Job_Application.service.impl;



import com.example.Job_Application.entities.Job;
import com.example.Job_Application.exception.JobNotFoundException;
import com.example.Job_Application.payload.request.JobRequest;
import com.example.Job_Application.repository.AdminRepository;
import com.example.Job_Application.repository.JobRepository;
import com.example.Job_Application.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JobServiceImpl implements JobService {
    private final JobRepository jobRepository;

    private final AdminRepository adminRepository;

    @Override
    public void deleteJob(Long id) {
        Job job = jobRepository.findById(id).orElseThrow(
                () -> new JobNotFoundException("Job Not Found"));

        jobRepository.delete(job);


    }

    @Override
    public void updateJob(Long jobId, JobRequest updateRequest) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new JobNotFoundException("Task not found with id: " + jobId));

        if (updateRequest.getTitle() != null) job.setTitle(updateRequest.getTitle());
        if (updateRequest.getDescription() != null) job.setDescription(updateRequest.getDescription());
        if (updateRequest.getResponsibilities() != null) job.setResponsibilities(updateRequest.getResponsibilities());

        jobRepository.save(job);
    }
}

