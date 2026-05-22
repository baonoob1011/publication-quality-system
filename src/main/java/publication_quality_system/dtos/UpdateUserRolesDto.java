package publication_quality_system.dtos;

import lombok.Data;

import java.util.Set;

@Data
public class UpdateUserRolesDto {
    private Set<Long> roleIds;
}
