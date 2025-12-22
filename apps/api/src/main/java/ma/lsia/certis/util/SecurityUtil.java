package ma.lsia.certis.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import ma.lsia.certis.entities.User;

@Component
public class SecurityUtil {

  /**
   * Get the currently authenticated user
   * @return User of authenticated user, or null if not authenticated
   */
  public static User getCurrentUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    
    if (authentication == null || !authentication.isAuthenticated()) {
      return null;
    }

    Object principal = authentication.getPrincipal();
    if (principal instanceof User) {
      return (User) principal;
    }
    
    return null;
  }

  /**
   * Get the email of the currently authenticated user
   * @return email of authenticated user, or null if not authenticated
   */
  public static String getCurrentUserEmail() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    
    if (authentication == null || !authentication.isAuthenticated()) {
      return null;
    }

    Object principal = authentication.getPrincipal();
    if (principal instanceof UserDetails) {
      return ((UserDetails) principal).getUsername();
    }
    
    return null;
  }

  /**
   * Check if there is an authenticated user
   * @return true if user is authenticated
   */
  public static boolean isAuthenticated() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    return authentication != null && authentication.isAuthenticated();
  }
}
