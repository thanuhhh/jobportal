package com.jobportal.backend.controller;

import com.jobportal.backend.dto.AdminUserResponse;
import com.jobportal.backend.dto.CategoryRequest;
import com.jobportal.backend.dto.CategoryResponse;
import com.jobportal.backend.dto.JobResponse;
import com.jobportal.backend.dto.PageResponse;
import com.jobportal.backend.entity.Role;
import com.jobportal.backend.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Long>> stats() {
        return ResponseEntity.ok(adminService.stats());
    }

    @GetMapping("/users")
    public ResponseEntity<PageResponse<AdminUserResponse>> users(
            @RequestParam(required = false) Role role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(adminService.users(role, page, size));
    }

    @PatchMapping("/users/{id}/block")
    public ResponseEntity<AdminUserResponse> block(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.setEnabled(id, false));
    }

    @PatchMapping("/users/{id}/unblock")
    public ResponseEntity<AdminUserResponse> unblock(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.setEnabled(id, true));
    }

    @GetMapping("/jobs")
    public ResponseEntity<PageResponse<JobResponse>> jobs(
            @RequestParam(required = false) Boolean approved,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(adminService.jobs(approved, page, size));
    }

    @PatchMapping("/jobs/{id}/approve")
    public ResponseEntity<JobResponse> approve(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.approveJob(id));
    }

    @DeleteMapping("/jobs/{id}")
    public ResponseEntity<Void> deleteJob(@PathVariable Long id) {
        adminService.deleteJob(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/categories")
    public ResponseEntity<CategoryResponse> addCategory(@Valid @RequestBody CategoryRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.addCategory(req.getName()));
    }

    @DeleteMapping("/categories/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        adminService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
