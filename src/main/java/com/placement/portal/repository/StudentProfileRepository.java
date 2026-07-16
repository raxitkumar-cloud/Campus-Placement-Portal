package com.placement.portal.repository;

import com.placement.portal.model.StudentProfile;
import com.placement.portal.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentProfileRepository extends JpaRepository<StudentProfile, Long> {
    Optional<StudentProfile> findByUser(User user);
    Optional<StudentProfile> findByUserId(Long userId);
}
