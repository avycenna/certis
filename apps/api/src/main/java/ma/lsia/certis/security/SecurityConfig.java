package ma.lsia.certis.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
  
  private final JwtFilter jwtAuthenticationFilter;
  private final CorsConfigurationSource corsConfigurationSource;
  
  public SecurityConfig(@Lazy JwtFilter jwtAuthenticationFilter, CorsConfigurationSource corsConfigurationSource) {
    this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    this.corsConfigurationSource = corsConfigurationSource;
  }
  
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
      .cors(cors -> cors.configurationSource(corsConfigurationSource)) // Enable CORS
      .csrf(csrf -> csrf.disable()) // Disable CSRF for REST API
      .sessionManagement(session -> session
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Use stateless sessions (JWT)
      .authorizeHttpRequests(auth -> auth
        // Public endpoints (no authentication required)
        .requestMatchers("/auth/register", "/auth/login").permitAll()
        .requestMatchers("/invitations/accept").permitAll() // Public invitation acceptance
        .requestMatchers("/h2-console/**").permitAll() // H2 Console (dev only)
        .requestMatchers("/docs/**", "/docs/api/**", "/swagger-ui/**", "/v3/api-docs/**").permitAll() // Swagger
        .requestMatchers("/**").permitAll() // dev only
        
        // Protected endpoints (authentication required)
        // For owner-only access, use SecurityUtil in your service/controller
        // .anyRequest().authenticated() // Uncomment in production
      )
      .headers(headers -> headers
        .frameOptions(frame -> frame.sameOrigin())) // Allow H2 console frames
      .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class); // Add JWT filter

    return http.build();
  }
}
