package ma.lsia.certis.entities;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Email;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import io.swagger.v3.oas.annotations.media.Schema;
import ma.lsia.certis.enums.Role;

@Entity
@Table(
  name = "users",
  indexes = {
    @Index(name = "idx_user_email", columnList = "email")
  }
)
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
  /**
   * User information
   * @Fields firstName lastName email password verifiedAt
   */
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Schema(description = "Unique identifier of the user", example = "b3b7c8e2-1d2a-4c3e-9f2e-123456789abc")
  private UUID id;

  @NotBlank
  @Size(min = 3, max = 50)
  @Schema(description = "First name of the user", example = "John")
  private String firstName;
  
  @NotBlank
  @Size(min = 3, max = 50)
  @Schema(description = "Last name of the user", example = "Doe")
  private String lastName;
  
  @NotBlank
  @Email
  @Column(unique = true)
  @Schema(description = "Email address of the user", example = "user@email.com")
  private String email;

  @NotBlank
  @Size(max = 255)
  @JsonIgnore
  @Schema(description = "Hashed password of the user")
  private String password;

  @Schema(description = "Date and time when the user was verified (ISO 8601 format)", example = "2025-01-15T10:00:00")
  private LocalDateTime verifiedAt;

  /**
   * Organization relation
   * @Fields organization role joinedAt
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "organization_id")
  @Schema(description = "Organization the user belongs to")
  private Organization organization;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  @Schema(description = "Role of the user in the system", example = "USER")
  private Role role = Role.USER;

  @Schema(description = "Date and time when the user joined (ISO 8601 format)", example = "2025-01-10T09:00:00")
  private LocalDateTime joinedAt;

  @OneToMany(mappedBy = "issuer", fetch = FetchType.LAZY)
  @JsonIgnore
  @Schema(description = "Certificates issued by the user")
  private Set<Certificate> issuedCertificates;

  /**
   * Auditing fields
   */
  @CreatedBy
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "created_by", updatable = false)
  @JsonIgnore
  @Schema(description = "User who created this user record")
  private User createdBy;
  
  @CreatedDate
  @Column(updatable = false)
  @Schema(description = "Date and time when the user was created (ISO 8601 format)", example = "2025-01-01T00:00:00")
  private LocalDateTime createdAt;

  @LastModifiedDate
  @Schema(description = "Date and time when the user was last updated (ISO 8601 format)", example = "2025-06-01T12:00:00")
  private LocalDateTime updatedAt;

  @Version
  @Schema(description = "Version number for optimistic locking")
  private Long version;

  @Schema(description = "Date and time of the user's last login (ISO 8601 format)", example = "2025-12-01T12:00:00")
  private LocalDateTime lastLogin;

  /**
   * Helper method to check if user is verified.
   * 
   * @return true if verifiedAt is not null, false otherwise
   */
  public boolean isVerified() {
    return verifiedAt != null;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof User)) return false;
    User user = (User) o;
    return id != null && id.equals(user.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }

  @Override
  public String toString() {
    return "User{" +
      "id=" + id +
      ", firstName='" + firstName + '\'' +
      ", lastName='" + lastName + '\'' +
      ", email='" + email + '\'' +
      ", verifiedAt=" + verifiedAt +
      ", createdAt=" + createdAt +
      ", updatedAt=" + updatedAt +
      ", lastLogin=" + lastLogin +
      '}';
  }
}
