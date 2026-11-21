package com.jobportal.backend.config;

import com.jobportal.backend.entity.Role;
import com.jobportal.backend.entity.User;
import com.jobportal.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminSeeder {

    @Bean
    CommandLineRunner seedAdmin(UserRepository repo,
                                PasswordEncoder encoder,
                                @Value("${app.admin.email:}") String email,
                                @Value("${app.admin.password:}") String password) {
        return args -> {
            if (email.isBlank() || password.isBlank() || repo.existsByEmail(email)) {
                return;
            }
            repo.save(User.builder()
                    .name("Administrator")
                    .email(email)
                    .password(encoder.encode(password))
                    .role(Role.ADMIN)
                    .build());
        };
    }
}
