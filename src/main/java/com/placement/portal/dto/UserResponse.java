package com.placement.portal.dto;

import com.placement.portal.model.CompanyProfile;
import com.placement.portal.model.Role;
import com.placement.portal.model.StudentProfile;
import com.placement.portal.model.User;

public class UserResponse {
    private Long id;
    private String email;
    private String fullName;
    private Role role;
    private StudentProfile studentProfile;
    private CompanyProfile companyProfile;

    // Constructors
    public UserResponse() {}

    public UserResponse(User user, StudentProfile studentProfile, CompanyProfile companyProfile) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.fullName = user.getFullName();
        this.role = user.getRole();
        this.studentProfile = studentProfile;
        this.companyProfile = companyProfile;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public StudentProfile getStudentProfile() {
        return studentProfile;
    }

    public void setStudentProfile(StudentProfile studentProfile) {
        this.studentProfile = studentProfile;
    }

    public CompanyProfile getCompanyProfile() {
        return companyProfile;
    }

    public void setCompanyProfile(CompanyProfile companyProfile) {
        this.companyProfile = companyProfile;
    }
}
