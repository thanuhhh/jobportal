package com.jobportal.backend.controller;

import com.jobportal.backend.dto.ApplicantResponse;
import com.jobportal.backend.dto.PageResponse;
import com.jobportal.backend.dto.StatusUpdateRequest;
import com.jobportal.backend.service.RecruiterApplicationService;
import com.jobportal.backend.service.ResumeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recruiter")
@RequiredArgsConstructor
public class RecruiterApplicantController {

    private final RecruiterApplicationService applicationService;
    private final ResumeService resumeService;

    @GetMapping("/jobs/{jobId}/applications")
    public ResponseEntity<PageResponse<ApplicantResponse>> applicants(
            @PathVariable Long jobId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(applicationService.applicants(jobId, page, size));
    }

    @PatchMapping("/applications/{id}/status")
    public ResponseEntity<ApplicantResponse> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody StatusUpdateRequest req) {
        return ResponseEntity.ok(applicationService.updateStatus(id, req.getStatus()));
    }

    @GetMapping("/applications/{id}/resume")
    public ResponseEntity<Resource> resume(@PathVariable Long id) {
        Resource file = resumeService.loadForRecruiter(id);
        ContentDisposition cd = ContentDisposition.inline().filename("resume.pdf").build();
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, cd.toString())
                .body(file);
    }
}
