package publication_quality_system.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.util.Set;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto {
    private Long id;

    @NotBlank(message = "Full name cannot be empty")
    @Size(max = 255, message = "Full name cannot exceed 255 characters")
    private String fullName;

    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Invalid email format")
    private String email;

    private String password;

    private Set<RoleDto> roles;
}
