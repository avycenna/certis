package ma.lsia.certis.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response payload for batch certificate creation")
public class BatchCertificateResponse {
  @Schema(description = "Total number of certificates requested", example = "10")
  private int totalRequested;

  @Schema(description = "Number of certificates successfully created", example = "8")
  private int successfullyCreated;

  @Schema(description = "Number of certificates that failed to be created", example = "2")
  private int failed;

  @Schema(description = "List of successfully created certificates")
  private List<CertificateResponse> certificates;

  @Schema(description = "List of errors for failed certificate creations")
  private List<BatchError> errors;

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  @Schema(description = "Error details for a failed certificate in the batch")
  public static class BatchError {
    @Schema(description = "Index of the failed certificate in the batch", example = "3")
    private int index;

    @Schema(description = "Subject of the failed certificate", example = "Java Developer Certificate")
    private String subject;

    @Schema(description = "Error message for the failure", example = "Subject is required")
    private String error;
  }

  public BatchCertificateResponse(int totalRequested) {
    this.totalRequested = totalRequested;
    this.successfullyCreated = 0;
    this.failed = 0;
    this.certificates = new ArrayList<>();
    this.errors = new ArrayList<>();
  }

  public void addSuccess(CertificateResponse cert) {
    this.certificates.add(cert);
    this.successfullyCreated++;
  }

  public void addError(int index, String subject, String error) {
    this.errors.add(new BatchError(index, subject, error));
    this.failed++;
  }
}
