package ma.lsia.certis.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ma.lsia.certis.entities.Certificate;

import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response payload containing certificate details")
public class CertificateResponse {
  @Schema(description = "Unique identifier of the certificate", example = "b3b7c8e2-1d2a-4c3e-9f2e-123456789abc")
  private UUID id;

  @Schema(description = "Serial number of the certificate", example = "CERT-2025-0001")
  private String serialNumber;

  @Schema(description = "Whether the certificate is revoked", example = "false")
  private Boolean isRevoked;

  @Schema(description = "Subject of the certificate", example = "Java Developer Certificate")
  private String subject;

  @Schema(description = "Start date of certificate validity (ISO 8601 format)", example = "2025-01-01T00:00:00")
  private LocalDateTime activeFrom;

  @Schema(description = "End date of certificate validity (ISO 8601 format)", example = "2025-12-31T23:59:59")
  private LocalDateTime activeTo;

  @Schema(description = "Date the certificate was created (ISO 8601 format)", example = "2025-01-01T00:00:00")
  private LocalDateTime createdAt;

  @Schema(description = "Date the certificate was last updated (ISO 8601 format)", example = "2025-06-01T12:00:00")
  private LocalDateTime updatedAt;

  @Schema(description = "Issuer of the certificate")
  private UserResponse issuer;

  public static CertificateResponse fromCertificate(Certificate cert) {
    CertificateResponse response = new CertificateResponse();
    response.setId(cert.getId());
    response.setSerialNumber(cert.getSerialNumber());
    response.setIsRevoked(cert.getIsRevoked());
    response.setSubject(cert.getSubject());
    response.setActiveFrom(cert.getActiveFrom());
    response.setActiveTo(cert.getActiveTo());
    response.setCreatedAt(cert.getCreatedAt());
    response.setUpdatedAt(cert.getUpdatedAt());
    response.setIssuer(UserResponse.fromUser(cert.getIssuer()));
    return response;
  }
}
