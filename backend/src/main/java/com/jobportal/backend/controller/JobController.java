package com.jobportal.backend.controller;

import com.jobportal.backend.dto.JobResponse;
import com.jobportal.backend.dto.PageResponse;
import com.jobportal.backend.entity.JobType;
import com.jobportal.backend.service.JobSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobSearchService jobSearchService;

    @GetMapping
    public ResponseEntity<PageResponse<JobResponse>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) JobType type,
            @RequestParam(required = false) Long minSalary,
            @RequestParam(required = false) Integer maxExperience,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(jobSearchService.search(
                keyword, location, categoryId, type, minSalary, maxExperience, page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobResponse> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(jobSearchService.getById(id));
    }
}
