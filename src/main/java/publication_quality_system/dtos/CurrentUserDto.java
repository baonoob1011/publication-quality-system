package publication_quality_system.dtos;

import lombok.Data;

import java.util.List;

@Data
public class CurrentUserDto {
    private String username;
    private String email;
    private String sub;
    private List<String> authorities;
}
