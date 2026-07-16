package com.placement.portal.service;

import com.placement.portal.model.JobApplication;
import com.placement.portal.model.JobPost;
import com.placement.portal.model.StudentProfile;
import com.placement.portal.repository.JobApplicationRepository;
import com.placement.portal.repository.JobPostRepository;
import com.placement.portal.repository.StudentProfileRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StatsService {

    private final StudentProfileRepository studentProfileRepository;
    private final JobPostRepository jobPostRepository;
    private final JobApplicationRepository jobApplicationRepository;

    public StatsService(StudentProfileRepository studentProfileRepository,
                        JobPostRepository jobPostRepository,
                        JobApplicationRepository jobApplicationRepository) {
        this.studentProfileRepository = studentProfileRepository;
        this.jobPostRepository = jobPostRepository;
        this.jobApplicationRepository = jobApplicationRepository;
    }

    public Map<String, Object> getGlobalStats() {
        Map<String, Object> stats = new HashMap<>();

        List<StudentProfile> students = studentProfileRepository.findAll();
        long totalStudents = students.size();
        long placedStudents = students.stream().filter(s -> "PLACED".equalsIgnoreCase(s.getPlacementStatus())).count();
        long unplacedStudents = totalStudents - placedStudents;
        double placementPercentage = totalStudents > 0 ? ((double) placedStudents / totalStudents) * 100 : 0.0;

        List<JobPost> jobs = jobPostRepository.findAll();
        long totalJobs = jobs.size();
        long openJobs = jobs.stream().filter(j -> "OPEN".equalsIgnoreCase(j.getStatus())).count();
        double avgCtc = jobs.stream().mapToDouble(JobPost::getCtc).average().orElse(0.0);

        List<JobApplication> applications = jobApplicationRepository.findAll();
        long totalApplications = applications.size();

        stats.put("totalStudents", totalStudents);
        stats.put("placedStudents", placedStudents);
        stats.put("unplacedStudents", unplacedStudents);
        stats.put("placementPercentage", Math.round(placementPercentage * 10.0) / 10.0); // round to 1 decimal
        stats.put("totalJobs", totalJobs);
        stats.put("openJobs", openJobs);
        stats.put("averageCtc", Math.round(avgCtc * 100.0) / 100.0); // round to 2 decimals
        stats.put("totalApplications", totalApplications);

        return stats;
    }
}
