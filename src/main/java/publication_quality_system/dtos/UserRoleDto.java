package publication_quality_system.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Set;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserRoleDto {
    private Long id;
    private String name;
    private String description;
    private Set<String> permissions;
}
