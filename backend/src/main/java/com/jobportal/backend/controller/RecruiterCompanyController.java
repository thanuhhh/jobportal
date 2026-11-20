package com.jobportal.backend.controller;

import com.jobportal.backend.dto.CompanyRequest;
import com.jobportal.backend.dto.CompanyResponse;
import com.jobportal.backend.service.CompanyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recruiter/company")
@RequiredArgsConstructor
public class RecruiterCompanyController {

    private final CompanyService companyService;

    @PutMapping
    public ResponseEntity<CompanyResponse> save(@Valid @RequestBody CompanyRequest req) {
        return ResponseEntity.ok(companyService.saveMyCompany(req));
    }

    @GetMapping
    public ResponseEntity<CompanyResponse> get() {
        return ResponseEntity.ok(companyService.getMyCompany());
    }
}
