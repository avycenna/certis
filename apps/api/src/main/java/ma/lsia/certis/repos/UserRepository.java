package ma.lsia.certis.repos;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import ma.lsia.certis.entities.User;

/**
 * Repository for User entity.
 * 
 * Spring Data REST auto-generates endpoints at /data/users
 * 
 * Note: Security is enforced at the controller/REST level, not at repository level,
 * to allow internal operations like DevDataLoader and AuthService to function without authentication.
 */
@RepositoryRestResource
public interface UserRepository extends JpaRepository<User, UUID> {
  Optional<User> findByEmail(String email);
  Optional<User> findByIdAndVerifiedAtIsNotNull(UUID id);
  Optional<User> findByEmailAndVerifiedAtIsNotNull(String email);
}
