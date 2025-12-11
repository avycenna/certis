package ma.lsia.certis.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ma.lsia.certis.entities.User;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
  private Long id;
  private String firstName;
  private String lastName;
  private String email;
  private LocalDateTime isVerified;
  private LocalDateTime createdAt;
  private LocalDateTime lastLogin;

  public static UserResponse fromUser(User user) {
    return new UserResponse(
      user.getId(),
      user.getFirstName(),
      user.getLastName(),
      user.getEmail(),
      user.getIsVerified(),
      user.getCreatedAt(),
      user.getLastLogin()
    );
  }
}
