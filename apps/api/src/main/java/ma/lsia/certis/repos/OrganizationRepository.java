package ma.lsia.certis.repos;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import ma.lsia.certis.entities.Organization;

/**
 * Repository for Organization entity.
 * 
 * Spring Data REST auto-generates endpoints at /data/organizations
 * 
 * Note: Security is enforced at the controller/REST level, not at repository level,
 * to allow internal operations like DevDataLoader to function without authentication.
 */
@RepositoryRestResource
public interface OrganizationRepository extends JpaRepository<Organization, UUID> {
  Optional<Organization> findByDomain(String domain);
  Optional<Organization> findByName(String name);
}
