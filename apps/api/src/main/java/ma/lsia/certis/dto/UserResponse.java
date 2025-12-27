package ma.lsia.certis.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ma.lsia.certis.entities.User;
import ma.lsia.certis.enums.Role;

import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response payload containing user details")
public class UserResponse {
  @Schema(description = "Unique identifier of the user", example = "b3b7c8e2-1d2a-4c3e-9f2e-123456789abc")
  private UUID id;

  @Schema(description = "User's first name", example = "John")
  private String firstName;

  @Schema(description = "User's last name", example = "Doe")
  private String lastName;

  @Schema(description = "User's email address", example = "john.doe@example.com")
  private String email;

  @Schema(description = "Role of the user", example = "USER")
  private Role role;

  @Schema(description = "Unique identifier of the user's organization", example = "b3b7c8e2-1d2a-4c3e-9f2e-123456789abc")
  private UUID organizationId;

  @Schema(description = "Date and time when the user was verified (ISO 8601 format)", example = "2025-01-01T00:00:00")
  private LocalDateTime isVerified;

  @Schema(description = "Date and time when the user joined (ISO 8601 format)", example = "2025-01-01T00:00:00")
  private LocalDateTime joinedAt;

  @Schema(description = "Date and time when the user was created (ISO 8601 format)", example = "2025-01-01T00:00:00")
  private LocalDateTime createdAt;

  @Schema(description = "Date and time of the user's last login (ISO 8601 format)", example = "2025-12-01T12:00:00")
  private LocalDateTime lastLogin;

  public static UserResponse fromUser(User user) {
    return new UserResponse(
      user.getId(),
      user.getFirstName(),
      user.getLastName(),
      user.getEmail(),
      user.getRole(),
      user.getOrganization() != null ? user.getOrganization().getId() : null,
      user.getIsVerified(),
      user.getJoinedAt(),
      user.getCreatedAt(),
      user.getLastLogin()
    );
  }
}
