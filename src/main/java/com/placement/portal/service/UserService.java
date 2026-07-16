package com.placement.portal.service;

import com.placement.portal.dto.SignupRequest;
import com.placement.portal.model.*;
import com.placement.portal.repository.CompanyProfileRepository;
import com.placement.portal.repository.StudentProfileRepository;
import com.placement.portal.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final CompanyProfileRepository companyProfileRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       StudentProfileRepository studentProfileRepository,
                       CompanyProfileRepository companyProfileRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.studentProfileRepository = studentProfileRepository;
        this.companyProfileRepository = companyProfileRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<StudentProfile> getStudentProfile(User user) {
        return studentProfileRepository.findByUser(user);
    }

    public Optional<CompanyProfile> getCompanyProfile(User user) {
        return companyProfileRepository.findByUser(user);
    }

    @Transactional
    public User registerUser(SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email is already registered!");
        }

        // Create and save main User
        User user = new User(
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getFullName(),
                request.getRole()
        );
        User savedUser = userRepository.save(user);

        // Create role-specific profiles
        if (request.getRole() == Role.STUDENT) {
            Double cgpa = request.getCgpa() != null ? request.getCgpa() : 0.0;
            String branch = request.getBranch() != null ? request.getBranch() : "Not Specified";
            Integer gradYear = request.getGraduationYear() != null ? request.getGraduationYear() : 2026;
            
            StudentProfile studentProfile = new StudentProfile(
                    savedUser,
                    cgpa,
                    branch,
                    request.getSkills(),
                    request.getResumeUrl(),
                    gradYear
            );
            studentProfileRepository.save(studentProfile);
        } else if (request.getRole() == Role.COMPANY) {
            String companyName = request.getCompanyName() != null ? request.getCompanyName() : "Not Specified";
            String industry = request.getIndustry() != null ? request.getIndustry() : "Not Specified";
            
            CompanyProfile companyProfile = new CompanyProfile(
                    savedUser,
                    companyName,
                    industry,
                    request.getDescription(),
                    request.getWebsite()
            );
            companyProfileRepository.save(companyProfile);
        }

        return savedUser;
    }

    @Transactional
    public StudentProfile updateStudentProfile(User user, StudentProfile updatedData) {
        StudentProfile profile = studentProfileRepository.findByUser(user)
                .orElseThrow(() -> new IllegalStateException("Student profile not found for user: " + user.getEmail()));

        profile.setCgpa(updatedData.getCgpa());
        profile.setBranch(updatedData.getBranch());
        profile.setSkills(updatedData.getSkills());
        profile.setResumeUrl(updatedData.getResumeUrl());
        profile.setGraduationYear(updatedData.getGraduationYear());
        if (updatedData.getPlacementStatus() != null) {
            profile.setPlacementStatus(updatedData.getPlacementStatus());
        }

        return studentProfileRepository.save(profile);
    }

    @Transactional
    public CompanyProfile updateCompanyProfile(User user, CompanyProfile updatedData) {
        CompanyProfile profile = companyProfileRepository.findByUser(user)
                .orElseThrow(() -> new IllegalStateException("Company profile not found for user: " + user.getEmail()));

        profile.setCompanyName(updatedData.getCompanyName());
        profile.setIndustry(updatedData.getIndustry());
        profile.setDescription(updatedData.getDescription());
        profile.setWebsite(updatedData.getWebsite());

        return companyProfileRepository.save(profile);
    }
}
