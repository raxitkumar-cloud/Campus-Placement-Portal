package com.placement.portal.model;

import jakarta.persistence.*;

@Entity
@Table(name = "student_profiles")
public class StudentProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Double cgpa;

    @Column(nullable = false)
    private String branch;

    @Column(columnDefinition = "TEXT")
    private String skills;

    @Column(name = "resume_url")
    private String resumeUrl;

    @Column(name = "graduation_year", nullable = false)
    private Integer graduationYear;

    @Column(name = "placement_status", nullable = false)
    private String placementStatus = "UNPLACED"; // PLACED or UNPLACED

    // Constructors
    public StudentProfile() {
    }

    public StudentProfile(User user, Double cgpa, String branch, String skills, String resumeUrl, Integer graduationYear) {
        this.user = user;
        this.cgpa = cgpa;
        this.branch = branch;
        this.skills = skills;
        this.resumeUrl = resumeUrl;
        this.graduationYear = graduationYear;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Double getCgpa() {
        return cgpa;
    }

    public void setCgpa(Double cgpa) {
        this.cgpa = cgpa;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public String getResumeUrl() {
        return resumeUrl;
    }

    public void setResumeUrl(String resumeUrl) {
        this.resumeUrl = resumeUrl;
    }

    public Integer getGraduationYear() {
        return graduationYear;
    }

    public void setGraduationYear(Integer graduationYear) {
        this.graduationYear = graduationYear;
    }

    public String getPlacementStatus() {
        return placementStatus;
    }

    public void setPlacementStatus(String placementStatus) {
        this.placementStatus = placementStatus;
    }
}
