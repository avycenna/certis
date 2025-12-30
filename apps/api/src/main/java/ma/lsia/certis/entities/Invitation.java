package ma.lsia.certis.entities;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ma.lsia.certis.enums.InvitationStatus;
import ma.lsia.certis.enums.Role;

import io.swagger.v3.oas.annotations.media.Schema;

@Entity
@Table(
  name = "invitations",
  indexes = {
    @Index(name = "idx_invitation_token", columnList = "token"),
    @Index(name = "idx_invitation_email", columnList = "email"),
    @Index(name = "idx_invitation_status", columnList = "status")
  }
)
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Represents an invitation sent to a user to join an organization")
public class Invitation {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Schema(description = "Unique identifier of the invitation", example = "b3b7c8e2-1d2a-4c3e-9f2e-123456789abc")
  private UUID id;

  @NotBlank
  @Column(unique = true, nullable = false)
  @Schema(description = "Unique token for the invitation", example = "abc123def456")
  private String token;

  @NotBlank
  @Email
  @Column(nullable = false)
  @Schema(description = "Email address of the invitee", example = "invitee@example.com")
  private String email;

  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  @Schema(description = "Role assigned to the invitee", example = "USER")
  private Role role;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "organization_id", nullable = false)
  @Schema(description = "Organization the invitee is invited to")
  private Organization organization;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "invited_by_user_id", nullable = false)
  @Schema(description = "User who sent the invitation")
  private User invitedBy;

  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  @Schema(description = "Status of the invitation", example = "PENDING")
  private InvitationStatus status = InvitationStatus.PENDING;

  @NotNull
  @Schema(description = "Expiration date and time of the invitation (ISO 8601 format)", example = "2025-12-31T23:59:59")
  private LocalDateTime expiresAt;

  @CreatedDate
  @Column(updatable = false, nullable = false)
  @Schema(description = "Date and time the invitation was created (ISO 8601 format)", example = "2025-01-01T00:00:00")
  private LocalDateTime createdAt;

  @Schema(description = "Date and time the invitation was accepted (ISO 8601 format)")
  private LocalDateTime acceptedAt;

  public boolean isExpired() {
    return LocalDateTime.now().isAfter(expiresAt);
  }

  public boolean isValid() {
    return status == InvitationStatus.PENDING && !isExpired();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Invitation)) return false;
    Invitation that = (Invitation) o;
    return id != null && id.equals(that.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
