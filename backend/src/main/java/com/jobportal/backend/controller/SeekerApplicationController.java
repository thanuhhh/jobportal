package com.jobportal.backend.controller;

import com.jobportal.backend.dto.ApplicationResponse;
import com.jobportal.backend.dto.ApplyRequest;
import com.jobportal.backend.dto.PageResponse;
import com.jobportal.backend.service.ApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/seeker")
@RequiredArgsConstructor
public class SeekerApplicationController {

    private final ApplicationService applicationService;

    @PostMapping("/jobs/{jobId}/apply")
    public ResponseEntity<ApplicationResponse> apply(
            @PathVariable Long jobId,
            @Valid @RequestBody(required = false) ApplyRequest req) {
        String cover = req == null ? null : req.getCoverLetter();
        return ResponseEntity.status(HttpStatus.CREATED).body(applicationService.apply(jobId, cover));
    }

    @GetMapping("/applications")
    public ResponseEntity<PageResponse<ApplicationResponse>> myApplications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(applicationService.myApplications(page, size));
    }
}
