package com.jobportal.backend.dto;

import com.jobportal.backend.entity.ApplicationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StatusUpdateRequest {
    @NotNull(message = "Status is required")
    private ApplicationStatus status;
}
