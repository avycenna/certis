package ma.lsia.certis.services;

import ma.lsia.certis.entities.Course;
import ma.lsia.certis.entities.Organization;
import ma.lsia.certis.entities.User;
import ma.lsia.certis.repos.CourseRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CourseService {
  private final CourseRepository courseRepository;

  public CourseService(CourseRepository courseRepository) {
    this.courseRepository = courseRepository;
  }

  public List<Course> getCoursesByOrganization(UUID orgId) {
    return courseRepository.findAll().stream()
        .filter(c -> c.getOrganization().getId().equals(orgId))
        .toList();
  }

  public Optional<Course> getCourse(UUID courseId) {
    if (courseId == null) {
      return Optional.empty();
    }
    return courseRepository.findById(courseId);
  }

  @Transactional
  public Course createCourse(Course course, User creator) {
    // Only org admins/owners can create courses
    Organization org = course.getOrganization();
    if (!isAdminOrOwner(creator, org)) {
      throw new AccessDeniedException("Only org admins/owners can create courses");
    }
    course.setCreatedBy(creator);
    return courseRepository.save(course);
  }

  @Transactional
  public void deleteCourse(UUID courseId, User requester) {
    if (courseId == null) {
      throw new IllegalArgumentException("Course ID must not be null");
    }
    Course course = courseRepository.findById(courseId)
        .orElseThrow(() -> new IllegalArgumentException("Course not found"));
    Organization org = course.getOrganization();
    if (!isAdminOrOwner(requester, org)) {
      throw new AccessDeniedException("Only org admins/owners can delete courses");
    }
    if (course.getCertificates() != null && !course.getCertificates().isEmpty()) {
      throw new IllegalStateException("Cannot delete course with existing certificates");
    }
    courseRepository.delete(course);
  }

  private boolean isAdminOrOwner(User user, Organization org) {
    // Implement your logic for admin/owner check
    return org.getOwner().getId().equals(user.getId()) ||
        (user.getRole() != null && user.getRole().name().equals("ADMIN"));
  }
}
