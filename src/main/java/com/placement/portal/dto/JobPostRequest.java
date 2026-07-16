package com.placement.portal.dto;

public class JobPostRequest {
    private String title;
    private String description;
    private String location;
    private Double ctc;
    private Double eligibleCgpa;
    private String eligibleBranches;

    // Constructors
    public JobPostRequest() {}

    // Getters and Setters
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
}
