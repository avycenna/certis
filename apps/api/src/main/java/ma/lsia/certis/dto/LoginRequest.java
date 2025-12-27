package ma.lsia.certis.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request payload for user login")
public class LoginRequest {

  @Schema(description = "User's email address", example = "user@example.com")
  @NotBlank(message = "Email is required")
  @Email(message = "Email must be valid")
  private String email;


  @Schema(description = "User's password", example = "StrongPassword123!")
  @NotBlank(message = "Password is required")
  private String password;
}
