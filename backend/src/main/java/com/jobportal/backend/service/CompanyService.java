package com.jobportal.backend.service;

import com.jobportal.backend.dto.CompanyRequest;
import com.jobportal.backend.dto.CompanyResponse;
import com.jobportal.backend.entity.Company;
import com.jobportal.backend.entity.User;
import com.jobportal.backend.exception.ResourceNotFoundException;
import com.jobportal.backend.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final CurrentUserService currentUserService;

    @Transactional
    public CompanyResponse saveMyCompany(CompanyRequest req) {
        User recruiter = currentUserService.getCurrentUser();

        Company company = companyRepository.findByRecruiterId(recruiter.getId())
                .orElseGet(() -> Company.builder().recruiter(recruiter).build());

        company.setName(req.getName());
        company.setDescription(req.getDescription());
        company.setWebsite(req.getWebsite());
        company.setLocation(req.getLocation());

        return toResponse(companyRepository.save(company));
    }

    @Transactional(readOnly = true)
    public CompanyResponse getMyCompany() {
        User recruiter = currentUserService.getCurrentUser();
        Company company = companyRepository.findByRecruiterId(recruiter.getId())
                .orElseThrow(() -> new ResourceNotFoundException("You have not created a company profile yet"));
        return toResponse(company);
    }

    private CompanyResponse toResponse(Company c) {
        return CompanyResponse.builder()
                .id(c.getId())
                .name(c.getName())
                .description(c.getDescription())
                .website(c.getWebsite())
                .location(c.getLocation())
                .build();
    }
}
