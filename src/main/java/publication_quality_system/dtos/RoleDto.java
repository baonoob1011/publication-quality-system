package publication_quality_system.dtos;

import lombok.Data;
import publication_quality_system.enums.RoleName;

@Data
public class RoleDto {
    private Long id;
    private RoleName name;
    private String description;
}
