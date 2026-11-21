package com.jobportal.backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "job_seeker_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobSeekerProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    private String phone;

    @Column(length = 1000)
    private String skills;

    @Column(length = 500)
    private String education;

    private Integer experienceYears;

    @Column(length = 2000)
    private String summary;

    private String resumePath;
}
