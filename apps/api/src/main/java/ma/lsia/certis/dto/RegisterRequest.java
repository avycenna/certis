package ma.lsia.certis.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request payload for user registration")
public class RegisterRequest {

  @Schema(description = "User's first name", example = "John")
  @NotBlank(message = "First name is required")
  @Size(min = 3, max = 50, message = "First name must be between 3 and 50 characters")
  private String firstName;
  

  @Schema(description = "User's last name", example = "Doe")
  @NotBlank(message = "Last name is required")
  @Size(min = 3, max = 50, message = "Last name must be between 3 and 50 characters")
  private String lastName;
  

  @Schema(description = "User's email address", example = "john.doe@example.com")
  @NotBlank(message = "Email is required")
  @Email(message = "Email must be valid")
  private String email;


  @Schema(description = "User's password (must meet complexity requirements)", example = "StrongPassword123!")
  @NotBlank(message = "Password is required")
  @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
  @Pattern(
    regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
    message = "Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character (@$!%*?&)"
  )
  private String password;
}
