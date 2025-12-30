package ma.lsia.certis.repos;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.security.access.prepost.PreAuthorize;

import ma.lsia.certis.entities.Certificate;

/**
 * Repository for Certificate entity with multitenant isolation.
 * 
 * Spring Data REST auto-generates endpoints at /certificates
 * Custom queries enforce tenant filtering based on authenticated user's organization.
 */
@RepositoryRestResource
@PreAuthorize("isAuthenticated()")
public interface CertificateRepository extends JpaRepository<Certificate, UUID> {
  
  /**
   * Find certificate by serial number (no tenant filter - for public verification).
   */
  Optional<Certificate> findBySerialNumber(String serialNumber);
  
  /**
   * Find revoked certificate by serial number.
   */
  Optional<Certificate> findBySerialNumberAndIsRevokedIsTrue(String serialNumber);
  
  /**
   * Find valid (non-revoked) certificate by serial number.
   */
  Optional<Certificate> findBySerialNumberAndIsRevokedIsFalse(String serialNumber);
  
  /**
   * Find all certificates for the current user's organization.
   * Uses SpEL expression to inject organizationId from JWT token.
   */
  @Query("SELECT c FROM Certificate c WHERE c.organization.id = ?#{@jwtContextHolder.getOrganizationId()}")
  List<Certificate> findAllForCurrentOrg();
  
  /**
   * Find certificate by ID with tenant filtering.
   * Ensures users can only access certificates from their organization.
   */
  @Query("SELECT c FROM Certificate c WHERE c.id = :id AND c.organization.id = ?#{@jwtContextHolder.getOrganizationId()}")
  Optional<Certificate> findByIdForCurrentOrg(@Param("id") UUID id);
  
  /**
   * Find all non-revoked certificates for the current user's organization.
   */
  @Query("SELECT c FROM Certificate c WHERE c.isRevoked = false AND c.organization.id = ?#{@jwtContextHolder.getOrganizationId()}")
  List<Certificate> findValidCertificatesForCurrentOrg();
  
  /**
   * Count certificates in the current user's organization.
   */
  @Query("SELECT COUNT(c) FROM Certificate c WHERE c.organization.id = ?#{@jwtContextHolder.getOrganizationId()}")
  long countForCurrentOrg();
}
