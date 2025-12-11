package ma.lsia.certis.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import ma.lsia.certis.dto.UpdateUserRequest;
import ma.lsia.certis.dto.UserResponse;
import ma.lsia.certis.entities.User;
import ma.lsia.certis.exception.UnauthorizedException;
import ma.lsia.certis.services.UserService;
import ma.lsia.certis.util.SecurityUtil;

@RestController
@RequestMapping("/users")
public class UserController {
  
  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  /**
   * Get current user profile
   */
  @GetMapping("/me")
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
   * Get user by ID (owner-only)
   */
  @GetMapping("/{id}")
  public ResponseEntity<UserResponse> getUserById(@PathVariable @NonNull Long id) {
    String currentUserEmail = SecurityUtil.getCurrentUserEmail();
    if (currentUserEmail == null) {
      throw new UnauthorizedException("User not authenticated");
    }
    
    User user = userService.getUserById(id)
        .orElseThrow(() -> new UnauthorizedException("User not found"));
    
    // Only allow users to view their own profile
    if (!user.getEmail().equals(currentUserEmail)) {
      throw new UnauthorizedException("You can only view your own profile");
    }
    
    return ResponseEntity.ok(UserResponse.fromUser(user));
  }

  /**
   * Update current user profile
   */
  @PutMapping("/me")
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
   */
  @DeleteMapping("/me")
  public ResponseEntity<Void> deleteCurrentUser() {
    String userEmail = SecurityUtil.getCurrentUserEmail();
    if (userEmail == null) {
      throw new UnauthorizedException("User not authenticated");
    }
    
    User user = userService.getUserByEmail(userEmail)
        .orElseThrow(() -> new UnauthorizedException("User not found"));

    Long userId = user.getId();
    if (userId == null) {
      throw new UnauthorizedException("User ID not found");
    }
    
    userService.deleteUser(userId);
    return ResponseEntity.noContent().build();
  }
}
