package com.jobportal.backend.controller;

import com.jobportal.backend.dto.ProfileRequest;
import com.jobportal.backend.dto.ProfileResponse;
import com.jobportal.backend.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/seeker/profile")
@RequiredArgsConstructor
public class SeekerProfileController {

    private final ProfileService profileService;

    @GetMapping
    public ResponseEntity<ProfileResponse> get() {
        return ResponseEntity.ok(profileService.getMyProfile());
    }

    @PutMapping
    public ResponseEntity<ProfileResponse> save(@Valid @RequestBody ProfileRequest req) {
        return ResponseEntity.ok(profileService.saveMyProfile(req));
    }
}
