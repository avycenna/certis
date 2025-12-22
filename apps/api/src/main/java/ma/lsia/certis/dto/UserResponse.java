package ma.lsia.certis.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ma.lsia.certis.entities.User;
import ma.lsia.certis.enums.Role;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
  private UUID id;
  private String firstName;
  private String lastName;
  private String email;
  private Role role;
  private UUID organizationId;
  private LocalDateTime isVerified;
  private LocalDateTime joinedAt;
  private LocalDateTime createdAt;
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
