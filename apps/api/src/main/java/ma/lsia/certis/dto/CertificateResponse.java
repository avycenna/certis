package ma.lsia.certis.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ma.lsia.certis.entities.Certificate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CertificateResponse {
  private UUID id;
  private String serialNumber;
  private Boolean isRevoked;
  private String subject;
  private LocalDateTime activeFrom;
  private LocalDateTime activeTo;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
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
