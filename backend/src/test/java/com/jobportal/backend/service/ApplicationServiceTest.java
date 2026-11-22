package com.jobportal.backend.service;

import com.jobportal.backend.dto.ApplicationResponse;
import com.jobportal.backend.entity.Application;
import com.jobportal.backend.entity.ApplicationStatus;
import com.jobportal.backend.entity.Company;
import com.jobportal.backend.entity.Job;
import com.jobportal.backend.entity.JobStatus;
import com.jobportal.backend.entity.Role;
import com.jobportal.backend.entity.User;
import com.jobportal.backend.exception.ResourceNotFoundException;
import com.jobportal.backend.repository.ApplicationRepository;
import com.jobportal.backend.repository.JobRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApplicationServiceTest {

    @Mock private ApplicationRepository applicationRepository;
    @Mock private JobRepository jobRepository;
    @Mock private CurrentUserService currentUserService;
    @Mock private EmailService emailService;
    @InjectMocks private ApplicationService applicationService;

    private User seeker;
    private Job job;

    @BeforeEach
    void setUp() {
        seeker = User.builder().id(1L).name("Ravi").email("ravi@test.com").role(Role.JOB_SEEKER).build();
        User recruiter = User.builder().id(2L).name("Priya").email("priya@company.com").role(Role.RECRUITER).build();
        Company company = Company.builder().id(1L).name("TechNova").recruiter(recruiter).build();
        job = Job.builder().id(10L).title("Java Developer").company(company)
                .status(JobStatus.OPEN).approved(true).build();
    }

    @Test
    void applySavesApplicationAndSendsEmails() {
        when(currentUserService.getCurrentUser()).thenReturn(seeker);
        when(jobRepository.findById(10L)).thenReturn(Optional.of(job));
        when(applicationRepository.existsByJobIdAndSeekerId(10L, 1L)).thenReturn(false);
        when(applicationRepository.save(any(Application.class))).thenAnswer(inv -> inv.getArgument(0));

        ApplicationResponse res = applicationService.apply(10L, "Hello");

        assertEquals(ApplicationStatus.APPLIED, res.getStatus());
        assertEquals("Java Developer", res.getJobTitle());
        verify(emailService, times(2)).send(any(), any(), any());
    }

    @Test
    void applyTwiceIsRejected() {
        when(currentUserService.getCurrentUser()).thenReturn(seeker);
        when(jobRepository.findById(10L)).thenReturn(Optional.of(job));
        when(applicationRepository.existsByJobIdAndSeekerId(10L, 1L)).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> applicationService.apply(10L, null));

        assertEquals("You have already applied to this job", ex.getMessage());
        verify(applicationRepository, never()).save(any());
    }

    @Test
    void applyToClosedJobIsRejected() {
        job.setStatus(JobStatus.CLOSED);
        when(currentUserService.getCurrentUser()).thenReturn(seeker);
        when(jobRepository.findById(10L)).thenReturn(Optional.of(job));

        assertThrows(IllegalArgumentException.class, () -> applicationService.apply(10L, null));
        verify(applicationRepository, never()).save(any());
    }

    @Test
    void applyToUnapprovedJobIsRejected() {
        job.setApproved(false);
        when(currentUserService.getCurrentUser()).thenReturn(seeker);
        when(jobRepository.findById(10L)).thenReturn(Optional.of(job));

        assertThrows(IllegalArgumentException.class, () -> applicationService.apply(10L, null));
    }

    @Test
    void applyToMissingJobGivesNotFound() {
        when(currentUserService.getCurrentUser()).thenReturn(seeker);
        when(jobRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> applicationService.apply(99L, null));
    }
}
