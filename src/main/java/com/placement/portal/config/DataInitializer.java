package com.placement.portal.config;

import com.placement.portal.dto.SignupRequest;
import com.placement.portal.model.*;
import com.placement.portal.repository.JobApplicationRepository;
import com.placement.portal.repository.UserRepository;
import com.placement.portal.service.ApplicationService;
import com.placement.portal.service.JobService;
import com.placement.portal.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final UserService userService;
    private final JobService jobService;
    private final ApplicationService applicationService;
    private final PasswordEncoder passwordEncoder;
    private final JobApplicationRepository jobApplicationRepository;

    public DataInitializer(UserRepository userRepository,
                           UserService userService,
                           JobService jobService,
                           ApplicationService applicationService,
                           PasswordEncoder passwordEncoder,
                           JobApplicationRepository jobApplicationRepository) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.jobService = jobService;
        this.applicationService = applicationService;
        this.passwordEncoder = passwordEncoder;
        this.jobApplicationRepository = jobApplicationRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() > 0) {
            return; // DB already initialized
        }

        // 1. Create Admin
        SignupRequest adminReq = new SignupRequest();
        adminReq.setEmail("admin@placement.com");
        adminReq.setPassword("admin123");
        adminReq.setFullName("Placement Coordinator");
        adminReq.setRole(Role.ADMIN);
        userService.registerUser(adminReq);

        // 2. Create Students
        SignupRequest s1 = new SignupRequest();
        s1.setEmail("student@placement.com");
        s1.setPassword("student123");
        s1.setFullName("Raj Sharma");
        s1.setRole(Role.STUDENT);
        s1.setCgpa(8.5);
        s1.setBranch("CSE");
        s1.setSkills("Java, Spring Boot, Javascript, SQL");
        s1.setResumeUrl("https://drive.google.com/mock-resume-raj");
        s1.setGraduationYear(2026);
        User userS1 = userService.registerUser(s1);

        SignupRequest s2 = new SignupRequest();
        s2.setEmail("aman@placement.com");
        s2.setPassword("student123");
        s2.setFullName("Aman Verma");
        s2.setRole(Role.STUDENT);
        s2.setCgpa(7.2);
        s2.setBranch("ECE");
        s2.setSkills("C++, Embedded C, IoT, Python");
        s2.setResumeUrl("https://drive.google.com/mock-resume-aman");
        s2.setGraduationYear(2026);
        User userS2 = userService.registerUser(s2);

        SignupRequest s3 = new SignupRequest();
        s3.setEmail("priya@placement.com");
        s3.setPassword("student123");
        s3.setFullName("Priya Patel");
        s3.setRole(Role.STUDENT);
        s3.setCgpa(9.1);
        s3.setBranch("CSE");
        s3.setSkills("Python, Machine Learning, Docker, AWS");
        s3.setResumeUrl("https://drive.google.com/mock-resume-priya");
        s3.setGraduationYear(2026);
        User userS3 = userService.registerUser(s3);

        // 3. Create Companies
        SignupRequest c1 = new SignupRequest();
        c1.setEmail("company@placement.com");
        c1.setPassword("company123");
        c1.setFullName("HR Manager");
        c1.setRole(Role.COMPANY);
        c1.setCompanyName("TechSoft Solutions");
        c1.setIndustry("IT / Software Services");
        c1.setDescription("TechSoft is a global digital consulting and software engineering firm leading digital transformation initiatives.");
        c1.setWebsite("https://techsoft.example.com");
        User userC1 = userService.registerUser(c1);

        SignupRequest c2 = new SignupRequest();
        c2.setEmail("google@placement.com");
        c2.setPassword("company123");
        c2.setFullName("Google Recruiter");
        c2.setRole(Role.COMPANY);
        c2.setCompanyName("Google Corp");
        c2.setIndustry("Product / Tech");
        c2.setDescription("Organizing the world's information and making it universally accessible and useful.");
        c2.setWebsite("https://careers.google.com");
        User userC2 = userService.registerUser(c2);

        // 4. Create Job Posts
        // For TechSoft
        com.placement.portal.dto.JobPostRequest job1 = new com.placement.portal.dto.JobPostRequest();
        job1.setTitle("Software Engineer Intern");
        job1.setDescription("We are looking for self-motivated Software Engineering interns who are passionate about learning Java/Spring and web technologies. You will work on real-world client deliverables.");
        job1.setLocation("Bangalore, India");
        job1.setCtc(12.0); // 12 LPA
        job1.setEligibleCgpa(7.5);
        job1.setEligibleBranches("CSE, IT, ECE");
        JobPost jPost1 = jobService.createJobPost(userC1, job1);

        com.placement.portal.dto.JobPostRequest job2 = new com.placement.portal.dto.JobPostRequest();
        job2.setTitle("Associate QA Analyst");
        job2.setDescription("Looking for quality assurance testing associates. Experience with manual testing and Selenium automation is a plus.");
        job2.setLocation("Pune, India");
        job2.setCtc(6.5); // 6.5 LPA
        job2.setEligibleCgpa(6.0);
        job2.setEligibleBranches("ALL");
        JobPost jPost2 = jobService.createJobPost(userC1, job2);

        // For Google
        com.placement.portal.dto.JobPostRequest job3 = new com.placement.portal.dto.JobPostRequest();
        job3.setTitle("Site Reliability Engineer");
        job3.setDescription("SREs combine software engineering and systems engineering to build massive scalable systems. Deep understanding of UNIX systems, networking, and Python/Go is required.");
        job3.setLocation("Hyderabad, India");
        job3.setCtc(24.5); // 24.5 LPA
        job3.setEligibleCgpa(8.0);
        job3.setEligibleBranches("CSE, IT");
        JobPost jPost3 = jobService.createJobPost(userC2, job3);

        // 5. Create Job Applications
        // Raj Sharma (S1) applies to Software Engineer Intern (eligible)
        JobApplication app1 = applicationService.applyToJob(userS1, jPost1.getId());
        applicationService.updateApplicationStatus(app1.getId(), ApplicationStatus.SHORTLISTED, "Resume matched requirements. Shortlisted for technical round.");

        // Raj Sharma (S1) applies to Associate QA Analyst (eligible)
        applicationService.applyToJob(userS1, jPost2.getId());

        // Aman Verma (S2) applies to Associate QA Analyst (eligible)
        JobApplication app3 = applicationService.applyToJob(userS2, jPost2.getId());
        // Aman Verma gets selected! (makes him Placed)
        applicationService.updateApplicationStatus(app3.getId(), ApplicationStatus.SELECTED, "Congratulations! You cleared all technical rounds.");

        // Priya Patel (S3) applies to SRE at Google (eligible)
        JobApplication app4 = applicationService.applyToJob(userS3, jPost3.getId());
        applicationService.updateApplicationStatus(app4.getId(), ApplicationStatus.SHORTLISTED, "Shortlisted for SRE online coding challenge.");
    }
}
