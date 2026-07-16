package com.placement.portal.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "job_applications")
public class JobApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "job_post_id", nullable = false)
    private JobPost jobPost;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_profile_id", nullable = false)
    private StudentProfile student;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus status = ApplicationStatus.APPLIED;

    @Column(name = "applied_at", nullable = false)
    private LocalDateTime appliedAt;

    @Column(columnDefinition = "TEXT")
    private String feedback;

    // Constructors
    public JobApplication() {
        this.appliedAt = LocalDateTime.now();
    }

    public JobApplication(JobPost jobPost, StudentProfile student) {
        this();
        this.jobPost = jobPost;
        this.student = student;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public JobPost getJobPost() {
        return jobPost;
    }

    public void setJobPost(JobPost jobPost) {
        this.jobPost = jobPost;
    }

    public StudentProfile getStudent() {
        return student;
    }

    public void setStudent(StudentProfile student) {
        this.student = student;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }

    public LocalDateTime getAppliedAt() {
        return appliedAt;
    }

    public void setAppliedAt(LocalDateTime appliedAt) {
        this.appliedAt = appliedAt;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
}
