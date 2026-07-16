package com.placement.portal.service;

import com.placement.portal.dto.JobPostRequest;
import com.placement.portal.model.CompanyProfile;
import com.placement.portal.model.JobPost;
import com.placement.portal.model.StudentProfile;
import com.placement.portal.model.User;
import com.placement.portal.repository.CompanyProfileRepository;
import com.placement.portal.repository.JobPostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class JobService {

    private final JobPostRepository jobPostRepository;
    private final CompanyProfileRepository companyProfileRepository;

    public JobService(JobPostRepository jobPostRepository, CompanyProfileRepository companyProfileRepository) {
        this.jobPostRepository = jobPostRepository;
        this.companyProfileRepository = companyProfileRepository;
    }

    public List<JobPost> getAllJobs() {
        return jobPostRepository.findAll();
    }

    public List<JobPost> getOpenJobs() {
        return jobPostRepository.findByStatus("OPEN");
    }

    public List<JobPost> getJobsByCompanyUser(User companyUser) {
        CompanyProfile company = companyProfileRepository.findByUser(companyUser)
                .orElseThrow(() -> new IllegalArgumentException("Company profile not found for user: " + companyUser.getEmail()));
        return jobPostRepository.findByCompanyId(company.getId());
    }

    public Optional<JobPost> getJobById(Long id) {
        return jobPostRepository.findById(id);
    }

    @Transactional
    public JobPost createJobPost(User companyUser, JobPostRequest request) {
        CompanyProfile company = companyProfileRepository.findByUser(companyUser)
                .orElseThrow(() -> new IllegalArgumentException("Company profile not found for user: " + companyUser.getEmail()));

        JobPost jobPost = new JobPost(
                company,
                request.getTitle(),
                request.getDescription(),
                request.getLocation(),
                request.getCtc(),
                request.getEligibleCgpa(),
                request.getEligibleBranches()
        );

        return jobPostRepository.save(jobPost);
    }

    @Transactional
    public JobPost updateJobStatus(Long jobId, String status) {
        JobPost job = jobPostRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Job post not found: " + jobId));
        
        if ("OPEN".equalsIgnoreCase(status) || "CLOSED".equalsIgnoreCase(status)) {
            job.setStatus(status.toUpperCase());
        } else {
            throw new IllegalArgumentException("Invalid status value. Use 'OPEN' or 'CLOSED'.");
        }
        
        return jobPostRepository.save(job);
    }

    public boolean isStudentEligible(StudentProfile student, JobPost job) {
        // Rule 1: Compare CGPA
        if (student.getCgpa() < job.getEligibleCgpa()) {
            return false;
        }

        // Rule 2: Compare Branch
        String eligible = job.getEligibleBranches().trim();
        if ("ALL".equalsIgnoreCase(eligible)) {
            return true;
        }

        String studentBranch = student.getBranch().trim();
        return Arrays.stream(eligible.split(","))
                .map(String::trim)
                .anyMatch(branch -> branch.equalsIgnoreCase(studentBranch));
    }
}
