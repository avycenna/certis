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
public class Certificate {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private UUID id;

  @NotBlank
  @Column(unique = true)
  private String serialNumber;

  private Boolean isRevoked = false;

  @Size(min = 1, max = 2048)
  private String revocationReason;

  @ManyToOne(fetch = FetchType.LAZY)
  @NotNull
  private Organization organization;
  
  @ManyToOne(fetch = FetchType.LAZY)
  @NotNull
  private User issuer;

  @NotBlank
  @Size(min = 3, max = 100)
  private String subject;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private LocalDateTime activeFrom;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private LocalDateTime activeTo;

  @Column(columnDefinition = "TEXT") // Switch to JSON in production
  private String metadata;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  @Column(updatable = false)
  private LocalDateTime createdAt;
  
  @ManyToOne
  @JoinColumn(updatable = false)
  private User createdBy;
  
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
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
