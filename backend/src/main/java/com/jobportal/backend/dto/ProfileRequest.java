package com.jobportal.backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProfileRequest {
    @Size(max = 20, message = "Phone is too long")
    private String phone;

    @Size(max = 1000, message = "Skills text is too long")
    private String skills;

    @Size(max = 500, message = "Education text is too long")
    private String education;

    @Min(value = 0, message = "Experience cannot be negative")
    private Integer experienceYears;

    @Size(max = 2000, message = "Summary is too long")
    private String summary;
}
