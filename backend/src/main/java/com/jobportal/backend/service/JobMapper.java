package com.jobportal.backend.service;

import com.jobportal.backend.dto.JobResponse;
import com.jobportal.backend.entity.Job;

public final class JobMapper {

    private JobMapper() {
    }

    public static JobResponse toResponse(Job j) {
        return JobResponse.builder()
                .id(j.getId())
                .title(j.getTitle())
                .description(j.getDescription())
                .location(j.getLocation())
                .salaryMin(j.getSalaryMin())
                .salaryMax(j.getSalaryMax())
                .type(j.getType())
                .experienceYears(j.getExperienceYears())
                .status(j.getStatus())
                .approved(j.isApproved())
                .categoryId(j.getCategory().getId())
                .categoryName(j.getCategory().getName())
                .companyId(j.getCompany().getId())
                .companyName(j.getCompany().getName())
                .createdAt(j.getCreatedAt())
                .build();
    }
}
