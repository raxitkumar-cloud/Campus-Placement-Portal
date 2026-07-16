package com.placement.portal.model;

import jakarta.persistence.*;

@Entity
@Table(name = "company_profiles")
public class CompanyProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "company_name", nullable = false)
    private String companyName;

    @Column(nullable = false)
    private String industry;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column
    private String website;

    // Constructors
    public CompanyProfile() {
    }

    public CompanyProfile(User user, String companyName, String industry, String description, String website) {
        this.user = user;
        this.companyName = companyName;
        this.industry = industry;
        this.description = description;
        this.website = website;
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

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }
}
