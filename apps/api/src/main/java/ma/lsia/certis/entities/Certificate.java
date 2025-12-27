package ma.lsia.certis.entities;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import io.swagger.v3.oas.annotations.media.Schema;

@Entity
@Table(
  name = "certificates",
  indexes = {
    @Index(name = "idx_cert_serial_number", columnList = "serialNumber")
  }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Represents a certificate issued to a user for a course")
public class Certificate {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Schema(description = "Unique identifier of the certificate", example = "b3b7c8e2-1d2a-4c3e-9f2e-123456789abc")
  private UUID id;

  @NotBlank
  @Column(unique = true)
  @Schema(description = "Serial number of the certificate", example = "CERT-2025-0001")
  private String serialNumber;

  @Schema(description = "Whether the certificate is revoked", example = "false")
  private Boolean isRevoked = false;

  @Size(min = 1, max = 2048)
  @Schema(description = "Reason for revocation, if revoked", example = "Violation of terms")
  private String revocationReason;

  @ManyToOne(fetch = FetchType.LAZY)
  @NotNull
  @Schema(description = "Organization that issued the certificate")
  private Organization organization;
  
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "course_id")
  @Schema(description = "Course for which the certificate was issued")
  private Course course;
  
  @ManyToOne(fetch = FetchType.LAZY)
  @NotNull
  @Schema(description = "User who issued the certificate")
  private User issuer;

  @NotBlank
  @Size(min = 3, max = 100)
  @Schema(description = "Subject of the certificate", example = "Java Developer Certificate")
  private String subject;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  @Schema(description = "Start date of certificate validity (ISO 8601 format)", example = "2025-01-01T00:00:00")
  private LocalDateTime activeFrom;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  @Schema(description = "End date of certificate validity (ISO 8601 format)", example = "2025-12-31T23:59:59")
  private LocalDateTime activeTo;

  @Column(columnDefinition = "TEXT") // Switch to JSON in production
  @Schema(description = "Additional metadata for the certificate (JSON string)", example = "{\"duration\":\"40h\",\"skills\":[\"Java\",\"OOP\"]}")
  private String metadata;

  @ManyToOne
  @JoinColumn(updatable = false)
  @Schema(description = "User who created the certificate record")
  private User createdBy;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  @Column(updatable = false)
  @Schema(description = "Date the certificate was created (ISO 8601 format)", example = "2025-01-01T00:00:00")
  private LocalDateTime createdAt;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  @Schema(description = "Date the certificate was last updated (ISO 8601 format)", example = "2025-06-01T12:00:00")
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

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Certificate)) return false;
    Certificate that = (Certificate) o;
    return id != null && id.equals(that.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }

  @Override
  public String toString() {
    return "Certificate{" +
      "id=" + id +
      ", serialNumber='" + serialNumber + '\'' +
      ", isRevoked=" + isRevoked +
      ", subject='" + subject + '\'' +
      ", activeFrom=" + activeFrom +
      ", activeTo=" + activeTo +
      ", createdAt=" + createdAt +
      ", updatedAt=" + updatedAt +
      '}';
  }
}
