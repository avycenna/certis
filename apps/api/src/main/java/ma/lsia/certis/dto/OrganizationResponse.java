package ma.lsia.certis.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ma.lsia.certis.entities.Organization;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationResponse {
  private UUID id;
  private String name;
  private String description;
  private String domain;
  private UUID ownerId;
  private String ownerEmail;
  private Integer userCount;
  private LocalDateTime createdAt;
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
