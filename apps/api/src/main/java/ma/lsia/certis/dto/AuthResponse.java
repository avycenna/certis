package ma.lsia.certis.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "Response payload for authentication requests, containing JWT token and user info")
public class AuthResponse {
  @Schema(description = "JWT token for authentication", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
  private String token;

  @Schema(description = "Type of the token", example = "Bearer")
  private String type = "Bearer";

  @Schema(description = "Authenticated user details")
  private UserResponse user;

  public AuthResponse(String token, UserResponse user) {
    this.token = token;
    this.type = "Bearer";
    this.user = user;
  }
}
