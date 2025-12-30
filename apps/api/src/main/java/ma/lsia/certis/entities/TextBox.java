package ma.lsia.certis.entities;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import io.swagger.v3.oas.annotations.media.Schema;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Represents a text box layout for certificates")
public class TextBox {

  @Schema(description = "X coordinate of the text box", example = "100")
  private Integer x;

  @Schema(description = "Y coordinate of the text box", example = "200")
  private Integer y;

  @Schema(description = "Width of the text box", example = "300")
  private Integer width;

  @Schema(description = "Height of the text box", example = "50")
  private Integer height;
}
