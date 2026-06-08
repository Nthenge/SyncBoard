package com.eclectics.collaboration.Tool.configuration;

import com.eclectics.collaboration.Tool.enums.OveralRole;
import com.eclectics.collaboration.Tool.model.User;
import com.eclectics.collaboration.Tool.repository.UserRespository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
@Component
@RequiredArgsConstructor
@Slf4j
public class AdminSeeder implements CommandLineRunner {

    private final UserRespository userRespository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.one.email}") private String adminOneEmail;
    @Value("${admin.one.password}") private String adminOnePassword;
    @Value("${admin.two.email}") private String adminTwoEmail;
    @Value("${admin.two.password}") private String adminTwoPassword;

    @Override
    public void run(String... args) {
        seedAdmin(adminOneEmail, "Sheilah", adminOnePassword);
        seedAdmin(adminTwoEmail, "Abraham", adminTwoPassword);
    }

    private void seedAdmin(String email, String firstName, String password) {
        if (userRespository.findByEmail(email).isEmpty()) {
            User admin = new User();
            admin.setFirstName(firstName);
            admin.setSirName("Admin");
            admin.setEmail(email);
            admin.setPassword(passwordEncoder.encode(password));
            admin.setRole(OveralRole.ADMIN);
            admin.setEnabled(true);
            admin.setCreatedAt(LocalDateTime.now());

            userRespository.save(admin);
            log.info(">>> Admin seeded: {}", email);
        } else {
            log.info(">>> Admin already exists, skipping: {}", email);
        }
    }
}
