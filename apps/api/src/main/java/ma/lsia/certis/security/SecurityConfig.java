package ma.lsia.certis.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
  
  private final JwtFilter jwtAuthenticationFilter;
  
  public SecurityConfig(@Lazy JwtFilter jwtAuthenticationFilter) {
    this.jwtAuthenticationFilter = jwtAuthenticationFilter;
  }
  
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
      .csrf(csrf -> csrf.disable()) // Disable CSRF for REST API
      .sessionManagement(session -> session
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Use stateless sessions (JWT)
      .authorizeHttpRequests(auth -> auth
        // Public endpoints (no authentication required)
        .requestMatchers("/auth/register", "/auth/login").permitAll()
        .requestMatchers("/h2-console/**").permitAll() // H2 Console (dev only)
        .requestMatchers("/docs/**", "/docs/api/**").permitAll() // Swagger
        
        // Protected endpoints (authentication required)
        // For owner-only access, use SecurityUtil in your service/controller
        .anyRequest().authenticated()
      )
      .headers(headers -> headers
        .frameOptions(frame -> frame.sameOrigin())) // Allow H2 console frames
      .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class); // Add JWT filter

    return http.build();
  }
}
