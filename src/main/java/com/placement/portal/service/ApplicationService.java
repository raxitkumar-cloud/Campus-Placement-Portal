package com.placement.portal.service;

import com.placement.portal.model.*;
import com.placement.portal.repository.JobApplicationRepository;
import com.placement.portal.repository.StudentProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ApplicationService {

    private final JobApplicationRepository jobApplicationRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final JobService jobService;

    public ApplicationService(JobApplicationRepository jobApplicationRepository,
                              StudentProfileRepository studentProfileRepository,
                              JobService jobService) {
        this.jobApplicationRepository = jobApplicationRepository;
        this.studentProfileRepository = studentProfileRepository;
        this.jobService = jobService;
    }

    public List<JobApplication> getApplicationsByStudent(User studentUser) {
        StudentProfile student = studentProfileRepository.findByUser(studentUser)
                .orElseThrow(() -> new IllegalArgumentException("Student profile not found for user: " + studentUser.getEmail()));
        return jobApplicationRepository.findByStudentId(student.getId());
    }

    public List<JobApplication> getApplicationsByCompany(User companyUser) {
        return jobApplicationRepository.findByJobPostCompanyId(companyUser.getId());
    }

    public List<JobApplication> getApplicationsByJobPost(Long jobPostId) {
        return jobApplicationRepository.findByJobPostId(jobPostId);
    }

    @Transactional
    public JobApplication applyToJob(User studentUser, Long jobPostId) {
        StudentProfile student = studentProfileRepository.findByUser(studentUser)
                .orElseThrow(() -> new IllegalArgumentException("Student profile not found for user: " + studentUser.getEmail()));

        JobPost job = jobService.getJobById(jobPostId)
                .orElseThrow(() -> new IllegalArgumentException("Job post not found: " + jobPostId));

        // 1. Verify Job status is OPEN
        if (!"OPEN".equalsIgnoreCase(job.getStatus())) {
            throw new IllegalStateException("Applications are closed for this job.");
        }

        // 2. Check if student already applied
        if (jobApplicationRepository.existsByJobPostIdAndStudentId(jobPostId, student.getId())) {
            throw new IllegalStateException("You have already applied for this job!");
        }

        // 3. Verify Eligibility criteria
        if (!jobService.isStudentEligible(student, job)) {
            throw new IllegalStateException("You do not meet the eligibility criteria (CGPA/Branch) for this job.");
        }

        JobApplication application = new JobApplication(job, student);
        return jobApplicationRepository.save(application);
    }

    @Transactional
    public JobApplication updateApplicationStatus(Long applicationId, ApplicationStatus status, String feedback) {
        JobApplication application = jobApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("Job application not found: " + applicationId));

        application.setStatus(status);
        application.setFeedback(feedback);

        // Business Logic: If selected, set student's profile status to PLACED
        if (status == ApplicationStatus.SELECTED) {
            StudentProfile student = application.getStudent();
            student.setPlacementStatus("PLACED");
            studentProfileRepository.save(student);
        }

        return jobApplicationRepository.save(application);
    }
}
