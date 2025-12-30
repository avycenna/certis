package ma.lsia.certis.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;
import ma.lsia.certis.dto.CreateOrganizationRequest;
import ma.lsia.certis.entities.Organization;
import ma.lsia.certis.entities.User;
import ma.lsia.certis.enums.Role;
import ma.lsia.certis.exception.ConflictException;
import ma.lsia.certis.repos.OrganizationRepository;
import ma.lsia.certis.repos.UserRepository;

@Service
@Slf4j
public class OrganizationService {
  private final OrganizationRepository orgRepo;
  private final UserRepository userRepo;

  public OrganizationService(OrganizationRepository orgRepo, UserRepository userRepo) {
    this.orgRepo = orgRepo;
    this.userRepo = userRepo;
  }

  @Transactional
  public Organization createOrganization(CreateOrganizationRequest request, User owner) {
    // Validate user doesn't have system role
    if (owner.getRole().isSystemRole()) {
      throw new IllegalStateException("System roles (SUDOER, STAFF) cannot create organizations");
    }
    
    // Check if user already has an organization
    if (owner.getOrganization() != null) {
      throw new ConflictException("User already has an organization");
    }

    // Check if domain is already taken
    Optional<Organization> existingOrg = orgRepo.findByDomain(request.getDomain());
    if (existingOrg.isPresent()) {
      throw new ConflictException("Domain is already registered by another organization");
    }

    Organization org = new Organization();
    org.setName(request.getName());
    org.setDesc(request.getDescription());
    org.setDomain(request.getDomain());

    org = orgRepo.save(org);
    
    // Update user's organization reference and set OWNER role
    // This automatically makes the owner a member via the bidirectional relationship
    owner.setOrganization(org);
    owner.setRole(Role.OWNER);
    owner.setJoinedAt(LocalDateTime.now());
    userRepo.save(owner);

    log.info("Created organization: {} for user: {}", org.getName(), owner.getEmail());
    return org;
  }

  @Transactional(readOnly = true)
  public Optional<Organization> getOrganizationByUser(User user) {
    return Optional.ofNullable(user.getOrganization());
  }

  @Transactional(readOnly = true)
  public boolean userHasOrganization(User user) {
    return user.getOrganization() != null;
  }

  /**
   * Get all users in an organization
   */
  @Transactional(readOnly = true)
  public List<User> getOrganizationUsers(UUID organizationId) {
    if (organizationId == null) {
      throw new IllegalArgumentException("Organization ID must not be null");
    }
    
    return userRepo.findAll().stream()
        .filter(user -> user.getOrganization() != null && 
                       user.getOrganization().getId().equals(organizationId))
        .collect(Collectors.toList());
  }

  /**
   * Check if user can manage organization (is OWNER or ADMIN)
   */
  @Transactional(readOnly = true)
  public boolean canUserManageOrg(UUID userId, UUID organizationId) {
    if (userId == null || organizationId == null) {
      return false;
    }
    User user = userRepo.findById(userId).orElse(null);
    if (user == null) {
      return false;
    }
    
    // System roles can manage any org
    if (user.getRole().isSystemRole()) {
      return true;
    }
    
    // Must be OWNER or ADMIN of the organization
    return user.getOrganization() != null &&
           user.getOrganization().getId().equals(organizationId) &&
           (user.getRole() == Role.OWNER || user.getRole() == Role.ADMIN);
  }

  /**
   * Get organization by ID
   */
  @Transactional(readOnly = true)
  public Optional<Organization> getOrganizationById(UUID organizationId) {
    if (organizationId == null) {
      return Optional.empty();
    }
    return orgRepo.findById(organizationId);
  }

  /**
   * Get all organizations (admin only)
   */
  @Transactional(readOnly = true)
  public List<Organization> getAllOrganizations() {
    return orgRepo.findAll();
  }
}
