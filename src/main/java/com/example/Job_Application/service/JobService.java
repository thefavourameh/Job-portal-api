package com.example.Job_Application.service;

import com.example.Job_Application.entities.AppUser;
import com.example.Job_Application.entities.Job;
import com.example.Job_Application.payload.request.JobRequest;
import com.example.Job_Application.payload.response.JobResponse;
import com.example.Job_Application.payload.response.UserResponse;

import java.util.List;

public interface JobService {
    public JobRequest createJob(Long adminId, JobRequest createRequest);
    public void deleteJob(Long id);
    public void updateJob(Long jobId, JobRequest updateRequest);
    JobResponse viewJob(Long id);
    public List<JobResponse> viewAllJobs();


}
