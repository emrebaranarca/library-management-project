package com.example.library_management.config;

import com.example.library_management.models.User;
import com.example.library_management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationContext applicationContext;


    @Bean
    @DependsOn("flyway")
    public CommandLineRunner initData(Flyway flyway) {
        return args -> {
            logBeans();
            // Create default admin if no users exist
            if (userRepository.count() == 0) {
                User adminUser = User.builder()
                        .id(UUID.randomUUID().toString())
                        .name("Admin User")
                        .email("admin@library.com")
                        .password(passwordEncoder.encode("admin123"))
                        .role(User.Role.ADMIN)
                        .createdAt(LocalDateTime.now())
                        .build();

                userRepository.save(adminUser);

                log.info("==============================================");
                log.info("Default admin user created:");
                log.info("Email: admin@library.com");
                log.info("Password: admin123");
                log.info("==============================================");
            }
        };
    }

    private void logBeans() {
        String[] beanNames = applicationContext.getBeanDefinitionNames();

        log.info("==============================================");
        log.info("Proje Bean'leri:");
        log.info("==============================================");

        Arrays.stream(beanNames)
                .filter(name -> {
                    Object bean = applicationContext.getBean(name);
                    String packageName = bean.getClass().getPackageName();
                    return packageName.startsWith("com.example.library");
                })
                .sorted()
                .forEach(name -> log.info("Bean: {}", name));
    };

}
