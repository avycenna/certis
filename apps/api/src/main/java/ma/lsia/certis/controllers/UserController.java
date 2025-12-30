package ma.lsia.certis.controllers;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.lsia.certis.dto.UpdateUserRequest;
import ma.lsia.certis.dto.UserResponse;
import ma.lsia.certis.entities.User;
import ma.lsia.certis.exception.UnauthorizedException;
import ma.lsia.certis.services.UserService;
import ma.lsia.certis.util.SecurityUtil;

@RestController
@RequestMapping("/api/me")
@Tag(name = "User Management", description = "User profile operations (requires authentication)")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class UserController {
  private final UserService userService;

  /**
   * Get current user profile
   * @return ResponseEntity<UserResponse>
   */
  @Operation(summary = "Get current user profile", description = "Retrieve the authenticated user's profile information")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "User profile retrieved successfully",
      content = @Content(schema = @Schema(implementation = UserResponse.class))),
    @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token")
  })
  @GetMapping
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<UserResponse> getCurrentUser() {
    String userEmail = SecurityUtil.getCurrentUserEmail();
    if (userEmail == null) {
      throw new UnauthorizedException("User not authenticated");
    }
    
    User user = userService.getUserByEmail(userEmail)
        .orElseThrow(() -> new UnauthorizedException("User not found"));
    return ResponseEntity.ok(UserResponse.fromUser(user));
  }


  /**
   * Update current user profile
   * @param UpdateUserRequest request
   * @return ResponseEntity<UserResponse>
   */
  @Operation(summary = "Update user profile", description = "Update the authenticated user's profile information")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Profile updated successfully",
      content = @Content(schema = @Schema(implementation = UserResponse.class))),
    @ApiResponse(responseCode = "400", description = "Invalid input"),
    @ApiResponse(responseCode = "401", description = "Unauthorized")
  })
  @PutMapping
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<UserResponse> updateCurrentUser(@Valid @NonNull @RequestBody UpdateUserRequest request) {
    String userEmail = SecurityUtil.getCurrentUserEmail();
    if (userEmail == null) {
      throw new UnauthorizedException("User not authenticated");
    }
    
    User user = userService.getUserByEmail(userEmail)
        .orElseThrow(() -> new UnauthorizedException("User not found"));

    if (user == null) {
      throw new UnauthorizedException("User not found");
    }
    
    // Update user fields
    if (request.getFirstName() != null) {
      user.setFirstName(request.getFirstName());
    }
    if (request.getLastName() != null) {
      user.setLastName(request.getLastName());
    }
    
    User updatedUser = userService.updateUser(user);
    return ResponseEntity.ok(UserResponse.fromUser(updatedUser));
  }


  /**
   * Delete current user account
   * @return ResponseEntity<Void>
   */
  @Operation(summary = "Delete user account", description = "Permanently delete the authenticated user's account")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "204", description = "Account deleted successfully"),
    @ApiResponse(responseCode = "401", description = "Unauthorized")
  })
  @DeleteMapping
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<Void> deleteCurrentUser() {
    String userEmail = SecurityUtil.getCurrentUserEmail();
    if (userEmail == null) {
      throw new UnauthorizedException("User not authenticated");
    }
    
    User user = userService.getUserByEmail(userEmail)
        .orElseThrow(() -> new UnauthorizedException("User not found"));

    UUID userId = user.getId();
    if (userId == null) {
      throw new UnauthorizedException("User ID not found");
    }
    
    userService.deleteUser(userId);
    return ResponseEntity.noContent().build();
  }
}
