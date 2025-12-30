package ma.lsia.certis.services;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.lang.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ma.lsia.certis.dto.RegisterRequest;
import ma.lsia.certis.entities.User;
import ma.lsia.certis.enums.Role;
import ma.lsia.certis.repos.UserRepository;
import ma.lsia.certis.util.PasswordValidator;
import ma.lsia.certis.util.SecurityUtil;

@Service
public class UserService {
  private final UserRepository userRepo;
  private final PasswordEncoder passwordEncoder;
  
  public UserService(UserRepository userRepo, PasswordEncoder passwordEncoder) {
    this.userRepo = userRepo;
    this.passwordEncoder = passwordEncoder;
  }
  
  @Transactional
  public User createUser(RegisterRequest request) {
    // Check if user already exists
    if (userRepo.findByEmail(request.getEmail()).isPresent()) {
      throw new IllegalArgumentException("Email already registered");
    }

    // Validate password strength before creating user
    PasswordValidator.validate(request.getPassword());

    User user = new User();
    user.setFirstName(request.getFirstName());
    user.setLastName(request.getLastName());
    user.setEmail(request.getEmail());
    // Encode password before saving to database
    user.setPassword(passwordEncoder.encode(request.getPassword()));
    user.setRole(Role.USER); // Default role for new users
    
    return userRepo.save(user);
  }

  @Transactional(readOnly = true)
  public Optional<User> getUserById(@NonNull UUID id) {
    return userRepo.findById(id);
  }

  @Transactional(readOnly = true)
  public Optional<User> getUserByEmail(@NonNull String email) {
    return userRepo.findByEmail(email);
  }

  @Transactional(readOnly = true)
  public Optional<User> getVerifiedUserById(@NonNull UUID id) {
    return userRepo.findByIdAndVerifiedAtIsNotNull(id);
  }

  @Transactional(readOnly = true)
  public Optional<User> getVerifiedUserByEmail(@NonNull String email) {
    return userRepo.findByEmailAndVerifiedAtIsNotNull(email);
  }

  /**
   * Get the currently authenticated user from the security context
   */
  @Transactional(readOnly = true)
  public User getCurrentUser() {
    User currentUser = SecurityUtil.getCurrentUser();
    
    if (currentUser == null) {
      throw new IllegalStateException("No authenticated user found in security context");
    }
    
    return currentUser;
  }

  @Transactional
  public void updateLastLogin(@NonNull UUID userId) {
    userRepo.findById(userId).ifPresent(user -> {
      user.setLastLogin(LocalDateTime.now());
      userRepo.save(user);
    });
  }

  @Transactional
  public User updateUser(@NonNull User user) {
    return userRepo.save(user);
  }

  @Transactional
  public void deleteUser(@NonNull UUID userId) {
    userRepo.deleteById(userId);
  }

  /**
   * Change a user's role
   * Validates permissions based on requester's role
   */
  @Transactional
  public User changeUserRole(@NonNull UUID userId, @NonNull Role newRole, @NonNull UUID requesterId) {
    User targetUser = userRepo.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("Target user not found"));
    
    User requester = userRepo.findById(requesterId)
        .orElseThrow(() -> new IllegalArgumentException("Requester not found"));
    
    // System roles (SUDOER, STAFF) can change anyone except SUDOER
    if (requester.getRole().isSystemRole()) {
      if (requester.getRole() == Role.STAFF && targetUser.getRole() == Role.SUDOER) {
        throw new IllegalStateException("STAFF cannot change SUDOER's role");
      }
      targetUser.setRole(newRole);
      return userRepo.save(targetUser);
    }
    
    // Org roles: must be in same organization
    if (requester.getOrganization() == null || targetUser.getOrganization() == null ||
        !requester.getOrganization().getId().equals(targetUser.getOrganization().getId())) {
      throw new IllegalStateException("Can only change roles within your organization");
    }
    
    // OWNER can change anyone in org
    if (requester.getRole() == Role.OWNER) {
      if (!newRole.isOrgRole()) {
        throw new IllegalArgumentException("Can only assign organization roles");
      }
      targetUser.setRole(newRole);
      return userRepo.save(targetUser);
    }
    
    // ADMIN can only change USER roles
    if (requester.getRole() == Role.ADMIN) {
      if (targetUser.getRole() != Role.USER) {
        throw new IllegalStateException("ADMIN can only change USER roles");
      }
      if (newRole != Role.USER && newRole != Role.ADMIN) {
        throw new IllegalArgumentException("ADMIN can only assign USER or ADMIN roles");
      }
      targetUser.setRole(newRole);
      return userRepo.save(targetUser);
    }
    
    throw new IllegalStateException("You do not have permission to change roles");
  }

  /**
   * Remove a user from their organization
   */
  @Transactional
  public void removeUserFromOrg(@NonNull UUID userId, @NonNull UUID requesterId) {
    User targetUser = userRepo.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("Target user not found"));
    
    User requester = userRepo.findById(requesterId)
        .orElseThrow(() -> new IllegalArgumentException("Requester not found"));
    
    // Cannot remove OWNER
    if (targetUser.getRole() == Role.OWNER) {
      throw new IllegalStateException("Cannot remove OWNER. Transfer ownership first.");
    }
    
    // Validate requester has permission
    if (requester.getRole() == Role.OWNER || requester.getRole() == Role.ADMIN) {
      if (requester.getOrganization() == null || targetUser.getOrganization() == null ||
          !requester.getOrganization().getId().equals(targetUser.getOrganization().getId())) {
        throw new IllegalStateException("Can only remove users from your organization");
      }
      
      // ADMIN cannot remove other ADMINs or OWNER
      if (requester.getRole() == Role.ADMIN && targetUser.getRole() != Role.USER) {
        throw new IllegalStateException("ADMIN can only remove USER roles");
      }
    } else if (!requester.getRole().isSystemRole()) {
      throw new IllegalStateException("Only OWNER or ADMIN can remove users");
    }
    
    targetUser.setOrganization(null);
    targetUser.setRole(Role.USER);
    targetUser.setJoinedAt(null);
    userRepo.save(targetUser);
  }

  /**
   * Transfer organization ownership
   */
  @Transactional
  public void transferOwnership(@NonNull UUID currentOwnerId, @NonNull UUID newOwnerId) {
    User currentOwner = userRepo.findById(currentOwnerId)
        .orElseThrow(() -> new IllegalArgumentException("Current owner not found"));
    
    User newOwner = userRepo.findById(newOwnerId)
        .orElseThrow(() -> new IllegalArgumentException("New owner not found"));
    
    // Validate current owner is actually the owner
    if (currentOwner.getRole() != Role.OWNER) {
      throw new IllegalStateException("You are not the owner");
    }
    
    // Validate both in same organization
    if (currentOwner.getOrganization() == null || newOwner.getOrganization() == null ||
        !currentOwner.getOrganization().getId().equals(newOwner.getOrganization().getId())) {
      throw new IllegalStateException("Both users must be in the same organization");
    }
    
    // Transfer ownership
    currentOwner.setRole(Role.ADMIN);
    newOwner.setRole(Role.OWNER);
    
    userRepo.save(currentOwner);
    userRepo.save(newOwner);
  }
}
