package ma.lsia.certis.repos;

import ma.lsia.certis.entities.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface CourseRepository extends JpaRepository<Course, UUID> {
  // Custom queries if needed
}
