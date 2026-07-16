package com.placement.portal.controller;

import com.placement.portal.model.CompanyProfile;
import com.placement.portal.model.JobPost;
import com.placement.portal.model.StudentProfile;
import com.placement.portal.repository.CompanyProfileRepository;
import com.placement.portal.repository.StudentProfileRepository;
import com.placement.portal.service.JobService;
import com.placement.portal.service.StatsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final StatsService statsService;
    private final StudentProfileRepository studentProfileRepository;
    private final CompanyProfileRepository companyProfileRepository;
    private final JobService jobService;

    public AdminController(StatsService statsService,
                           StudentProfileRepository studentProfileRepository,
                           CompanyProfileRepository companyProfileRepository,
                           JobService jobService) {
        this.statsService = statsService;
        this.studentProfileRepository = studentProfileRepository;
        this.companyProfileRepository = companyProfileRepository;
        this.jobService = jobService;
    }

    @GetMapping("/stats")
    public ResponseEntity<?> getStats() {
        return ResponseEntity.ok(statsService.getGlobalStats());
    }

    @GetMapping("/users")
    public ResponseEntity<?> getUsers() {
        List<StudentProfile> students = studentProfileRepository.findAll();
        List<CompanyProfile> companies = companyProfileRepository.findAll();

        Map<String, Object> usersMap = new HashMap<>();
        usersMap.put("students", students);
        usersMap.put("companies", companies);

        return ResponseEntity.ok(usersMap);
    }

    @GetMapping("/jobs")
    public ResponseEntity<?> getAllJobs() {
        List<JobPost> jobs = jobService.getAllJobs();
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
}
