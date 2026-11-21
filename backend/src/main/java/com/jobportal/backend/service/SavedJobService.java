package com.jobportal.backend.service;

import com.jobportal.backend.dto.JobResponse;
import com.jobportal.backend.dto.PageResponse;
import com.jobportal.backend.entity.Job;
import com.jobportal.backend.entity.SavedJob;
import com.jobportal.backend.entity.User;
import com.jobportal.backend.exception.ResourceNotFoundException;
import com.jobportal.backend.repository.JobRepository;
import com.jobportal.backend.repository.SavedJobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SavedJobService {

    private final SavedJobRepository savedJobRepository;
    private final JobRepository jobRepository;
    private final CurrentUserService currentUserService;

    @Transactional
    public void save(Long jobId) {
        User me = currentUserService.getCurrentUser();
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));
        if (savedJobRepository.existsByJobIdAndSeekerId(jobId, me.getId())) {
            return;
        }
        savedJobRepository.save(SavedJob.builder().job(job).seeker(me).build());
    }

    @Transactional
    public void unsave(Long jobId) {
        User me = currentUserService.getCurrentUser();
        savedJobRepository.findByJobIdAndSeekerId(jobId, me.getId())
                .ifPresent(savedJobRepository::delete);
    }

    @Transactional(readOnly = true)
    public PageResponse<JobResponse> mySavedJobs(int page, int size) {
        User me = currentUserService.getCurrentUser();
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 50),
                Sort.by("savedAt").descending());
        Page<SavedJob> result = savedJobRepository.findBySeekerId(me.getId(), pageable);

        return new PageResponse<>(
                result.getContent().stream().map(s -> JobMapper.toResponse(s.getJob())).toList(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages());
    }
}
