package ma.lsia.certis.dto;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Data
@Schema(description = "Request payload for creating a course")
public class CreateCourseRequest {
    @Schema(description = "Title of the course", example = "Advanced Java Programming")
    private String title;

    @Schema(description = "Description of the course", example = "Covers advanced Java topics including concurrency and streams.")
    private String description;

    @Schema(description = "URL-friendly identifier for the course", example = "advanced-java-programming")
    private String slug;

    @Schema(description = "Whether the course is active", example = "true")
    private Boolean isActive = true;

    @Schema(description = "ID of the organization that owns the course", example = "b3b7c8e2-1d2a-4c3e-9f2e-123456789abc")
    private UUID orgId;
}
