package com.example.Job_Application.service;

import com.example.Job_Application.payload.request.JobRequest;
import com.example.Job_Application.payload.response.JobResponse;

import java.util.List;

public interface JobService {
    public void deleteJob(Long id);
    public void updateJob(Long jobId, JobRequest updateRequest);

}
