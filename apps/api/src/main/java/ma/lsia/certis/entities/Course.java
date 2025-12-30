package ma.lsia.certis.entities;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
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
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
// import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;

// @Builder

@Entity
@Table(
  name = "courses",
  indexes = {
    @Index(name = "idx_course_slug", columnList = "slug"),
    @Index(name = "idx_course_org_id", columnList = "org_id"),
    @Index(name = "idx_course_created_by", columnList = "created_by")
  }
)
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Represents a course offered by an organization")
public class Course {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Schema(description = "Unique identifier of the course", example = "b3b7c8e2-1d2a-4c3e-9f2e-123456789abc")
  private UUID id;

  @NotBlank
  @Size(min = 3, max = 100)
  @Schema(description = "Title of the course", example = "Advanced Java Programming")
  private String title;

  @Size(max = 2048)
  @Schema(description = "Description of the course", example = "Covers advanced Java topics including concurrency and streams.")
  private String description;

  @Size(max = 100)
  @Schema(description = "URL to the course template", example = "https://certis.com/templates/java.pdf")
  private String templateUrl;

  @Embedded
  @Schema(description = "Text box layout for certificates")
  private TextBox textBox;

  @NotBlank
  @Size(min = 3, max = 100)
  @Schema(description = "URL-friendly identifier for the course", example = "advanced-java-programming")
  private String slug;
  
  @Schema(description = "Whether the course is active", example = "true")
  private Boolean isActive = true;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "org_id", nullable = false)
  @Schema(description = "Organization that owns the course")
  private Organization organization;

  @OneToMany(mappedBy = "course")
  @JsonIgnore
  @Schema(description = "Certificates issued for this course")
  private Set<Certificate> certificates;

  @CreatedBy
  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "created_by")
  @JsonIgnore
  @Schema(description = "User who created the course")
  private User createdBy;

  @CreatedDate
  @Column(updatable = false)
  @Schema(description = "Date the course was created (ISO 8601 format)", example = "2025-01-01T00:00:00")
  private LocalDateTime createdAt;

  @LastModifiedDate
  @Schema(description = "Date the course was last updated (ISO 8601 format)", example = "2025-06-01T12:00:00")
  private LocalDateTime updatedAt;

  @Version
  @Schema(description = "Version number for optimistic locking")
  private Long version;

  /**
   * @PrePersist hook for slug generation.
   * Automatically generates a URL-friendly slug from the title if not provided.
   * This is a single-aggregate data transformation appropriate for @PrePersist.
   */
  @PrePersist
  protected void onCreate() {
    // Auto-generate slug from title if not provided
    if ((this.slug == null || this.slug.isBlank()) && this.title != null) {
      this.slug = generateSlug(this.title);
    }
  }
  
  @PreUpdate
  protected void onUpdate() {
    // Update slug if title changed and slug is empty
    if ((this.slug == null || this.slug.isBlank()) && this.title != null) {
      this.slug = generateSlug(this.title);
    }
  }

  /**
   * Generates a URL-friendly slug from a title.
   * Single-aggregate transformation suitable for @PrePersist.
   */
  private String generateSlug(String title) {
    return title.toLowerCase()
        .replaceAll("[^a-z0-9\\s-]", "")
        .replaceAll("\\s+", "-")
        .replaceAll("-+", "-")
        .trim();
  }

  @Override
  public String toString() {
    return "Course{" +
      "id=" + id +
      ", title='" + title + '\'' +
      ", description='" + description + '\'' +
      ", templateUrl='" + templateUrl + '\'' +
      ", slug='" + slug + '\'' +
      ", isActive=" + isActive +
      ", createdAt=" + createdAt +
      ", updatedAt=" + updatedAt +
      '}';
  }
}
