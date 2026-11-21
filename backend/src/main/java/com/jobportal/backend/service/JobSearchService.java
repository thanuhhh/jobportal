package com.jobportal.backend.service;

import com.jobportal.backend.dto.JobResponse;
import com.jobportal.backend.dto.PageResponse;
import com.jobportal.backend.entity.Job;
import com.jobportal.backend.entity.JobStatus;
import com.jobportal.backend.entity.JobType;
import com.jobportal.backend.exception.ResourceNotFoundException;
import com.jobportal.backend.repository.JobRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JobSearchService {

    private final JobRepository jobRepository;

    @Transactional(readOnly = true)
    public PageResponse<JobResponse> search(String keyword, String location, Long categoryId,
                                            JobType type, Long minSalary, Integer maxExperience,
                                            int page, int size) {

        Specification<Job> spec = (root, query, cb) -> {
            List<Predicate> ps = new ArrayList<>();
            ps.add(cb.equal(root.get("status"), JobStatus.OPEN));
            ps.add(cb.isTrue(root.<Boolean>get("approved")));

            if (has(keyword)) {
                String like = "%" + keyword.trim().toLowerCase() + "%";
                ps.add(cb.or(
                        cb.like(cb.lower(root.get("title")), like),
                        cb.like(cb.lower(root.get("description")), like)));
            }
            if (has(location)) {
                ps.add(cb.like(cb.lower(root.get("location")),
                        "%" + location.trim().toLowerCase() + "%"));
            }
            if (categoryId != null) {
                ps.add(cb.equal(root.get("category").get("id"), categoryId));
            }
            if (type != null) {
                ps.add(cb.equal(root.get("type"), type));
            }
            if (minSalary != null) {
                ps.add(cb.greaterThanOrEqualTo(root.<Long>get("salaryMax"), minSalary));
            }
            if (maxExperience != null) {
                ps.add(cb.lessThanOrEqualTo(root.<Integer>get("experienceYears"), maxExperience));
            }
            return cb.and(ps.toArray(new Predicate[0]));
        };

        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 50),
                Sort.by("createdAt").descending());
        Page<Job> result = jobRepository.findAll(spec, pageable);

        return new PageResponse<>(
                result.getContent().stream().map(JobMapper::toResponse).toList(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages());
    }

    @Transactional(readOnly = true)
    public JobResponse getById(Long id) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));
        if (!job.isApproved()) {
            throw new ResourceNotFoundException("Job not found");
        }
        return JobMapper.toResponse(job);
    }

    private boolean has(String s) {
        return s != null && !s.isBlank();
    }
}
