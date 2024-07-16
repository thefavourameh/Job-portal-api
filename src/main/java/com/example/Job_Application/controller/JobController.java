package com.example.Job_Application.controller;

import com.example.Job_Application.payload.request.JobRequest;
import com.example.Job_Application.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/job")
public class JobController {

    private final JobService jobService;

    @PatchMapping("update/{jobId}")
    public ResponseEntity<String> updateJob(@PathVariable Long jobId, @RequestBody JobRequest updateRequest) {
        jobService.updateJob(jobId, updateRequest);
        return ResponseEntity.ok("Job updated successfully");
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteJob(@PathVariable Long id){
        jobService.deleteJob(id);

        return new ResponseEntity<>("Job Deleted Successfully!", HttpStatus.NO_CONTENT);
    }

    }