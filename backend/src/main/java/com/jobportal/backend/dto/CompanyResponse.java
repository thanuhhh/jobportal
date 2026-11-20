package com.jobportal.backend.dto;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
public class CompanyResponse {
    private Long id;
    private String name;
    private String description;
    private String website;
    private String location;
}
