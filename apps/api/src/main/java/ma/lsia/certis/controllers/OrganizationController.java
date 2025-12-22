package ma.lsia.certis.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import ma.lsia.certis.dto.CreateOrganizationRequest;
import ma.lsia.certis.dto.OrganizationResponse;
import ma.lsia.certis.entities.Organization;
import ma.lsia.certis.entities.User;
import ma.lsia.certis.exception.UnauthorizedException;
import ma.lsia.certis.services.OrganizationService;
import ma.lsia.certis.services.UserService;
import ma.lsia.certis.util.SecurityUtil;

@RestController
@RequestMapping("/organizations")
@Tag(name = "Organizations", description = "Organization management endpoints")
@SecurityRequirement(name = "bearerAuth")
public class OrganizationController {
  private final OrganizationService organizationService;
  private final UserService userService;

  public OrganizationController(OrganizationService organizationService, UserService userService) {
    this.organizationService = organizationService;
    this.userService = userService;
  }

  /**
   * Create a new organization
   * @param CreateOrganizationRequest request
   * @return ResponseEntity<OrganizationResponse>
   */
  @Operation(summary = "Create organization", 
             description = "Create a new organization for the authenticated user. Users can only create one organization.")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "201", description = "Organization created successfully",
      content = @Content(schema = @Schema(implementation = OrganizationResponse.class))),
    @ApiResponse(responseCode = "400", description = "Invalid input or user already has an organization"),
    @ApiResponse(responseCode = "401", description = "User not authenticated"),
    @ApiResponse(responseCode = "409", description = "Domain already registered")
  })
  @PostMapping
  public ResponseEntity<OrganizationResponse> createOrganization(
      @Valid @NonNull @RequestBody CreateOrganizationRequest request) {
    
    String email = SecurityUtil.getCurrentUserEmail();
    if (email == null) {
      throw new UnauthorizedException("User not authenticated");
    }
    
    User user = userService.getUserByEmail(email)
        .orElseThrow(() -> new UnauthorizedException("User not found"));

    Organization org = organizationService.createOrganization(request, user);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(OrganizationResponse.fromOrganization(org));
  }

  /**
   * Get the authenticated user's organization
   * @return ResponseEntity<OrganizationResponse>
   */
  @Operation(summary = "Get my organization", 
             description = "Retrieve the authenticated user's organization")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Organization retrieved successfully",
      content = @Content(schema = @Schema(implementation = OrganizationResponse.class))),
    @ApiResponse(responseCode = "401", description = "User not authenticated"),
    @ApiResponse(responseCode = "404", description = "User has no organization")
  })
  @GetMapping("/me")
  public ResponseEntity<OrganizationResponse> getMyOrganization() {
    String email = SecurityUtil.getCurrentUserEmail();
    if (email == null) {
      throw new UnauthorizedException("User not authenticated");
    }
    
    User user = userService.getUserByEmail(email)
        .orElseThrow(() -> new UnauthorizedException("User not found"));

    return organizationService.getOrganizationByUser(user)
        .map(org -> ResponseEntity.ok(OrganizationResponse.fromOrganization(org)))
        .orElse(ResponseEntity.notFound().build());
  }
}
