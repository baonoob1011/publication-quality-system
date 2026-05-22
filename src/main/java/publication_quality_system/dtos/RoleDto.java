package publication_quality_system.lab_member.dtos;

import lombok.Data;
import publication_quality_system.lab_member.enums.SystemRole;

@Data
public class RoleDto {
    private Long id;
    private SystemRole name;
    private String description;
}
