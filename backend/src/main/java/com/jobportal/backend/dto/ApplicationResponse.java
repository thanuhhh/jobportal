package com.jobportal.backend.dto;

import com.jobportal.backend.entity.ApplicationStatus;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class ApplicationResponse {
    private Long id;
    private Long jobId;
    private String jobTitle;
    private String companyName;
    private ApplicationStatus status;
    private LocalDateTime appliedAt;
}
