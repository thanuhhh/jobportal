package com.jobportal.backend.controller;

import com.jobportal.backend.dto.JobResponse;
import com.jobportal.backend.dto.PageResponse;
import com.jobportal.backend.service.SavedJobService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/seeker/saved-jobs")
@RequiredArgsConstructor
public class SeekerSavedJobController {

    private final SavedJobService savedJobService;

    @PostMapping("/{jobId}")
    public ResponseEntity<Void> save(@PathVariable Long jobId) {
        savedJobService.save(jobId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{jobId}")
    public ResponseEntity<Void> unsave(@PathVariable Long jobId) {
        savedJobService.unsave(jobId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<PageResponse<JobResponse>> mine(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(savedJobService.mySavedJobs(page, size));
    }
}
