package ma.lsia.certis.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import ma.lsia.certis.enums.Role;
import ma.lsia.certis.services.UserService;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtFilter extends OncePerRequestFilter {

  private final JwtUtil jwtUtil;
  private final UserService userService;

  public JwtFilter(JwtUtil jwtUtil, UserService userService) {
    this.jwtUtil = jwtUtil;
    this.userService = userService;
  }

  @Override
  protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain chain)
      throws ServletException, IOException {
    
    if (request == null || response == null || chain == null) {
      throw new IllegalArgumentException("Request, Response, and FilterChain must not be null");
    }

    final String authHeader = request.getHeader("Authorization");

    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      chain.doFilter(request, response);
      return;
    }

    try {
      final String token = authHeader.substring(7);
      final String email = jwtUtil.extractEmail(token);

      if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
        ma.lsia.certis.entities.User user = userService.getUserByEmail(email).orElse(null);

        if (user != null && jwtUtil.validateToken(token, email)) {
          // Extract role from token
          Role role = jwtUtil.extractRole(token);
          
          // Create authorities with ROLE_ prefix for Spring Security
          UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
              user, // Use actual User entity as principal (not UserDetails)
              null, 
              Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.name())));
          authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
          SecurityContextHolder.getContext().setAuthentication(authToken);
        }
      }
    } catch (Exception e) {
      // Token invalid, continue without authentication
    }

    chain.doFilter(request, response);
  }
}
