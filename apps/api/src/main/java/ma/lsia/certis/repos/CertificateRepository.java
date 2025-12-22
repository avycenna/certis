package ma.lsia.certis.repos;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import ma.lsia.certis.entities.Certificate;

public interface CertificateRepository extends JpaRepository<Certificate, UUID> {
  Optional<Certificate> findBySerialNumber(String serialNumber);
  Optional<Certificate> findBySerialNumberAndIsRevokedIsTrue(String serialNumber);
  Optional<Certificate> findBySerialNumberAndIsRevokedIsFalse(String serialNumber);
}
