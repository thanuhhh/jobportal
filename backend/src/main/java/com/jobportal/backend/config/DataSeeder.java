package com.jobportal.backend.config;

import com.jobportal.backend.entity.Category;
import com.jobportal.backend.repository.CategoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedCategories(CategoryRepository repo) {
        return args -> {
            if (repo.count() == 0) {
                List.of("Software Development", "Data Science", "Design",
                        "Marketing", "Sales", "Finance", "Human Resources", "Customer Support")
                        .forEach(name -> repo.save(Category.builder().name(name).build()));
            }
        };
    }
}
