package com.jobportal.backend.service;

import com.jobportal.backend.dto.AdminUserResponse;
import com.jobportal.backend.dto.CategoryResponse;
import com.jobportal.backend.dto.JobResponse;
import com.jobportal.backend.dto.PageResponse;
import com.jobportal.backend.entity.Category;
import com.jobportal.backend.entity.Job;
import com.jobportal.backend.entity.Role;
import com.jobportal.backend.entity.User;
import com.jobportal.backend.exception.ResourceNotFoundException;
import com.jobportal.backend.repository.ApplicationRepository;
import com.jobportal.backend.repository.CategoryRepository;
import com.jobportal.backend.repository.JobRepository;
import com.jobportal.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final ApplicationRepository applicationRepository;
    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public PageResponse<AdminUserResponse> users(Role role, int page, int size) {
        Pageable pageable = pageable(page, size);
        Page<User> result = role == null
                ? userRepository.findAll(pageable)
                : userRepository.findByRole(role, pageable);
        return new PageResponse<>(
                result.getContent().stream().map(this::toUser).toList(),
                result.getNumber(), result.getSize(),
                result.getTotalElements(), result.getTotalPages());
    }

    @Transactional
    public AdminUserResponse setEnabled(Long id, boolean enabled) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (user.getRole() == Role.ADMIN) {
            throw new IllegalArgumentException("Admin accounts cannot be blocked");
        }
        user.setEnabled(enabled);
        return toUser(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public PageResponse<JobResponse> jobs(Boolean approved, int page, int size) {
        Pageable pageable = pageable(page, size);
        Page<Job> result = approved == null
                ? jobRepository.findAll(pageable)
                : jobRepository.findByApproved(approved, pageable);
        return new PageResponse<>(
                result.getContent().stream().map(JobMapper::toResponse).toList(),
                result.getNumber(), result.getSize(),
                result.getTotalElements(), result.getTotalPages());
    }

    @Transactional
    public JobResponse approveJob(Long id) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));
        job.setApproved(true);
        return JobMapper.toResponse(jobRepository.save(job));
    }

    @Transactional
    public void deleteJob(Long id) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));
        jobRepository.delete(job);
    }

    public CategoryResponse addCategory(String name) {
        String clean = name.trim();
        if (categoryRepository.existsByNameIgnoreCase(clean)) {
            throw new IllegalArgumentException("Category already exists");
        }
        Category saved = categoryRepository.save(Category.builder().name(clean).build());
        return new CategoryResponse(saved.getId(), saved.getName());
    }

    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Category not found");
        }
        categoryRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Map<String, Long> stats() {
        Map<String, Long> m = new LinkedHashMap<>();
        m.put("users", userRepository.count());
        m.put("jobs", jobRepository.count());
        m.put("pendingJobs", jobRepository.countByApproved(false));
        m.put("applications", applicationRepository.count());
        m.put("categories", categoryRepository.count());
        return m;
    }

    private Pageable pageable(int page, int size) {
        return PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 50),
                Sort.by("id").descending());
    }

    private AdminUserResponse toUser(User u) {
        return AdminUserResponse.builder()
                .id(u.getId())
                .name(u.getName())
                .email(u.getEmail())
                .role(u.getRole())
                .enabled(u.isEnabled())
                .createdAt(u.getCreatedAt())
                .build();
    }
}
