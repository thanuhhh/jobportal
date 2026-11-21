package com.jobportal.backend.dto;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
public class ProfileResponse {
    private String name;
    private String email;
    private String phone;
    private String skills;
    private String education;
    private Integer experienceYears;
    private String summary;
    private String resumePath;
}
