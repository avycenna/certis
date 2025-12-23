package ma.lsia.certis.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class CreateCourseRequest {
    private String title;
    private String description;
    private String slug;
    private Boolean isActive = true;
    private UUID orgId;
}
