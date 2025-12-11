package ma.lsia.certis.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import ma.lsia.certis.dto.AuthResponse;
import ma.lsia.certis.dto.LoginRequest;
import ma.lsia.certis.dto.RegisterRequest;
import ma.lsia.certis.dto.UserResponse;
import ma.lsia.certis.entities.User;
import ma.lsia.certis.services.AuthService;
import ma.lsia.certis.services.UserService;

@RestController
@RequestMapping("/auth")
public class AuthController {
  private final UserService userService;
  private final AuthService authService;

  public AuthController(UserService userService, AuthService authService) {
    this.userService = userService;
    this.authService = authService;
  }

  @PostMapping("/register")
  public ResponseEntity<UserResponse> registerUser(@Valid @NonNull @RequestBody RegisterRequest request) {
    User user = userService.createUser(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(UserResponse.fromUser(user));
  }

  @PostMapping("/login")
  public ResponseEntity<AuthResponse> login(@Valid @NonNull @RequestBody LoginRequest request) {
    AuthResponse response = authService.login(request);
    return ResponseEntity.ok(response);
  }
}
