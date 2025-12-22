package ma.lsia.certis.dto;

import java.util.List;

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
public class BatchCreateCertificateRequest {
  @NotEmpty(message = "Certificates list cannot be empty")
  @Size(max = 100, message = "Cannot create more than 100 certificates at once")
  @Valid
  private List<CreateCertificateRequest> certificates;
}
