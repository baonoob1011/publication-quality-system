package publication_quality_system.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequestDto {
    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password cannot be empty")
    private String password;

    @NotBlank(message = "Full name cannot be empty")
    @Size(max = 255, message = "Full name cannot exceed 255 characters")
    private String fullName;
}
