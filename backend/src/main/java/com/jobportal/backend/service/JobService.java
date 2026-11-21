package com.jobportal.backend.service;

import com.jobportal.backend.dto.JobRequest;
import com.jobportal.backend.dto.JobResponse;
import com.jobportal.backend.dto.PageResponse;
import com.jobportal.backend.entity.*;
import com.jobportal.backend.exception.ResourceNotFoundException;
import com.jobportal.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class JobService {

    private final JobRepository jobRepository;
    private final CompanyRepository companyRepository;
    private final CategoryRepository categoryRepository;
    private final CurrentUserService currentUserService;

    @Transactional
    public JobResponse createJob(JobRequest req) {
        validateSalary(req);
        Company company = getMyCompanyOrThrow();
        Category category = getCategoryOrThrow(req.getCategoryId());

        Job job = Job.builder()
                .title(req.getTitle())
                .description(req.getDescription())
                .location(req.getLocation())
                .salaryMin(req.getSalaryMin())
                .salaryMax(req.getSalaryMax())
                .type(req.getType())
                .experienceYears(req.getExperienceYears())
                .company(company)
                .category(category)
                .build();

        return toResponse(jobRepository.save(job));
    }

    @Transactional
    public JobResponse updateJob(Long id, JobRequest req) {
        validateSalary(req);
        Job job = getOwnedJob(id);
        Category category = getCategoryOrThrow(req.getCategoryId());

        job.setTitle(req.getTitle());
        job.setDescription(req.getDescription());
        job.setLocation(req.getLocation());
        job.setSalaryMin(req.getSalaryMin());
        job.setSalaryMax(req.getSalaryMax());
        job.setType(req.getType());
        job.setExperienceYears(req.getExperienceYears());
        job.setCategory(category);

        return toResponse(jobRepository.save(job));
    }

    @Transactional
    public JobResponse closeJob(Long id) {
        Job job = getOwnedJob(id);
        job.setStatus(JobStatus.CLOSED);
        return toResponse(jobRepository.save(job));
    }

    @Transactional
    public void deleteJob(Long id) {
        Job job = getOwnedJob(id);
        jobRepository.delete(job);
    }

    @Transactional(readOnly = true)
    public JobResponse getMyJob(Long id) {
        return toResponse(getOwnedJob(id));
    }

    @Transactional(readOnly = true)
    public PageResponse<JobResponse> getMyJobs(int page, int size) {
        Company company = getMyCompanyOrThrow();
        Pageable pageable = PageRequest.of(page, Math.min(size, 50), Sort.by("createdAt").descending());
        Page<Job> result = jobRepository.findByCompanyId(company.getId(), pageable);

        return new PageResponse<>(
                result.getContent().stream().map(this::toResponse).toList(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages());
    }

    private Company getMyCompanyOrThrow() {
        User recruiter = currentUserService.getCurrentUser();
        return companyRepository.findByRecruiterId(recruiter.getId())
                .orElseThrow(() -> new IllegalArgumentException("Create your company profile first"));
    }

    private Category getCategoryOrThrow(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
    }

    private Job getOwnedJob(Long id) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));
        User me = currentUserService.getCurrentUser();
        if (!job.getCompany().getRecruiter().getId().equals(me.getId())) {
            throw new AccessDeniedException("Not your job");
        }
        return job;
    }

    private void validateSalary(JobRequest req) {
        if (req.getSalaryMin() != null && req.getSalaryMax() != null
                && req.getSalaryMin() > req.getSalaryMax()) {
            throw new IllegalArgumentException("Minimum salary cannot be greater than maximum salary");
        }
    }

    private JobResponse toResponse(Job j) {
        return JobResponse.builder()
                .id(j.getId())
                .title(j.getTitle())
                .description(j.getDescription())
                .location(j.getLocation())
                .salaryMin(j.getSalaryMin())
                .salaryMax(j.getSalaryMax())
                .type(j.getType())
                .experienceYears(j.getExperienceYears())
                .status(j.getStatus())
                .approved(j.isApproved())
                .categoryId(j.getCategory().getId())
                .categoryName(j.getCategory().getName())
                .companyId(j.getCompany().getId())
                .companyName(j.getCompany().getName())
                .createdAt(j.getCreatedAt())
                .build();
    }
}
