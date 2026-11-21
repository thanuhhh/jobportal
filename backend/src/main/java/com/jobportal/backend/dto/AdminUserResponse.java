package com.jobportal.backend.dto;

import com.jobportal.backend.entity.Role;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class AdminUserResponse {
    private Long id;
    private String name;
    private String email;
    private Role role;
    private boolean enabled;
    private LocalDateTime createdAt;
}
