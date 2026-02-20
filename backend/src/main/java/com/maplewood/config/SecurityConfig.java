package com.maplewood.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
public class SecurityConfig {

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http, JdbcTemplate jdbcTemplate)
                        throws Exception {
                http.cors(Customizer.withDefaults())
                                .csrf(csrf -> csrf.csrfTokenRepository(
                                                CookieCsrfTokenRepository.withHttpOnlyFalse()))
                                .sessionManagement(session -> session.sessionCreationPolicy(
                                                SessionCreationPolicy.IF_REQUIRED))
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers(HttpMethod.OPTIONS, "/**")
                                                .permitAll()
                                                .requestMatchers("/api/health").permitAll()
                                                .anyRequest().authenticated())
                                .formLogin(form -> form.loginProcessingUrl("/api/auth/login")
                                                .successHandler((request, response,
                                                                authentication) -> {
                                                        jdbcTemplate.update(
                                                                        "UPDATE users SET last_login_at = CURRENT_TIMESTAMP WHERE username = ?",
                                                                        authentication.getName());
                                                        response.setStatus(
                                                                        HttpServletResponse.SC_OK);
                                                })
                                                .failureHandler((request, response,
                                                                exception) -> response.setStatus(
                                                                                HttpServletResponse.SC_UNAUTHORIZED))
                                                .permitAll())
                                .logout(logout -> logout.logoutUrl("/api/auth/logout")
                                                .invalidateHttpSession(true)
                                                .deleteCookies("JSESSIONID")
                                                .logoutSuccessHandler((request, response,
                                                                authentication) -> response
                                                                                .setStatus(HttpServletResponse.SC_OK)))
                                .httpBasic(AbstractHttpConfigurer::disable);

                return http.build();
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }
}
