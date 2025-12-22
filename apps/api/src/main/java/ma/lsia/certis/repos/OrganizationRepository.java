package ma.lsia.certis.repos;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import ma.lsia.certis.entities.Organization;

public interface OrganizationRepository extends JpaRepository<Organization, UUID> {
  Optional<Organization> findByDomain(String domain);
  Optional<Organization> findByName(String name);
}
