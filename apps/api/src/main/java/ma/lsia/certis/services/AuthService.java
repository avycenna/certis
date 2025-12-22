package ma.lsia.certis.services;

import java.util.UUID;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import ma.lsia.certis.dto.AuthResponse;
import ma.lsia.certis.dto.LoginRequest;
import ma.lsia.certis.dto.UserResponse;
import ma.lsia.certis.entities.User;
import ma.lsia.certis.security.JwtUtil;

@Service
public class AuthService {
  private final UserService userService;
  private final PasswordEncoder passwordEncoder;
  private final JwtUtil jwtUtil;
  
  public AuthService(UserService userService, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
    this.userService = userService;
    this.passwordEncoder = passwordEncoder;
    this.jwtUtil = jwtUtil;
  }

  public AuthResponse login(@NonNull LoginRequest request) {
    String email = request.getEmail();
    String password = request.getPassword();
    
    if (email == null || password == null) {
      throw new BadCredentialsException("Email and password are required");
    }
    
    User user = userService.getUserByEmail(email)
        .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

    if (!passwordEncoder.matches(password, user.getPassword())) {
      throw new BadCredentialsException("Invalid email or password");
    }

    UUID userId = user.getId();
    if (userId == null) {
      throw new IllegalStateException("User ID cannot be null");
    }

    // Update last login
    userService.updateLastLogin(userId);

    // Generate JWT token with role
    String token = jwtUtil.generateToken(user.getEmail(), userId, user.getRole());

    // Return response with token and user info
    return new AuthResponse(token, UserResponse.fromUser(user));
  }

  public AuthResponse refreshToken(@NonNull String oldToken) {
    try {
      // Extract user information from the old token
      String email = jwtUtil.extractEmail(oldToken);
      UUID userId = jwtUtil.extractUserId(oldToken);
      
      if (email == null || userId == null) {
        throw new BadCredentialsException("Invalid token");
      }
      
      // Verify user still exists
      User user = userService.getUserByEmail(email)
          .orElseThrow(() -> new BadCredentialsException("User not found"));
      
      // Generate new JWT token with fresh expiration and current role
      String newToken = jwtUtil.generateToken(user.getEmail(), userId, user.getRole());
      
      // Return response with new token and user info
      return new AuthResponse(newToken, UserResponse.fromUser(user));
    } catch (Exception e) {
      throw new BadCredentialsException("Invalid or expired token");
    }
  }
}
