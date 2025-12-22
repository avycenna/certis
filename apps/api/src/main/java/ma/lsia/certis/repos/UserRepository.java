package ma.lsia.certis.repos;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import ma.lsia.certis.entities.User;

public interface UserRepository extends JpaRepository<User, UUID> {
  Optional<User> findByEmail(String email);
  Optional<User> findByIdAndIsVerifiedIsNotNull(UUID id);
  Optional<User> findByEmailAndIsVerifiedIsNotNull(String email);
}
