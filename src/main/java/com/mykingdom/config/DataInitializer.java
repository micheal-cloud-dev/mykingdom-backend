package com.mykingdom.config;

import com.mykingdom.model.User;
import com.mykingdom.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Create default admin if not exists
        if (!userRepository.existsByUsername("admin")) {
            User admin = User.builder()
                    .username("admin")
                    .email("admin@mykingdom.com")
                    .password(passwordEncoder.encode("Admin@123"))
                    .role(User.Role.ADMIN)
                    .isActive(true)
                    .build();
            userRepository.save(admin);
            log.info("✅ Default admin created — username: admin | password: Admin@123");
        }

        // Create default teacher if not exists
        if (!userRepository.existsByUsername("teacher1")) {
            User teacher = User.builder()
                    .username("teacher1")
                    .email("teacher1@mykingdom.com")
                    .password(passwordEncoder.encode("Teacher@123"))
                    .role(User.Role.TEACHER)
                    .isActive(true)
                    .build();
            userRepository.save(teacher);
            log.info("✅ Default teacher created — username: teacher1 | password: Teacher@123");
        }

        log.info("🏰 My Kingdom Backend is running!");
    }
}
