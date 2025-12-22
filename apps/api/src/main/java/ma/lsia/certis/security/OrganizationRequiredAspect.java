package ma.lsia.certis.security;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import ma.lsia.certis.entities.User;
import ma.lsia.certis.services.UserService;

@Aspect
@Component
@Slf4j
public class OrganizationRequiredAspect {
  private final UserService userService;

  public OrganizationRequiredAspect(UserService userService) {
    this.userService = userService;
  }

  @Before("@annotation(ma.lsia.certis.security.RequiresOrganization)")
  public void checkOrganizationExists() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    
    if (authentication == null || !authentication.isAuthenticated()) {
      throw new IllegalStateException("User must be authenticated");
    }

    String email = authentication.getName();
    if (email == null) {
      throw new IllegalStateException("Authenticated user email cannot be null");
    }
    User user = userService.getUserByEmail(email)
        .orElseThrow(() -> new IllegalStateException("User not found"));

    if (user.getOrganization() == null) {
      log.warn("User {} attempted to access organization-required endpoint without an organization", email);
      throw new IllegalStateException("User must create an organization before performing this action");
    }
  }
}
