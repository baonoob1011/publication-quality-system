package publication_quality_system.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LogoutRequestDto {
    @NotBlank(message = "Access token cannot be empty")
    private String accessToken;
}
