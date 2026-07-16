package com.placement.portal.repository;

import com.placement.portal.model.JobPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobPostRepository extends JpaRepository<JobPost, Long> {
    List<JobPost> findByCompanyId(Long companyId);
    List<JobPost> findByStatus(String status);
}
