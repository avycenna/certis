package ma.lsia.certis.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Tag(name = "Authentication", description = "User registration and login endpoints")
public class AuthController {
  private final UserService userService;
  private final AuthService authService;

  public AuthController(UserService userService, AuthService authService) {
    this.userService = userService;
    this.authService = authService;
  }

  @Operation(summary = "Register a new user", description = "Create a new user account with email and password")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "201", description = "User created successfully",
      content = @Content(schema = @Schema(implementation = UserResponse.class))),
    @ApiResponse(responseCode = "400", description = "Invalid input or email already exists")
  })
  @PostMapping("/register")
  public ResponseEntity<UserResponse> registerUser(@Valid @NonNull @RequestBody RegisterRequest request) {
    User user = userService.createUser(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(UserResponse.fromUser(user));
  }

  @Operation(summary = "Login", description = "Authenticate user and receive JWT token")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Login successful",
      content = @Content(schema = @Schema(implementation = AuthResponse.class))),
    @ApiResponse(responseCode = "401", description = "Invalid credentials")
  })
  @PostMapping("/login")
  public ResponseEntity<AuthResponse> login(@Valid @NonNull @RequestBody LoginRequest request) {
    AuthResponse response = authService.login(request);
    return ResponseEntity.ok(response);
  }

  @Operation(summary = "Refresh token", description = "Refresh an expired JWT token to get a new one")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Token refreshed successfully",
      content = @Content(schema = @Schema(implementation = AuthResponse.class))),
    @ApiResponse(responseCode = "401", description = "Invalid or expired token")
  })
  @PostMapping("/refresh")
  public ResponseEntity<AuthResponse> refreshToken(@Valid @NonNull @RequestBody String oldToken) {
    AuthResponse response = authService.refreshToken(oldToken);
    return ResponseEntity.ok(response);
  }
}
