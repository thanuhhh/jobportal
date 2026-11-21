package com.jobportal.backend.service;

import com.jobportal.backend.dto.ProfileRequest;
import com.jobportal.backend.dto.ProfileResponse;
import com.jobportal.backend.entity.JobSeekerProfile;
import com.jobportal.backend.entity.User;
import com.jobportal.backend.repository.JobSeekerProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final JobSeekerProfileRepository profileRepository;
    private final CurrentUserService currentUserService;

    @Transactional
    public ProfileResponse saveMyProfile(ProfileRequest req) {
        User me = currentUserService.getCurrentUser();
        JobSeekerProfile profile = profileRepository.findByUserId(me.getId())
                .orElseGet(() -> JobSeekerProfile.builder().user(me).build());

        profile.setPhone(req.getPhone());
        profile.setSkills(req.getSkills());
        profile.setEducation(req.getEducation());
        profile.setExperienceYears(req.getExperienceYears());
        profile.setSummary(req.getSummary());

        return toResponse(profileRepository.save(profile), me);
    }

    @Transactional(readOnly = true)
    public ProfileResponse getMyProfile() {
        User me = currentUserService.getCurrentUser();
        return profileRepository.findByUserId(me.getId())
                .map(p -> toResponse(p, me))
                .orElseGet(() -> ProfileResponse.builder()
                        .name(me.getName())
                        .email(me.getEmail())
                        .build());
    }

    private ProfileResponse toResponse(JobSeekerProfile p, User me) {
        return ProfileResponse.builder()
                .name(me.getName())
                .email(me.getEmail())
                .phone(p.getPhone())
                .skills(p.getSkills())
                .education(p.getEducation())
                .experienceYears(p.getExperienceYears())
                .summary(p.getSummary())
                .resumePath(p.getResumePath())
                .build();
    }
}
