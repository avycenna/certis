package ma.lsia.certis.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;
import ma.lsia.certis.dto.BatchCertificateResponse;
import ma.lsia.certis.dto.BatchCreateCertificateRequest;
import ma.lsia.certis.dto.CertificateResponse;
import ma.lsia.certis.dto.CreateCertificateRequest;
import ma.lsia.certis.entities.Certificate;
import ma.lsia.certis.entities.Organization;
import ma.lsia.certis.entities.User;
import ma.lsia.certis.repos.CertificateRepository;

@Service
@Slf4j
public class CertificateService {
  private final CertificateRepository certRepo;
  private final UserService userService;

  public CertificateService(CertificateRepository certRepo, UserService userService) {
    this.certRepo = certRepo;
    this.userService = userService;
  }

  @Transactional(readOnly = true)
  public Optional<Certificate> getCertificateBySerialNumber(String serialNumber) {
    return certRepo.findBySerialNumber(serialNumber);
  }

  @Transactional
  public Certificate createCertificate(CreateCertificateRequest request, User issuer) {
    // Get organization from issuer user
    Organization organization = issuer.getOrganization();
    if (organization == null) {
      throw new IllegalArgumentException("User must be associated with an organization to create certificates");
    }

    Certificate cert = new Certificate();
    
    // Generate unique serial number
    cert.setSerialNumber(generateUniqueSerialNumber());
    cert.setSubject(request.getSubject());
    cert.setActiveFrom(request.getActiveFrom());
    cert.setActiveTo(request.getActiveTo());
    cert.setIssuer(issuer);
    cert.setOrganization(organization);
    cert.setIsRevoked(false);

    return certRepo.save(cert);
  }

  private String generateUniqueSerialNumber() {
    String serialNumber;
    int attempts = 0;
    do {
      serialNumber = java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
      attempts++;
      if (attempts > 10) {
        throw new RuntimeException("Failed to generate unique serial number after 10 attempts");
      }
    } while (certRepo.findBySerialNumber(serialNumber).isPresent());
    return serialNumber;
  }

  @Transactional
  public BatchCertificateResponse batchCreateCertificates(
      BatchCreateCertificateRequest request,
      String issuerEmail) {

    if (issuerEmail == null || issuerEmail.isBlank()) {
      throw new IllegalArgumentException("Issuer email must be provided");
    }
    
    List<CreateCertificateRequest> certificates = request.getCertificates();
    BatchCertificateResponse response = new BatchCertificateResponse(certificates.size());

    // Get issuer user
    User issuer = userService.getUserByEmail(issuerEmail)
        .orElseThrow(() -> new IllegalArgumentException("Issuer not found"));

    // Process each certificate
    for (int i = 0; i < certificates.size(); i++) {
      CreateCertificateRequest certRequest = certificates.get(i);
      try {
        // Validate and create certificate
        Certificate cert = createCertificate(certRequest, issuer);
        response.addSuccess(CertificateResponse.fromCertificate(cert));
        
        log.info("Successfully created certificate {} ({}/{})", 
            cert.getSerialNumber(), i + 1, certificates.size());
      } catch (Exception e) {
        // Log error and continue with next certificate
        response.addError(i, certRequest.getSubject(), e.getMessage());
        log.error("Failed to create certificate for subject {} ({}/{}): {}", 
            certRequest.getSubject(), i + 1, certificates.size(), e.getMessage());
      }
    }

    log.info("Batch creation completed: {} successful, {} failed out of {} total",
        response.getSuccessfullyCreated(), response.getFailed(), response.getTotalRequested());

    return response;
  }
}
