package ma.lsia.certis.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import ma.lsia.certis.entities.User;
import ma.lsia.certis.util.SecurityUtil;

import java.util.Optional;

/**
 * Configuration for JPA Auditing.
 * Enables automatic population of @CreatedBy and @LastModifiedBy fields.
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class JpaAuditingConfig {

  /**
   * Provides the current auditor (authenticated user) for JPA auditing.
   * Used to automatically populate @CreatedBy and @LastModifiedBy fields.
   * 
   * @return AuditorAware<User> bean that returns the current authenticated user
   */
  @Bean
  @SuppressWarnings("null")
  public AuditorAware<User> auditorProvider() {
    return () -> {
      User currentUser = SecurityUtil.getCurrentUser();
      return Optional.ofNullable(currentUser);
    };
  }
}
