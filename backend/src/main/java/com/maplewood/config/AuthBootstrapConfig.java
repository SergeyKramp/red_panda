package com.maplewood.config;

import com.maplewood.repositories.AppUserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableConfigurationProperties(AuthBootstrapProperties.class)
public class AuthBootstrapConfig {

    @Bean
    public CommandLineRunner ensureBootstrapUser(
            AppUserRepository appUserRepository,
            JdbcTemplate jdbcTemplate,
            PasswordEncoder passwordEncoder,
            AuthBootstrapProperties authBootstrapProperties) {
        return args -> {
            String username = authBootstrapProperties.getUsername();
            String password = authBootstrapProperties.getPassword();
            String role = authBootstrapProperties.getRole();

            boolean userExists = appUserRepository.findByUsername(username).isPresent();
            if (userExists) {
                return;
            }

            String passwordHash = passwordEncoder.encode(password);

            jdbcTemplate.update(
                    "INSERT INTO users (username, password_hash, role, enabled, created_at) VALUES (?, ?, ?, 1, CURRENT_TIMESTAMP)",
                    username,
                    passwordHash,
                    role);
        };
    }
}
