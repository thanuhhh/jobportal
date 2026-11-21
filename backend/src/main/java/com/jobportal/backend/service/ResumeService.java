package com.jobportal.backend.service;

import com.jobportal.backend.dto.ProfileResponse;
import com.jobportal.backend.entity.Application;
import com.jobportal.backend.entity.JobSeekerProfile;
import com.jobportal.backend.entity.User;
import com.jobportal.backend.exception.ResourceNotFoundException;
import com.jobportal.backend.repository.ApplicationRepository;
import com.jobportal.backend.repository.JobSeekerProfileRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class ResumeService {

    private final JobSeekerProfileRepository profileRepository;
    private final ApplicationRepository applicationRepository;
    private final CurrentUserService currentUserService;
    private final Path uploadDir;

    public ResumeService(JobSeekerProfileRepository profileRepository,
                         ApplicationRepository applicationRepository,
                         CurrentUserService currentUserService,
                         @Value("${app.upload-dir:uploads}") String dir) {
        this.profileRepository = profileRepository;
        this.applicationRepository = applicationRepository;
        this.currentUserService = currentUserService;
        this.uploadDir = Paths.get(dir, "resumes").toAbsolutePath().normalize();
    }

    @Transactional
    public ProfileResponse upload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Please choose a PDF file");
        }
        String original = file.getOriginalFilename();
        if (original == null || !original.toLowerCase().endsWith(".pdf")) {
            throw new IllegalArgumentException("Only PDF files are allowed");
        }

        try {
            byte[] head;
            try (InputStream in = file.getInputStream()) {
                head = in.readNBytes(4);
            }
            if (head.length < 4 || !new String(head, StandardCharsets.US_ASCII).equals("%PDF")) {
                throw new IllegalArgumentException("The file is not a valid PDF");
            }

            User me = currentUserService.getCurrentUser();
            JobSeekerProfile profile = profileRepository.findByUserId(me.getId())
                    .orElseGet(() -> JobSeekerProfile.builder().user(me).build());

            Files.createDirectories(uploadDir);
            String stored = UUID.randomUUID() + ".pdf";
            try (InputStream in = file.getInputStream()) {
                Files.copy(in, uploadDir.resolve(stored), StandardCopyOption.REPLACE_EXISTING);
            }

            String old = profile.getResumePath();
            profile.setResumePath(stored);
            profileRepository.save(profile);

            if (old != null) {
                Files.deleteIfExists(uploadDir.resolve(old).normalize());
            }

            return ProfileResponse.builder()
                    .name(me.getName())
                    .email(me.getEmail())
                    .phone(profile.getPhone())
                    .skills(profile.getSkills())
                    .education(profile.getEducation())
                    .experienceYears(profile.getExperienceYears())
                    .summary(profile.getSummary())
                    .resumePath(profile.getResumePath())
                    .build();
        } catch (IOException e) {
            throw new IllegalStateException("Could not store the file", e);
        }
    }

    @Transactional(readOnly = true)
    public Resource loadForRecruiter(Long applicationId) {
        Application app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));
        User me = currentUserService.getCurrentUser();
        if (!app.getJob().getCompany().getRecruiter().getId().equals(me.getId())) {
            throw new AccessDeniedException("Not your applicant");
        }

        JobSeekerProfile profile = profileRepository.findByUserId(app.getSeeker().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Applicant has not uploaded a resume"));
        if (profile.getResumePath() == null) {
            throw new ResourceNotFoundException("Applicant has not uploaded a resume");
        }

        Path file = uploadDir.resolve(profile.getResumePath()).normalize();
        if (!file.startsWith(uploadDir) || !Files.exists(file)) {
            throw new ResourceNotFoundException("Resume file not found");
        }
        return new FileSystemResource(file);
    }
}
