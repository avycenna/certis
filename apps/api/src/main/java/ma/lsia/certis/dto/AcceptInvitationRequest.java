package ma.lsia.certis.dto;

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
@Schema(description = "Request payload for accepting an invitation")
public class AcceptInvitationRequest {
  @Schema(description = "Invitation token received by email", example = "abc123def456")
  @NotBlank(message = "Token is required")
  private String token;
}
