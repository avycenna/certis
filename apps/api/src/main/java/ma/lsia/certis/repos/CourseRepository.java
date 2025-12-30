package ma.lsia.certis.repos;

import ma.lsia.certis.entities.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Course entity with multitenant isolation.
 * 
 * Spring Data REST auto-generates endpoints at /courses
 * Custom queries enforce tenant filtering based on authenticated user's organization.
 */
@RepositoryRestResource
@PreAuthorize("isAuthenticated()")
public interface CourseRepository extends JpaRepository<Course, UUID> {
  
  /**
   * Find course by slug (no tenant filter - used for uniqueness checks).
   */
  Optional<Course> findBySlug(String slug);
  
  /**
   * Find all courses for the current user's organization.
   * Uses SpEL expression to inject organizationId from JWT token.
   */
  @Query("SELECT c FROM Course c WHERE c.organization.id = ?#{@jwtContextHolder.getOrganizationId()}")
  List<Course> findAllForCurrentOrg();
  
  /**
   * Find course by ID with tenant filtering.
   * Ensures users can only access courses from their organization.
   */
  @Query("SELECT c FROM Course c WHERE c.id = :id AND c.organization.id = ?#{@jwtContextHolder.getOrganizationId()}")
  Optional<Course> findByIdForCurrentOrg(@Param("id") UUID id);
  
  /**
   * Find active courses for the current user's organization.
   */
  @Query("SELECT c FROM Course c WHERE c.isActive = true AND c.organization.id = ?#{@jwtContextHolder.getOrganizationId()}")
  List<Course> findActiveCoursesForCurrentOrg();
  
  /**
   * Count courses in the current user's organization.
   */
  @Query("SELECT COUNT(c) FROM Course c WHERE c.organization.id = ?#{@jwtContextHolder.getOrganizationId()}")
  long countForCurrentOrg();
}
