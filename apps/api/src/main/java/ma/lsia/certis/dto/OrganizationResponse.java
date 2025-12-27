package ma.lsia.certis.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ma.lsia.certis.entities.Organization;

import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response payload containing organization details")
public class OrganizationResponse {
  @Schema(description = "Unique identifier of the organization", example = "b3b7c8e2-1d2a-4c3e-9f2e-123456789abc")
  private UUID id;

  @Schema(description = "Name of the organization", example = "Certis Academy")
  private String name;

  @Schema(description = "Description of the organization", example = "A platform for managing certificates and courses.")
  private String description;

  @Schema(description = "Domain of the organization", example = "certis.com")
  private String domain;

  @Schema(description = "Unique identifier of the owner user", example = "b3b7c8e2-1d2a-4c3e-9f2e-123456789abc")
  private UUID ownerId;

  @Schema(description = "Email address of the owner user", example = "owner@certis.com")
  private String ownerEmail;

  @Schema(description = "Number of users in the organization", example = "42")
  private Integer userCount;

  @Schema(description = "Date the organization was created (ISO 8601 format)", example = "2025-01-01T00:00:00")
  private LocalDateTime createdAt;

  @Schema(description = "Date the organization was last updated (ISO 8601 format)", example = "2025-06-01T12:00:00")
  private LocalDateTime updatedAt;

  public static OrganizationResponse fromOrganization(Organization org) {
    OrganizationResponse response = new OrganizationResponse();
    response.setId(org.getId());
    response.setName(org.getName());
    response.setDescription(org.getDesc());
    response.setDomain(org.getDomain());
    response.setOwnerId(org.getOwner().getId());
    response.setOwnerEmail(org.getOwner().getEmail());
    response.setUserCount(org.getUsers() != null ? org.getUsers().size() : 0);
    response.setCreatedAt(org.getCreatedAt());
    response.setUpdatedAt(org.getUpdatedAt());
    return response;
  }
}
