package ma.lsia.certis.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ma.lsia.certis.enums.Role;

import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request payload for creating an invitation to join an organization")
public class CreateInvitationRequest {

  @Schema(description = "Email address of the invitee", example = "invitee@example.com")
  @NotBlank(message = "Email is required")
  @Email(message = "Email must be valid")
  private String email;


  @Schema(description = "Role to assign to the invitee", example = "USER")
  @NotNull(message = "Role is required")
  private Role role;
}
