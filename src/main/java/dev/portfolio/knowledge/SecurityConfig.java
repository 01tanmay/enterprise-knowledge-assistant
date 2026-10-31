package dev.portfolio.knowledge;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.filter.OncePerRequestFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
  @Bean
  SecurityFilterChain security(HttpSecurity http, @Value("${app.api-key}") String expected) throws Exception {
    if (expected == null || expected.isBlank())
      throw new IllegalArgumentException("APP_API_KEY must be configured");
    return http.csrf(c -> c.disable())
      .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
      .authorizeHttpRequests(a -> a.anyRequest().authenticated())
      .addFilterBefore(new OncePerRequestFilter() {
        @Override protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res,
            FilterChain chain) throws ServletException, IOException {
          byte[] actual = req.getHeader("X-API-Key") == null ? new byte[0]
            : req.getHeader("X-API-Key").getBytes(StandardCharsets.UTF_8);
          if (!MessageDigest.isEqual(expected.getBytes(StandardCharsets.UTF_8), actual)) {
            res.sendError(401, "Invalid API key");
            return;
          }
          chain.doFilter(req, res);
        }
      }, org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class)
      .build();
  }
}
