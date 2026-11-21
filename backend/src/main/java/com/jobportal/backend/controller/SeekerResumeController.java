package com.jobportal.backend.controller;

import com.jobportal.backend.dto.ProfileResponse;
import com.jobportal.backend.service.ResumeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/seeker/profile")
@RequiredArgsConstructor
public class SeekerResumeController {

    private final ResumeService resumeService;

    @PostMapping("/resume")
    public ResponseEntity<ProfileResponse> upload(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(resumeService.upload(file));
    }
}
