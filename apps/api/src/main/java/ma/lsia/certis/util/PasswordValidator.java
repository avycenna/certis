package ma.lsia.certis.util;

/**
 * Utility class for validating password strength.
 * Enforces security requirements for user passwords.
 */
public final class PasswordValidator {

  private PasswordValidator() {
    // Utility class - prevent instantiation
  }

  /**
   * Validates password strength according to security requirements.
   * 
   * Requirements:
   * - Minimum 8 characters
   * - At least one uppercase letter
   * - At least one lowercase letter
   * - At least one number
   * - At least one special character (@$!%*?&)
   * 
   * @param password The password to validate
   * @throws IllegalArgumentException if password does not meet requirements
   */
  public static void validate(String password) {
    if (password == null || password.length() < 8) {
      throw new IllegalArgumentException("Password must be at least 8 characters long");
    }
    
    if (!password.matches(".*[A-Z].*")) {
      throw new IllegalArgumentException("Password must contain at least one uppercase letter");
    }
    
    if (!password.matches(".*[a-z].*")) {
      throw new IllegalArgumentException("Password must contain at least one lowercase letter");
    }
    
    if (!password.matches(".*\\d.*")) {
      throw new IllegalArgumentException("Password must contain at least one number");
    }
    
    if (!password.matches(".*[@$!%*?&].*")) {
      throw new IllegalArgumentException("Password must contain at least one special character (@$!%*?&)");
    }
  }
}
