package ma.lsia.certis.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import ma.lsia.certis.dto.OrganizationResponse;
import ma.lsia.certis.entities.User;
import ma.lsia.certis.exception.UnauthorizedException;
import ma.lsia.certis.services.UserService;
import ma.lsia.certis.util.SecurityUtil;

@RestController
@RequestMapping("/organizations/current")
@Tag(name = "Organizations", description = "Organization management endpoints")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class OrganizationController {
  private final UserService userService;

  /**
   * Get current user's organization.
   * This is a custom endpoint for non-CRUD operations.
   * Standard CRUD operations are handled by Spring Data REST at /data/organizations
   */
  @Operation(summary = "Get current organization", description = "Retrieve the authenticated user's organization")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Organization retrieved successfully",
      content = @Content(schema = @Schema(implementation = OrganizationResponse.class))),
    @ApiResponse(responseCode = "401", description = "Unauthorized"),
    @ApiResponse(responseCode = "404", description = "User not associated with any organization")
  })
  @GetMapping("")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<OrganizationResponse> getCurrentOrganization() {
    String userEmail = SecurityUtil.getCurrentUserEmail();
    if (userEmail == null) {
      throw new UnauthorizedException("User not authenticated");
    }
    
    User user = userService.getUserByEmail(userEmail)
        .orElseThrow(() -> new UnauthorizedException("User not found"));
    
    if (user.getOrganization() == null) {
      return ResponseEntity.notFound().build();
    }
    
    return ResponseEntity.ok(OrganizationResponse.fromOrganization(user.getOrganization()));
  }
}
