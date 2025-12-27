package ma.lsia.certis.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request payload for creating a new organization")
public class CreateOrganizationRequest {

  @Schema(description = "Name of the organization", example = "Certis Academy")
  @NotBlank(message = "Organization name is required")
  @Size(min = 3, max = 50, message = "Organization name must be between 3 and 50 characters")
  private String name;
  

  @Schema(description = "Description of the organization", example = "A platform for managing certificates and courses.")
  @Size(max = 1024, message = "Description cannot exceed 1024 characters")
  private String description;


  @Schema(description = "Domain of the organization (must be a valid domain name)", example = "certis.com")
  @NotBlank(message = "Domain is required")
  @Size(min = 3, max = 255, message = "Domain must be between 3 and 255 characters")
  @Pattern(
    regexp = "^(?:[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?\\.)+[a-zA-Z]{2,}$",
    message = "Domain must be a valid domain name (e.g., example.com, subdomain.example.com)"
  )
  private String domain;
}
