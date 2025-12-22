package ma.lsia.certis.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ma.lsia.certis.enums.Role;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateInvitationRequest {
  @NotBlank(message = "Email is required")
  @Email(message = "Email must be valid")
  private String email;

  @NotNull(message = "Role is required")
  private Role role;
}
