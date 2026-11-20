package com.jobportal.backend.dto;

import com.jobportal.backend.entity.JobType;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class JobRequest {
    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    @NotBlank(message = "Location is required")
    private String location;

    @PositiveOrZero(message = "Minimum salary cannot be negative")
    private Long salaryMin;

    @PositiveOrZero(message = "Maximum salary cannot be negative")
    private Long salaryMax;

    @NotNull(message = "Job type is required")
    private JobType type;

    @Min(value = 0, message = "Experience cannot be negative")
    private Integer experienceYears;

    @NotNull(message = "Category is required")
    private Long categoryId;
}
