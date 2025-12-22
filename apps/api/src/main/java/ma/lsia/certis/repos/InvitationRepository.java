package ma.lsia.certis.repos;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ma.lsia.certis.entities.Invitation;
import ma.lsia.certis.enums.InvitationStatus;

@Repository
public interface InvitationRepository extends JpaRepository<Invitation, UUID> {
  Optional<Invitation> findByToken(String token);
  
  Optional<Invitation> findByTokenAndStatus(String token, InvitationStatus status);
  
  List<Invitation> findByOrganizationIdAndStatus(UUID organizationId, InvitationStatus status);
  
  List<Invitation> findByEmailAndStatus(String email, InvitationStatus status);
  
  List<Invitation> findByStatusAndExpiresAtBefore(InvitationStatus status, LocalDateTime dateTime);
  
  boolean existsByEmailAndOrganizationIdAndStatus(String email, UUID organizationId, InvitationStatus status);
}
