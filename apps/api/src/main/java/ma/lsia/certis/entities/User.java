package ma.lsia.certis.entities;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
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
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
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
  @Size(min = 6, max = 255)
  @JsonIgnore
  @Schema(description = "Hashed password of the user")
  private String password;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  @Schema(description = "Role of the user in the system", example = "USER")
  private Role role = Role.USER;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  @Schema(description = "Date and time when the user was verified (ISO 8601 format)", example = "2025-01-15T10:00:00")
  private LocalDateTime isVerified;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "organization_id")
  @Schema(description = "Organization the user belongs to")
  private Organization organization;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  @Schema(description = "Date and time when the user joined (ISO 8601 format)", example = "2025-01-10T09:00:00")
  private LocalDateTime joinedAt;

  @OneToMany(mappedBy = "issuer", fetch = FetchType.LAZY)
  @Schema(description = "Certificates issued by the user")
  private Set<Certificate> issuedCertificates;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  @Column(updatable = false)
  @Schema(description = "Date and time when the user was created (ISO 8601 format)", example = "2025-01-01T00:00:00")
  private LocalDateTime createdAt;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  @Schema(description = "Date and time when the user was last updated (ISO 8601 format)", example = "2025-06-01T12:00:00")
  private LocalDateTime updatedAt;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  @Schema(description = "Date and time of the user's last login (ISO 8601 format)", example = "2025-12-01T12:00:00")
  private LocalDateTime lastLogin;

  @PrePersist
  protected void onCreate() {
    this.createdAt = LocalDateTime.now();
    this.updatedAt = LocalDateTime.now();
  }

  @PreUpdate
  protected void onUpdate() {
    this.updatedAt = LocalDateTime.now();
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
      ", isVerified=" + isVerified +
      ", createdAt=" + createdAt +
      ", updatedAt=" + updatedAt +
      ", lastLogin=" + lastLogin +
      '}';
  }
}
