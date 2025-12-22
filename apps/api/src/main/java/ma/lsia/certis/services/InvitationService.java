package ma.lsia.certis.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;
import ma.lsia.certis.entities.Invitation;
import ma.lsia.certis.entities.Organization;
import ma.lsia.certis.entities.User;
import ma.lsia.certis.enums.InvitationStatus;
import ma.lsia.certis.enums.Role;
import ma.lsia.certis.exception.ConflictException;
import ma.lsia.certis.repos.InvitationRepository;
import ma.lsia.certis.repos.OrganizationRepository;
import ma.lsia.certis.repos.UserRepository;

@Service
@Slf4j
public class InvitationService {
  
  private final InvitationRepository invitationRepo;
  private final UserRepository userRepo;
  private final OrganizationRepository orgRepo;
  private final EmailService emailService;
  
  @Value("${invitation.expiration-days}")
  private int expirationDays;
  
  public InvitationService(
      InvitationRepository invitationRepo,
      UserRepository userRepo,
      OrganizationRepository orgRepo,
      EmailService emailService) {
    this.invitationRepo = invitationRepo;
    this.userRepo = userRepo;
    this.orgRepo = orgRepo;
    this.emailService = emailService;
  }
  
  /**
   * Create a new invitation
   */
  @Transactional
  public Invitation createInvitation(String email, Role role, UUID organizationId, UUID inviterId) {
    // Validate role is an org role
    if (!role.isOrgRole()) {
      throw new IllegalArgumentException("Can only invite users with organization roles (OWNER, ADMIN, USER)");
    }

    if (email == null || email.isBlank()) {
      throw new IllegalArgumentException("Email must not be null or blank");
    }

    if (organizationId == null) {
      throw new IllegalArgumentException("Organization ID must not be null");
    }

    if (inviterId == null) {
      throw new IllegalArgumentException("Inviter ID must not be null");
    }
    
    // Get organization
    Organization org = orgRepo.findById(organizationId)
        .orElseThrow(() -> new IllegalArgumentException("Organization not found"));
    
    // Get inviter
    User inviter = userRepo.findById(inviterId)
        .orElseThrow(() -> new IllegalArgumentException("Inviter not found"));
    
    // Validate inviter has permission (must be OWNER or ADMIN)
    if (inviter.getRole() != Role.OWNER && inviter.getRole() != Role.ADMIN) {
      throw new IllegalStateException("Only OWNER or ADMIN can invite users");
    }
    
    // Validate inviter belongs to the organization
    if (inviter.getOrganization() == null || !inviter.getOrganization().getId().equals(organizationId)) {
      throw new IllegalStateException("Inviter must belong to the organization");
    }
    
    // Check if user already has a pending invitation
    if (invitationRepo.existsByEmailAndOrganizationIdAndStatus(email, organizationId, InvitationStatus.PENDING)) {
      throw new ConflictException("User already has a pending invitation to this organization");
    }
    
    // Check if user already exists and belongs to an organization
    userRepo.findByEmail(email).ifPresent(user -> {
      if (user.getOrganization() != null) {
        throw new ConflictException("User already belongs to an organization");
      }
    });
    
    // Create invitation
    Invitation invitation = new Invitation();
    invitation.setToken(UUID.randomUUID().toString());
    invitation.setEmail(email);
    invitation.setRole(role);
    invitation.setOrganization(org);
    invitation.setInvitedBy(inviter);
    invitation.setStatus(InvitationStatus.PENDING);
    invitation.setExpiresAt(LocalDateTime.now().plusDays(expirationDays));
    
    invitation = invitationRepo.save(invitation);
    
    // Send invitation email
    try {
      emailService.sendInvitationEmail(email, invitation.getToken(), org.getName(), role);
    } catch (Exception e) {
      log.error("Failed to send invitation email, but invitation was created", e);
      // Don't fail the invitation creation if email fails
    }
    
    log.info("Invitation created for {} to join {} as {}", email, org.getName(), role);
    return invitation;
  }
  
  /**
   * Accept an invitation
   */
  @Transactional
  public void acceptInvitation(String token, UUID userId) {
    if (token == null || token.isBlank()) {
      throw new IllegalArgumentException("Invitation token must not be null or blank");
    }

    if (userId == null) {
      throw new IllegalArgumentException("User ID must not be null");
    }

    // Find invitation
    Invitation invitation = invitationRepo.findByTokenAndStatus(token, InvitationStatus.PENDING)
        .orElseThrow(() -> new IllegalArgumentException("Invalid or expired invitation"));
    
    // Check if expired
    if (!invitation.isValid()) {
      invitation.setStatus(InvitationStatus.EXPIRED);
      invitationRepo.save(invitation);
      throw new IllegalArgumentException("Invitation has expired");
    }
    
    // Get user
    User user = userRepo.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("User not found"));
    
    // Validate user email matches invitation
    if (!user.getEmail().equalsIgnoreCase(invitation.getEmail())) {
      throw new IllegalArgumentException("User email does not match invitation");
    }
    
    // Validate user doesn't already belong to an organization
    if (user.getOrganization() != null) {
      throw new ConflictException("User already belongs to an organization");
    }
    
    // Add user to organization
    user.setOrganization(invitation.getOrganization());
    user.setRole(invitation.getRole());
    user.setJoinedAt(LocalDateTime.now());
    userRepo.save(user);
    
    // Mark invitation as accepted
    invitation.setStatus(InvitationStatus.ACCEPTED);
    invitation.setAcceptedAt(LocalDateTime.now());
    invitationRepo.save(invitation);
    
    // Send welcome email
    try {
      emailService.sendWelcomeEmail(user.getEmail(), invitation.getOrganization().getName());
    } catch (Exception e) {
      log.error("Failed to send welcome email", e);
    }
    
    log.info("User {} accepted invitation to join {}", user.getEmail(), invitation.getOrganization().getName());
  }
  
  /**
   * Revoke an invitation
   */
  @Transactional
  public void revokeInvitation(String token, UUID revokerId) {
    if (token == null || token.isBlank()) {
      throw new IllegalArgumentException("Invitation token must not be null or blank");
    }

    if (revokerId == null) {
      throw new IllegalArgumentException("Revoker ID must not be null");
    }
    
    // Find invitation
    Invitation invitation = invitationRepo.findByToken(token)
        .orElseThrow(() -> new IllegalArgumentException("Invitation not found"));
    
    // Validate revoker has permission
    User revoker = userRepo.findById(revokerId)
        .orElseThrow(() -> new IllegalArgumentException("Revoker not found"));
    
    if (revoker.getRole() != Role.OWNER && revoker.getRole() != Role.ADMIN) {
      throw new IllegalStateException("Only OWNER or ADMIN can revoke invitations");
    }
    
    if (revoker.getOrganization() == null || 
        !revoker.getOrganization().getId().equals(invitation.getOrganization().getId())) {
      throw new IllegalStateException("Can only revoke invitations for your organization");
    }
    
    invitation.setStatus(InvitationStatus.REVOKED);
    invitationRepo.save(invitation);
    
    log.info("Invitation {} revoked by {}", token, revoker.getEmail());
  }
  
  /**
   * Get pending invitations for an organization
   */
  @Transactional(readOnly = true)
  public List<Invitation> getPendingInvitations(UUID organizationId) {
    return invitationRepo.findByOrganizationIdAndStatus(organizationId, InvitationStatus.PENDING);
  }
  
  /**
   * Cleanup expired invitations (for scheduled task)
   */
  @Transactional
  public void cleanupExpiredInvitations() {
    List<Invitation> expired = invitationRepo.findByStatusAndExpiresAtBefore(
        InvitationStatus.PENDING, LocalDateTime.now());
    
    for (Invitation invitation : expired) {
      invitation.setStatus(InvitationStatus.EXPIRED);
    }
    
    if (!expired.isEmpty()) {
      invitationRepo.saveAll(expired);
      log.info("Marked {} invitations as expired", expired.size());
    }
  }
}
