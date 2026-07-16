package com.placement.portal.controller;

import com.placement.portal.dto.JobPostRequest;
import com.placement.portal.model.*;
import com.placement.portal.service.ApplicationService;
import com.placement.portal.service.JobService;
import com.placement.portal.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/company")
public class CompanyController {

    private final UserService userService;
    private final JobService jobService;
    private final ApplicationService applicationService;

    public CompanyController(UserService userService, JobService jobService, ApplicationService applicationService) {
        this.userService = userService;
        this.jobService = jobService;
        this.applicationService = applicationService;
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(Principal principal) {
        User user = userService.findByEmail(principal.getName()).orElseThrow();
        CompanyProfile profile = userService.getCompanyProfile(user)
                .orElseThrow(() -> new IllegalStateException("Company profile not found."));
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(Principal principal, @RequestBody CompanyProfile updatedData) {
        try {
            User user = userService.findByEmail(principal.getName()).orElseThrow();
            CompanyProfile profile = userService.updateCompanyProfile(user, updatedData);
            return ResponseEntity.ok(profile);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PostMapping("/jobs")
    public ResponseEntity<?> createJob(Principal principal, @RequestBody JobPostRequest request) {
        try {
            User user = userService.findByEmail(principal.getName()).orElseThrow();
            JobPost job = jobService.createJobPost(user, request);
            return ResponseEntity.ok(job);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @GetMapping("/jobs")
    public ResponseEntity<?> getJobs(Principal principal) {
        User user = userService.findByEmail(principal.getName()).orElseThrow();
        List<JobPost> jobs = jobService.getJobsByCompanyUser(user);
        return ResponseEntity.ok(jobs);
    }

    @PutMapping("/jobs/{jobId}/status")
    public ResponseEntity<?> toggleJobStatus(@PathVariable Long jobId, @RequestParam String status) {
        try {
            JobPost job = jobService.updateJobStatus(jobId, status);
            return ResponseEntity.ok(job);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @GetMapping("/applications")
    public ResponseEntity<?> getApplications(Principal principal) {
        User user = userService.findByEmail(principal.getName()).orElseThrow();
        List<JobApplication> applications = applicationService.getApplicationsByCompany(user);
        return ResponseEntity.ok(applications);
    }

    @PutMapping("/applications/{applicationId}/status")
    public ResponseEntity<?> updateApplicationStatus(
            @PathVariable Long applicationId,
            @RequestParam ApplicationStatus status,
            @RequestParam(required = false) String feedback) {
        try {
            JobApplication application = applicationService.updateApplicationStatus(applicationId, status, feedback);
            return ResponseEntity.ok(application);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
}
