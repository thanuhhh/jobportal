package com.jobportal.backend.service;

import com.jobportal.backend.dto.ApplicationResponse;
import com.jobportal.backend.dto.PageResponse;
import com.jobportal.backend.entity.Application;
import com.jobportal.backend.entity.Job;
import com.jobportal.backend.entity.JobStatus;
import com.jobportal.backend.entity.User;
import com.jobportal.backend.exception.ResourceNotFoundException;
import com.jobportal.backend.repository.ApplicationRepository;
import com.jobportal.backend.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final JobRepository jobRepository;
    private final CurrentUserService currentUserService;

    @Transactional
    public ApplicationResponse apply(Long jobId, String coverLetter) {
        User me = currentUserService.getCurrentUser();
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));

        if (job.getStatus() != JobStatus.OPEN || !job.isApproved()) {
            throw new IllegalArgumentException("This job is closed");
        }
        if (applicationRepository.existsByJobIdAndSeekerId(jobId, me.getId())) {
            throw new IllegalArgumentException("You have already applied to this job");
        }

        Application app = Application.builder()
                .job(job)
                .seeker(me)
                .coverLetter(coverLetter)
                .build();
        return toResponse(applicationRepository.save(app));
    }

    @Transactional(readOnly = true)
    public PageResponse<ApplicationResponse> myApplications(int page, int size) {
        User me = currentUserService.getCurrentUser();
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 50),
                Sort.by("appliedAt").descending());
        Page<Application> result = applicationRepository.findBySeekerId(me.getId(), pageable);

        return new PageResponse<>(
                result.getContent().stream().map(this::toResponse).toList(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages());
    }

    private ApplicationResponse toResponse(Application a) {
        return ApplicationResponse.builder()
                .id(a.getId())
                .jobId(a.getJob().getId())
                .jobTitle(a.getJob().getTitle())
                .companyName(a.getJob().getCompany().getName())
                .status(a.getStatus())
                .appliedAt(a.getAppliedAt())
                .build();
    }
}
