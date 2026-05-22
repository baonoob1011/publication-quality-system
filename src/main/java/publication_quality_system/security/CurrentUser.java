package publication_quality_system.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Data
@AllArgsConstructor
public class CurrentUser {
    private String username;
    private String email;
    private String sub;
    private Collection<? extends GrantedAuthority> authorities;
}
