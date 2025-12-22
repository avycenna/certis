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

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InvitationResponse {
  private UUID id;
  private String email;
  private Role role;
  private String organizationName;
  private String invitedByEmail;
  private InvitationStatus status;
  private LocalDateTime expiresAt;
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
