package ma.lsia.certis.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

import ma.lsia.certis.entities.Certificate;
import ma.lsia.certis.entities.Course;
import ma.lsia.certis.entities.Organization;
import ma.lsia.certis.entities.User;

/**
 * Configuration for Spring Data REST.
 * 
 * This class customizes the behavior of auto-generated REST endpoints:
 * - Exposes entity IDs in JSON responses
 * - Sets base path for REST endpoints
 * - Configures CORS settings
 */
@Configuration
public class RestConfiguration implements RepositoryRestConfigurer {

  @Override
  public void configureRepositoryRestConfiguration(
      RepositoryRestConfiguration config, 
      CorsRegistry cors) {
    
    // Expose IDs for all entities in JSON responses
    // This makes it easier for clients to work with entities
    config.exposeIdsFor(
      User.class,
      Organization.class,
      Course.class,
      Certificate.class
    );
    
    // Set base path for Spring Data REST endpoints
    // All auto-generated endpoints will be under /data
    // e.g., /data/users, /data/courses, /data/certificates, /data/organizations
    // config.setBasePath("/data");
    
    // Return body on create (POST) and update (PUT/PATCH)
    // This avoids the need for clients to make a second GET request
    config.setReturnBodyOnCreate(true);
    config.setReturnBodyOnUpdate(true);
    
    // Set default page size for paginated responses
    config.setDefaultPageSize(20);
    config.setMaxPageSize(100);
  }
}
