package com.jobportal.backend.dto;

import com.jobportal.backend.entity.ApplicationStatus;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class ApplicantResponse {
    private Long applicationId;
    private Long jobId;
    private String jobTitle;
    private String seekerName;
    private String seekerEmail;
    private String phone;
    private String skills;
    private String education;
    private Integer experienceYears;
    private String coverLetter;
    private ApplicationStatus status;
    private boolean hasResume;
    private LocalDateTime appliedAt;
}
