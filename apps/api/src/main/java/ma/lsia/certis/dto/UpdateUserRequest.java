package ma.lsia.certis.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request payload for updating user profile information")
public class UpdateUserRequest {

  @Schema(description = "User's new first name", example = "Jane")
  private String firstName;

  @Schema(description = "User's new last name", example = "Smith")
  private String lastName;
}
