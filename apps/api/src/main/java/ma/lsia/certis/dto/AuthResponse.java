package ma.lsia.certis.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AuthResponse {
  private String token;
  private String type = "Bearer";
  private UserResponse user;

  public AuthResponse(String token, UserResponse user) {
    this.token = token;
    this.type = "Bearer";
    this.user = user;
  }
}
