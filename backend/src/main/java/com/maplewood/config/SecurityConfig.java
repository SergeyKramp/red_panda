package com.maplewood.config;

import java.io.IOException;
import java.util.function.Supplier;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
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
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.csrf.CsrfTokenRequestHandler;
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Configuration
public class SecurityConfig {

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http, JdbcTemplate jdbcTemplate)
                        throws Exception {
                http.cors(Customizer.withDefaults()).csrf(csrf -> csrf
                                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                                .csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler())
                                .ignoringRequestMatchers("/api/auth/login", "/api/auth/logout"))
                                .sessionManagement(session -> session.sessionCreationPolicy(
                                                SessionCreationPolicy.IF_REQUIRED))
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers(HttpMethod.OPTIONS, "/**")
                                                .permitAll()
                                                .requestMatchers("/api/health", "/api/auth/login")
                                                .permitAll().anyRequest().authenticated())
                                .exceptionHandling(ex -> ex.defaultAuthenticationEntryPointFor(
                                                new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                                                new AntPathRequestMatcher("/api/**")))
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
                                .httpBasic(AbstractHttpConfigurer::disable)
                                .addFilterAfter(new CsrfCookieFilter(), BasicAuthenticationFilter.class);

                return http.build();
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        private static final class SpaCsrfTokenRequestHandler implements CsrfTokenRequestHandler {
                private final CsrfTokenRequestHandler plain = new CsrfTokenRequestAttributeHandler();
                private final CsrfTokenRequestHandler xor = new XorCsrfTokenRequestAttributeHandler();

                @Override
                public void handle(HttpServletRequest request, HttpServletResponse response,
                                Supplier<CsrfToken> csrfToken) {
                        this.xor.handle(request, response, csrfToken);
                }

                @Override
                public String resolveCsrfTokenValue(HttpServletRequest request, CsrfToken csrfToken) {
                        if (StringUtils.hasText(request.getHeader(csrfToken.getHeaderName()))) {
                                return this.plain.resolveCsrfTokenValue(request, csrfToken);
                        }
                        return this.xor.resolveCsrfTokenValue(request, csrfToken);
                }
        }

        private static final class CsrfCookieFilter extends OncePerRequestFilter {
                @Override
                protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                FilterChain filterChain)
                                throws ServletException, IOException {
                        var csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
                        if (csrfToken != null) {
                                csrfToken.getToken();
                        }

                        filterChain.doFilter(request, response);
                }
        }
}
