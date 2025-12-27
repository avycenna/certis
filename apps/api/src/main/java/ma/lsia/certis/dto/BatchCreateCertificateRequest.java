package ma.lsia.certis.dto;

import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request payload for creating multiple certificates in batch")
public class BatchCreateCertificateRequest {
  @Schema(description = "List of certificates to create (max 100)", example = "[{\"subject\":\"Java Developer\",\"activeFrom\":\"2025-01-01T00:00:00\",\"activeTo\":\"2025-12-31T23:59:59\"}]")
  @NotEmpty(message = "Certificates list cannot be empty")
  @Size(max = 100, message = "Cannot create more than 100 certificates at once")
  @Valid
  private List<CreateCertificateRequest> certificates;
}
