package co.com.bancolombia.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Standard error response model")
public class ErrorResponse {

    @Schema(description = "Error code", example = "400")
    private String code;

    @Schema(description = "Error title or bad description", example = "Bad Request")
    private String message;

    @Schema(description = "Technical detail or specific error reason", example = "Field 'name' cannot be null")
    private String detail;
}
