package ma.lsia.certis.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BatchCertificateResponse {
  private int totalRequested;
  private int successfullyCreated;
  private int failed;
  private List<CertificateResponse> certificates;
  private List<BatchError> errors;

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class BatchError {
    private int index;
    private String subject;
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
