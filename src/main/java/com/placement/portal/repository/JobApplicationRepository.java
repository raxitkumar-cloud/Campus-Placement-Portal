package com.placement.portal.repository;

import com.placement.portal.model.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {
    List<JobApplication> findByStudentId(Long studentId);
    List<JobApplication> findByJobPostId(Long jobPostId);
    boolean existsByJobPostIdAndStudentId(Long jobPostId, Long studentId);
    List<JobApplication> findByJobPostCompanyId(Long companyId);
}
