package com.jobportal.backend.repository;

import com.jobportal.backend.entity.JobSeekerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface JobSeekerProfileRepository extends JpaRepository<JobSeekerProfile, Long> {
    Optional<JobSeekerProfile> findByUserId(Long userId);
}
