package ma.lsia.certis.dto;

import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request payload for creating a certificate")
public class CreateCertificateRequest {

  @Schema(description = "Subject of the certificate", example = "Java Developer Certificate")
  @NotBlank(message = "Subject is required")
  @Size(min = 3, max = 100, message = "Subject must be between 3 and 100 characters")
  private String subject;


  @Schema(description = "Start date of certificate validity (ISO 8601 format)", example = "2025-01-01T00:00:00")
  @NotNull(message = "Active from date is required")
  private LocalDateTime activeFrom;


  @Schema(description = "End date of certificate validity (ISO 8601 format)", example = "2025-12-31T23:59:59")
  @NotNull(message = "Active to date is required")
  private LocalDateTime activeTo;
}
