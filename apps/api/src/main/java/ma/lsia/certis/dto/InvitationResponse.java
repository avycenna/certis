package ma.lsia.certis.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ma.lsia.certis.entities.Invitation;
import ma.lsia.certis.enums.InvitationStatus;
import ma.lsia.certis.enums.Role;

import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response payload containing invitation details")
public class InvitationResponse {
  @Schema(description = "Unique identifier of the invitation", example = "b3b7c8e2-1d2a-4c3e-9f2e-123456789abc")
  private UUID id;

  @Schema(description = "Email address of the invitee", example = "invitee@example.com")
  private String email;

  @Schema(description = "Role assigned to the invitee", example = "USER")
  private Role role;

  @Schema(description = "Name of the organization the invitee is invited to", example = "Certis Academy")
  private String organizationName;

  @Schema(description = "Email address of the user who sent the invitation", example = "admin@certis.com")
  private String invitedByEmail;

  @Schema(description = "Status of the invitation", example = "PENDING")
  private InvitationStatus status;

  @Schema(description = "Expiration date and time of the invitation (ISO 8601 format)", example = "2025-12-31T23:59:59")
  private LocalDateTime expiresAt;

  @Schema(description = "Date and time the invitation was created (ISO 8601 format)", example = "2025-01-01T00:00:00")
  private LocalDateTime createdAt;

  public static InvitationResponse fromInvitation(Invitation invitation) {
    return new InvitationResponse(
      invitation.getId(),
      invitation.getEmail(),
      invitation.getRole(),
      invitation.getOrganization().getName(),
      invitation.getInvitedBy().getEmail(),
      invitation.getStatus(),
      invitation.getExpiresAt(),
      invitation.getCreatedAt()
    );
  }
}
