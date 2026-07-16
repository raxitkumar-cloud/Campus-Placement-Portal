package com.placement.portal.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "job_posts")
public class JobPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "company_id", nullable = false)
    private CompanyProfile company;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private Double ctc; // in LPA

    @Column(name = "eligible_cgpa", nullable = false)
    private Double eligibleCgpa;

    @Column(name = "eligible_branches", nullable = false)
    private String eligibleBranches; // e.g. "CSE, IT, ECE" or "ALL"

    @Column(nullable = false)
    private String status = "OPEN"; // OPEN or CLOSED

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // Constructors
    public JobPost() {
        this.createdAt = LocalDateTime.now();
    }

    public JobPost(CompanyProfile company, String title, String description, String location, Double ctc, Double eligibleCgpa, String eligibleBranches) {
        this();
        this.company = company;
        this.title = title;
        this.description = description;
        this.location = location;
        this.ctc = ctc;
        this.eligibleCgpa = eligibleCgpa;
        this.eligibleBranches = eligibleBranches;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CompanyProfile getCompany() {
        return company;
    }

    public void setCompany(CompanyProfile company) {
        this.company = company;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Double getCtc() {
        return ctc;
    }

    public void setCtc(Double ctc) {
        this.ctc = ctc;
    }

    public Double getEligibleCgpa() {
        return eligibleCgpa;
    }

    public void setEligibleCgpa(Double eligibleCgpa) {
        this.eligibleCgpa = eligibleCgpa;
    }

    public String getEligibleBranches() {
        return eligibleBranches;
    }

    public void setEligibleBranches(String eligibleBranches) {
        this.eligibleBranches = eligibleBranches;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
