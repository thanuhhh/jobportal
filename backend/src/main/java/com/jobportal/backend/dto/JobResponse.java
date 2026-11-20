package com.jobportal.backend.dto;

import com.jobportal.backend.entity.JobStatus;
import com.jobportal.backend.entity.JobType;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class JobResponse {
    private Long id;
    private String title;
    private String description;
    private String location;
    private Long salaryMin;
    private Long salaryMax;
    private JobType type;
    private Integer experienceYears;
    private JobStatus status;
    private Long categoryId;
    private String categoryName;
    private Long companyId;
    private String companyName;
    private LocalDateTime createdAt;
}
