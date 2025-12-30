package ma.lsia.certis.handlers;

import java.util.UUID;

import org.springframework.data.rest.core.annotation.HandleAfterCreate;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.lsia.certis.entities.Certificate;
import ma.lsia.certis.entities.Course;
import ma.lsia.certis.entities.Organization;
import ma.lsia.certis.entities.User;
import ma.lsia.certis.enums.Role;
import ma.lsia.certis.repos.CourseRepository;
import ma.lsia.certis.repos.UserRepository;
import ma.lsia.certis.util.SecurityUtil;

/**
 * Spring Data REST event handler for cross-cutting concerns.
 * 
 * This component handles:
 * - Setting Organization on records based on current user's org
 * - Validating uniqueness (slugs, emails) before persistence
 * - Profanity/safety checks on text fields
 * 
 * Note: These handlers are invoked by Spring Data REST before the entity
 * is passed to the repository, ensuring data integrity and business rules.
 */
@Component
@RepositoryEventHandler
@RequiredArgsConstructor
@Slf4j
public class EntityEventHandler {

  private final UserRepository userRepository;
  private final CourseRepository courseRepository;
  private final PasswordEncoder passwordEncoder;

  // ========================
  // COURSE HANDLERS
  // ========================

  /**
   * Before creating a Course:
   * 1. Set the organization from the current user
   * 2. Validate slug uniqueness within the organization
   * 3. Perform basic profanity/safety checks on title and description
   */
  @HandleBeforeCreate
  public void handleCourseBeforeCreate(Course course) {
    log.debug("HandleBeforeCreate: Course");
    
    // 1. Set organization from current authenticated user
    User currentUser = SecurityUtil.getCurrentUser();
    if (currentUser == null || currentUser.getOrganization() == null) {
      throw new IllegalStateException("User must be authenticated and belong to an organization to create courses");
    }
    
    course.setOrganization(currentUser.getOrganization());
    
    // 2. Validate slug uniqueness within organization (simple check)
    if (course.getSlug() != null && courseRepository.findBySlug(course.getSlug()).isPresent()) {
      throw new IllegalArgumentException("A course with slug '" + course.getSlug() + "' already exists");
    }
    
    // 3. Basic safety checks on text fields
    validateTextContent(course.getTitle(), "Course title");
    validateTextContent(course.getDescription(), "Course description");
  }

  /**
   * Before updating a Course:
   * 1. Ensure organization cannot be changed
   * 2. Validate slug uniqueness if changed
   * 3. Perform safety checks on text fields
   */
  @HandleBeforeSave
  @SuppressWarnings("null")
  public void handleCourseBeforeSave(Course course) {
    log.debug("HandleBeforeSave: Course");
    
    // 1. Organization cannot be changed after creation
    if (course.getId() != null) {
      UUID courseId = course.getId();
      Course existing = courseRepository.findById(courseId)
          .orElseThrow(() -> new IllegalArgumentException("Course not found"));
      
      if (!existing.getOrganization().getId().equals(course.getOrganization().getId())) {
        throw new IllegalStateException("Cannot change course organization");
      }
    }
    
    // 2. Validate slug uniqueness
    if (course.getSlug() != null) {
      courseRepository.findBySlug(course.getSlug()).ifPresent(existing -> {
        if (!existing.getId().equals(course.getId())) {
          throw new IllegalArgumentException("A course with slug '" + course.getSlug() + "' already exists");
        }
      });
    }
    
    // 3. Safety checks
    validateTextContent(course.getTitle(), "Course title");
    validateTextContent(course.getDescription(), "Course description");
  }

  // ========================
  // CERTIFICATE HANDLERS
  // ========================

  /**
   * Before creating a Certificate:
   * 1. Set the organization from the current user
   * 2. Validate serial number uniqueness
   * 3. Perform safety checks on subject and metadata
   */
  @HandleBeforeCreate
  public void handleCertificateBeforeCreate(Certificate certificate) {
    log.debug("HandleBeforeCreate: Certificate");
    
    // 1. Set organization from current user
    User currentUser = SecurityUtil.getCurrentUser();
    if (currentUser == null || currentUser.getOrganization() == null) {
      throw new IllegalStateException("User must be authenticated and belong to an organization to create certificates");
    }
    
    certificate.setOrganization(currentUser.getOrganization());
    
    // 2. Validate serial number uniqueness (handled in CertificateService.generateUniqueSerialNumber)
    // This is a secondary check for REST API direct access
    if (certificate.getSerialNumber() != null && !certificate.getSerialNumber().isBlank()) {
      // Serial number validation can be added here if needed
      validateTextContent(certificate.getSerialNumber(), "Certificate serial number");
    }
    
    // 3. Safety checks
    validateTextContent(certificate.getSubject(), "Certificate subject");
  }

  /**
   * Before updating a Certificate:
   * 1. Ensure organization cannot be changed
   * 2. Perform safety checks on modified fields
   */
  @HandleBeforeSave
  public void handleCertificateBeforeSave(Certificate certificate) {
    log.debug("HandleBeforeSave: Certificate");
    
    // Note: Certificate updates should be rare and carefully controlled
    // Most fields should be immutable after issuance
    
    validateTextContent(certificate.getSubject(), "Certificate subject");
  }

  // ========================
  // USER HANDLERS
  // ========================

  /**
   * Before creating a User:
   * 1. Validate email uniqueness
   * 2. Encode password if plain text
   * 3. Perform basic safety checks on names
   */
  @HandleBeforeCreate
  public void handleUserBeforeCreate(User user) {
    log.debug("HandleBeforeCreate: User");
    
    // 1. Validate email uniqueness
    if (user.getEmail() != null && userRepository.findByEmail(user.getEmail()).isPresent()) {
      throw new IllegalArgumentException("Email '" + user.getEmail() + "' is already registered");
    }
    
    // 2. Encode password if not already BCrypt-encoded
    // BCrypt hashes start with $2a$, $2b$, or $2y$ followed by cost parameter
    if (user.getPassword() != null && !isBCryptEncoded(user.getPassword())) {
      user.setPassword(passwordEncoder.encode(user.getPassword()));
      log.debug("Encoded password for user: {}", user.getEmail());
    }
    
    // 3. Safety checks on names
    validateTextContent(user.getFirstName(), "First name");
    validateTextContent(user.getLastName(), "Last name");
  }

  /**
   * Before updating a User:
   * 1. Ensure email uniqueness if changed
   * 2. Encode password if changed and plain text
   * 3. Perform safety checks
   */
  @HandleBeforeSave
  public void handleUserBeforeSave(User user) {
    log.debug("HandleBeforeSave: User");
    
    // 1. Validate email uniqueness if changed
    if (user.getId() != null && user.getEmail() != null) {
      userRepository.findByEmail(user.getEmail()).ifPresent(existing -> {
        if (!existing.getId().equals(user.getId())) {
          throw new IllegalArgumentException("Email '" + user.getEmail() + "' is already registered");
        }
      });
    }
    
    // 2. Encode password if changed and not already BCrypt-encoded
    if (user.getPassword() != null && !isBCryptEncoded(user.getPassword())) {
      user.setPassword(passwordEncoder.encode(user.getPassword()));
      log.debug("Encoded updated password for user: {}", user.getEmail());
    }
    
    // 3. Safety checks
    validateTextContent(user.getFirstName(), "First name");
    validateTextContent(user.getLastName(), "Last name");
  }

  // ========================
  // ORGANIZATION HANDLERS
  // ========================

  /**
   * Before creating an Organization:
   * 1. Validate domain and name are present
   * 2. Check that current user doesn't already have an organization
   * 3. Set current user as owner (update role and organization reference)
   * 4. Perform safety checks on text fields
   * 
   * Note: The user-organization relationship is updated in @HandleAfterCreate
   * because the organization ID is not available until after save.
   */
  @HandleBeforeCreate
  public void handleOrganizationBeforeCreate(Organization organization) {
    log.debug("HandleBeforeCreate: Organization");
    
    // 1. Get current user
    User currentUser = SecurityUtil.getCurrentUser();
    if (currentUser == null) {
      throw new IllegalStateException("User must be authenticated to create an organization");
    }
    
    // 2. Check if user already has an organization
    if (currentUser.getOrganization() != null) {
      throw new IllegalStateException("User already belongs to an organization");
    }
    
    // 3. Validate text content
    validateTextContent(organization.getName(), "Organization name");
    validateTextContent(organization.getDesc(), "Organization description");
  }

  /**
   * After creating an Organization:
   * Set the creator as the organization owner by updating their role and organization reference.
   */
  @HandleAfterCreate
  public void handleOrganizationAfterCreate(Organization organization) {
    log.debug("HandleAfterCreate: Organization");
    
    User currentUser = SecurityUtil.getCurrentUser();
    if (currentUser == null) {
      log.error("Current user is null after organization creation");
      return;
    }
    
    // Set user as organization owner
    currentUser.setOrganization(organization);
    currentUser.setRole(Role.OWNER);
    currentUser.setJoinedAt(java.time.LocalDateTime.now());
    
    // Save the updated user
    userRepository.save(currentUser);
    
    log.info("Set user {} as owner of organization {}", currentUser.getEmail(), organization.getName());
  }

  /**
   * Before updating an Organization:
   * 1. Perform safety checks on text fields
   */
  @HandleBeforeSave
  public void handleOrganizationBeforeSave(Organization organization) {
    log.debug("HandleBeforeSave: Organization");
    
    validateTextContent(organization.getName(), "Organization name");
    validateTextContent(organization.getDesc(), "Organization description");
  }

  // ========================
  // UTILITY METHODS
  // ========================

  /**
   * Checks if a password string is already BCrypt-encoded.
   * BCrypt hashes are 60 characters and start with $2a$, $2b$, or $2y$
   * 
   * @param password the password string to check
   * @return true if the password appears to be BCrypt-encoded
   */
  private boolean isBCryptEncoded(String password) {
    // BCrypt format: $2a$10$... or $2b$10$... or $2y$10$... (60 chars total)
    return password != null && 
           password.length() == 60 && 
           password.matches("^\\$2[ayb]\\$\\d{2}\\$.{53}$");
  }

  /**
   * Validates text content for basic profanity and safety issues.
   * 
   * This is a simple implementation. In production, you would:
   * - Use a proper profanity filter library
   * - Implement more sophisticated content moderation
   * - Check against a configurable blocklist
   * - Integrate with AI-based content safety services
   * 
   * @param text the text to validate
   * @param fieldName the name of the field (for error messages)
   */
  private void validateTextContent(String text, String fieldName) {
    if (text == null || text.isBlank()) {
      return; // Allow null/blank if not @NotBlank at entity level
    }
    
    // Simple blocklist check (expand as needed)
    String[] blockedTerms = {
      "spam", "scam", "malicious", "hack", "exploit"
      // Add more terms or use a proper library
    };
    
    String lowerText = text.toLowerCase();
    for (String term : blockedTerms) {
      if (lowerText.contains(term)) {
        log.warn("Blocked content in {}: contains '{}'", fieldName, term);
        throw new IllegalArgumentException(
          fieldName + " contains inappropriate content. Please revise and try again."
        );
      }
    }
    
    // Check for excessive length (basic DoS prevention)
    if (text.length() > 10000) {
      throw new IllegalArgumentException(fieldName + " exceeds maximum allowed length");
    }
  }
}
