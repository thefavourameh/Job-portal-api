package com.example.Job_Application.controller;

import com.example.Job_Application.entities.Job;
import com.example.Job_Application.exception.JobNotFoundException;
import com.example.Job_Application.payload.request.JobRequest;
import com.example.Job_Application.payload.response.JobResponse;
import com.example.Job_Application.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/job")
public class JobController {

    private final JobService jobService;


    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/new-job/{adminId}")
    public ResponseEntity<JobRequest> createJob(@PathVariable Long adminId, @RequestBody JobRequest createRequest) {
        return ResponseEntity.ok(jobService.createJob(adminId, createRequest));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("update/{jobId}")
    public ResponseEntity<String> updateJob(@PathVariable Long jobId, @RequestBody JobRequest updateRequest) {
        jobService.updateJob(jobId, updateRequest);
        return ResponseEntity.ok("Job updated successfully");
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteJob(@PathVariable Long id) {
        jobService.deleteJob(id);
        return ResponseEntity.ok("Job Deleted Successfully!");
    }

    @GetMapping("/view/{id}")
    public ResponseEntity<JobResponse> viewJob(@PathVariable Long id) {
        try {
            JobResponse jobResponse = jobService.viewJob(id);
            return ResponseEntity.ok(jobResponse);
        } catch (JobNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
    @GetMapping("/all-jobs")
    public ResponseEntity<List<JobResponse>> viewAllJobs() {
        List<JobResponse> jobs = jobService.viewAllJobs();
        return ResponseEntity.ok(jobs);
    }
}