package ma.lsia.certis.entities;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
// import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

import io.swagger.v3.oas.annotations.media.Schema;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
// @Builder
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

  @NotBlank
  @Size(min = 3, max = 100)
  @Schema(description = "URL-friendly identifier for the course", example = "advanced-java-programming")
  private String slug;
  
  @Schema(description = "Whether the course is active", example = "true")
  private Boolean isActive = true;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "org_id")
  @Schema(description = "Organization that owns the course")
  private Organization organization;

  @OneToMany(mappedBy = "course")
  @Schema(description = "Certificates issued for this course")
  private Set<Certificate> certificates;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "created_by")
  @Schema(description = "User who created the course")
  private User createdBy;

  @Schema(description = "Date the course was created (ISO 8601 format)", example = "2025-01-01T00:00:00")
  private LocalDateTime createdAt;

  @Schema(description = "Date the course was last updated (ISO 8601 format)", example = "2025-06-01T12:00:00")
  private LocalDateTime updatedAt;
}
