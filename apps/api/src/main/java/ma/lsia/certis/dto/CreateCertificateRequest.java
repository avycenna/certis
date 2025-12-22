package ma.lsia.certis.dto;

import java.time.LocalDateTime;

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
public class CreateCertificateRequest {
  @NotBlank(message = "Subject is required")
  @Size(min = 3, max = 100, message = "Subject must be between 3 and 100 characters")
  private String subject;

  @NotNull(message = "Active from date is required")
  private LocalDateTime activeFrom;

  @NotNull(message = "Active to date is required")
  private LocalDateTime activeTo;
}
