package com.placement.portal.controller;

import com.placement.portal.model.*;
import com.placement.portal.service.ApplicationService;
import com.placement.portal.service.JobService;
import com.placement.portal.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/student")
public class StudentController {

    private final UserService userService;
    private final JobService jobService;
    private final ApplicationService applicationService;

    public StudentController(UserService userService, JobService jobService, ApplicationService applicationService) {
        this.userService = userService;
        this.jobService = jobService;
        this.applicationService = applicationService;
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(Principal principal) {
        User user = userService.findByEmail(principal.getName()).orElseThrow();
        StudentProfile profile = userService.getStudentProfile(user)
                .orElseThrow(() -> new IllegalStateException("Student profile not found."));
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(Principal principal, @RequestBody StudentProfile updatedData) {
        try {
            User user = userService.findByEmail(principal.getName()).orElseThrow();
            StudentProfile profile = userService.updateStudentProfile(user, updatedData);
            return ResponseEntity.ok(profile);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @GetMapping("/jobs")
    public ResponseEntity<?> getEligibleJobs(Principal principal) {
        User user = userService.findByEmail(principal.getName()).orElseThrow();
        StudentProfile student = userService.getStudentProfile(user)
                .orElseThrow(() -> new IllegalStateException("Student profile not found."));

        List<JobPost> openJobs = jobService.getOpenJobs();
        List<JobApplication> studentApps = applicationService.getApplicationsByStudent(user);

        List<JobListingResponse> responseList = openJobs.stream().map(job -> {
            boolean eligible = jobService.isStudentEligible(student, job);
            boolean applied = studentApps.stream()
                    .anyMatch(app -> app.getJobPost().getId().equals(job.getId()));
            return new JobListingResponse(job, eligible, applied);
        }).collect(Collectors.toList());

        return ResponseEntity.ok(responseList);
    }

    @PostMapping("/apply")
    public ResponseEntity<?> applyToJob(Principal principal, @RequestParam Long jobId) {
        try {
            User user = userService.findByEmail(principal.getName()).orElseThrow();
            JobApplication application = applicationService.applyToJob(user, jobId);
            return ResponseEntity.ok(application);
        } catch (IllegalStateException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(500).body("Error applying to job: " + ex.getMessage());
        }
    }

    @GetMapping("/applications")
    public ResponseEntity<?> getApplications(Principal principal) {
        User user = userService.findByEmail(principal.getName()).orElseThrow();
        List<JobApplication> applications = applicationService.getApplicationsByStudent(user);
        return ResponseEntity.ok(applications);
    }

    // Response DTO mapping for student job feed
    public static class JobListingResponse {
        private Long id;
        private String title;
        private String companyName;
        private String location;
        private Double ctc;
        private Double eligibleCgpa;
        private String eligibleBranches;
        private String status;
        private boolean eligible;
        private boolean applied;

        public JobListingResponse(JobPost job, boolean eligible, boolean applied) {
            this.id = job.getId();
            this.title = job.getTitle();
            this.companyName = job.getCompany().getCompanyName();
            this.location = job.getLocation();
            this.ctc = job.getCtc();
            this.eligibleCgpa = job.getEligibleCgpa();
            this.eligibleBranches = job.getEligibleBranches();
            this.status = job.getStatus();
            this.eligible = eligible;
            this.applied = applied;
        }

        // Getters
        public Long getId() { return id; }
        public String getTitle() { return title; }
        public String getCompanyName() { return companyName; }
        public String getLocation() { return location; }
        public Double getCtc() { return ctc; }
        public Double getEligibleCgpa() { return eligibleCgpa; }
        public String getEligibleBranches() { return eligibleBranches; }
        public String getStatus() { return status; }
        public boolean isEligible() { return eligible; }
        public boolean isApplied() { return applied; }
    }
}
