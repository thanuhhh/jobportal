package com.jobportal.backend.repository;

import com.jobportal.backend.entity.SavedJob;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SavedJobRepository extends JpaRepository<SavedJob, Long> {
    boolean existsByJobIdAndSeekerId(Long jobId, Long seekerId);
    Optional<SavedJob> findByJobIdAndSeekerId(Long jobId, Long seekerId);
    Page<SavedJob> findBySeekerId(Long seekerId, Pageable pageable);
}
