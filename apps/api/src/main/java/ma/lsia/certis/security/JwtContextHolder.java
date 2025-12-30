package ma.lsia.certis.security;

import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

/**
 * Spring bean that extracts organization ID from JWT token in the current request context.
 * Used in SpEL expressions for tenant filtering in repository queries.
 * 
 * Example usage in repository:
 * @Query("SELECT c FROM Course c WHERE c.organization.id = ?#{@jwtContextHolder.getOrganizationId()}")
 */
@Component
@RequiredArgsConstructor
public class JwtContextHolder {

  private final HttpServletRequest request;
  private final JwtUtil jwtUtil;

  /**
   * Extracts organization ID from JWT token in the current HTTP request.
   * Used by SpEL expressions in repository queries for tenant isolation.
   * 
   * @return Organization UUID from JWT, or null if not authenticated or no organization
   */
  public UUID getOrganizationId() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    
    if (authentication == null || !authentication.isAuthenticated()) {
      return null;
    }

    // Extract JWT token from Authorization header
    String authHeader = request.getHeader("Authorization");
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      return null;
    }

    String token = authHeader.substring(7);
    
    try {
      return jwtUtil.extractOrganizationId(token);
    } catch (Exception e) {
      // Invalid token or missing organizationId claim
      return null;
    }
  }
}
