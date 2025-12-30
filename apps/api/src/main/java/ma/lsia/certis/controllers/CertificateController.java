package ma.lsia.certis.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.lsia.certis.dto.BatchCertificateResponse;
import ma.lsia.certis.dto.BatchCreateCertificateRequest;
import ma.lsia.certis.security.RequiresOrganization;
import ma.lsia.certis.services.CertificateService;
import ma.lsia.certis.services.UserService;

@RestController
@RequestMapping("/certificates/batch")
@Tag(name = "certificate-entity-controller", description = "Certificate management endpoints")
@RequiredArgsConstructor
public class CertificateController {
  private final CertificateService certificateService;
  private final UserService userService;

  /**
   * Batch create certificates
   * @param BatchCreateCertificateRequest request
   * @param Authentication authentication
   * @return ResponseEntity<BatchCertificateResponse>
   */
  @Operation(summary = "Batch create certificates", 
             description = "Create multiple certificates at once (max 100). Returns details of successful and failed creations.")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Batch operation completed",
      content = @Content(schema = @Schema(implementation = BatchCertificateResponse.class))),
    @ApiResponse(responseCode = "400", description = "Invalid input or exceeds limit"),
    @ApiResponse(responseCode = "401", description = "User not authenticated")
  })
  @PostMapping("")
  @RequiresOrganization
  @PreAuthorize("isAuthenticated() and (hasRole('OWNER') or hasRole('ADMIN'))")
  public ResponseEntity<BatchCertificateResponse> batchCreateCertificates(
      @Valid @NonNull @RequestBody BatchCreateCertificateRequest request,
      Authentication authentication) {
    
    String email = userService.getCurrentUser().getEmail();
    BatchCertificateResponse response = certificateService.batchCreateCertificates(request, email);
    
    // Return 200 even if some failed - client can check the response for details
    return ResponseEntity.ok(response);
  }
}
