package publication_quality_system.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Set;

@Data
public class CreateRoleDto {
    @NotBlank(message = "Role name cannot be empty")
    private String name;

    private String description;

    private Set<Long> permissionIds;
}
