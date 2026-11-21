package com.jobportal.backend.repository;

import com.jobportal.backend.entity.Application;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
    boolean existsByJobIdAndSeekerId(Long jobId, Long seekerId);
    Page<Application> findBySeekerId(Long seekerId, Pageable pageable);
    Page<Application> findByJobId(Long jobId, Pageable pageable);
}
