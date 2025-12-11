package ma.lsia.certis.repos;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import ma.lsia.certis.entities.User;

public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByEmail(String email);
  Optional<User> findByIdAndIsVerifiedIsNotNull(Long id);
  Optional<User> findByEmailAndIsVerifiedIsNotNull(String email);
}
