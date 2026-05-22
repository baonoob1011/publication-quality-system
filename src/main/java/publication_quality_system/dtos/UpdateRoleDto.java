package publication_quality_system.dtos;

import lombok.Data;

import java.util.Set;

@Data
public class UpdateRoleDto {
    private String name;
    private String description;
    private Set<Long> permissionIds;
}
