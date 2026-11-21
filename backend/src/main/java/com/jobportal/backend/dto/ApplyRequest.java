package com.jobportal.backend.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ApplyRequest {
    @Size(max = 2000, message = "Cover letter is too long")
    private String coverLetter;
}
