package ma.lsia.certis.entities;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ma.lsia.certis.enums.Role;

import java.util.Comparator;

import io.swagger.v3.oas.annotations.media.Schema;

@Entity
@Table(
  name = "organizations",
  indexes = {
    @Index(name = "idx_org_owner", columnList = "user_id")
  }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Represents an organization in the system")
public class Organization {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Schema(description = "Unique identifier of the organization", example = "b3b7c8e2-1d2a-4c3e-9f2e-123456789abc")
  private UUID id;

  @NotBlank
  @Size(min = 3, max = 50)
  @Schema(description = "Name of the organization", example = "Certis Academy")
  private String name;
  
  @Size(max = 1024)
  @Schema(description = "Description of the organization", example = "A platform for managing certificates and courses.")
  private String desc;

  @NotBlank
  @Size(min = 3, max = 255)
  @Pattern(
    regexp = "^(?:[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?\\.)+[a-zA-Z]{2,}$",
    message = "Domain must be a valid domain name (e.g., example.com, subdomain.example.com)"
  )
  @Schema(description = "Domain of the organization", example = "certis.com")
  private String domain;

  @NotNull
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "owner_user_id", unique = true)
  @Schema(description = "Owner of the organization")
  private User owner;

  @OneToMany(mappedBy = "organization", fetch = FetchType.LAZY)
  @Schema(description = "Users belonging to the organization")
  private Set<User> users;

  @OneToMany(mappedBy = "organization", fetch = FetchType.LAZY)
  @Schema(description = "Courses offered by the organization")
  private Set<Course> courses;

  @Lob
  @Size(max = 1048576 * 10) // 10 MB
  @Schema(description = "Logo of the organization (binary data)")
  private byte[] logo;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  @Schema(description = "Date the organization was created (ISO 8601 format)", example = "2025-01-01T00:00:00")
  private LocalDateTime createdAt;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  @Schema(description = "Date the organization was last updated (ISO 8601 format)", example = "2025-06-01T12:00:00")
  private LocalDateTime updatedAt;

  @PrePersist
  protected void onCreate() {
    this.createdAt = LocalDateTime.now();
    this.updatedAt = LocalDateTime.now();
  }

  @PreUpdate
  protected void onUpdate() {
    this.updatedAt = LocalDateTime.now();
  }

  // TODO: check if these functions should be moved to a repository or service class
  /**
   * Get the owner of the organization (user with OWNER role)
   */
  public User getOwner() {
    if (owner != null) {
      return owner;
    }
    // Fallback: find user with OWNER role in users collection
    if (users != null) {
      return users.stream()
          .filter(user -> user.getRole() == Role.OWNER)
          .findFirst()
          .orElse(null);
    }
    return null;
  }

  /**
   * Get the most senior admin or user as fallback for ownership transfer
   * Used when owner leaves without explicit transfer
   */
  public User getMostSeniorAdminOrUser() {
    if (users == null || users.isEmpty()) {
      return null;
    }
    
    // First try to find most senior ADMIN
    User seniorAdmin = users.stream()
        .filter(user -> user.getRole() == Role.ADMIN)
        .filter(user -> user.getJoinedAt() != null)
        .min(Comparator.comparing(User::getJoinedAt))
        .orElse(null);
    
    if (seniorAdmin != null) {
      return seniorAdmin;
    }
    
    // Fallback to most senior USER
    return users.stream()
        .filter(user -> user.getRole() == Role.USER)
        .filter(user -> user.getJoinedAt() != null)
        .min(Comparator.comparing(User::getJoinedAt))
        .orElse(null);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Organization)) return false;
    Organization that = (Organization) o;
    return id != null && id.equals(that.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }

  @Override
  public String toString() {
    return "Organization{" +
      "id=" + id +
      ", name='" + name + '\'' +
      ", desc='" + desc + '\'' +
      ", createdAt=" + createdAt +
      ", updatedAt=" + updatedAt +
      '}';
  }
}
