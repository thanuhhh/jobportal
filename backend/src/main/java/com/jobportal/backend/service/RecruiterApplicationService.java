package com.jobportal.backend.service;

import com.jobportal.backend.dto.ApplicantResponse;
import com.jobportal.backend.dto.PageResponse;
import com.jobportal.backend.entity.Application;
import com.jobportal.backend.entity.ApplicationStatus;
import com.jobportal.backend.entity.Job;
import com.jobportal.backend.entity.JobSeekerProfile;
import com.jobportal.backend.entity.User;
import com.jobportal.backend.exception.ResourceNotFoundException;
import com.jobportal.backend.repository.ApplicationRepository;
import com.jobportal.backend.repository.JobRepository;
import com.jobportal.backend.repository.JobSeekerProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RecruiterApplicationService {

    private final ApplicationRepository applicationRepository;
    private final JobRepository jobRepository;
    private final JobSeekerProfileRepository profileRepository;
    private final CurrentUserService currentUserService;

    @Transactional(readOnly = true)
    public PageResponse<ApplicantResponse> applicants(Long jobId, int page, int size) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));
        checkOwner(job);

        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 50),
                Sort.by("appliedAt").descending());
        Page<Application> result = applicationRepository.findByJobId(jobId, pageable);

        return new PageResponse<>(
                result.getContent().stream().map(this::toResponse).toList(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages());
    }

    @Transactional
    public ApplicantResponse updateStatus(Long applicationId, ApplicationStatus status) {
        Application app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));
        checkOwner(app.getJob());
        app.setStatus(status);
        return toResponse(applicationRepository.save(app));
    }

    private void checkOwner(Job job) {
        User me = currentUserService.getCurrentUser();
        if (!job.getCompany().getRecruiter().getId().equals(me.getId())) {
            throw new AccessDeniedException("Not your job");
        }
    }

    private ApplicantResponse toResponse(Application a) {
        User seeker = a.getSeeker();
        JobSeekerProfile p = profileRepository.findByUserId(seeker.getId()).orElse(null);
        return ApplicantResponse.builder()
                .applicationId(a.getId())
                .jobId(a.getJob().getId())
                .jobTitle(a.getJob().getTitle())
                .seekerName(seeker.getName())
                .seekerEmail(seeker.getEmail())
                .phone(p == null ? null : p.getPhone())
                .skills(p == null ? null : p.getSkills())
                .education(p == null ? null : p.getEducation())
                .experienceYears(p == null ? null : p.getExperienceYears())
                .coverLetter(a.getCoverLetter())
                .status(a.getStatus())
                .hasResume(p != null && p.getResumePath() != null)
                .appliedAt(a.getAppliedAt())
                .build();
    }
}
