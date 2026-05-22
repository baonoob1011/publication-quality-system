package publication_quality_system.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import publication_quality_system.entities.Permission;
import publication_quality_system.entities.Role;
import publication_quality_system.entities.User;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();

        // Map roles and permissions to GrantedAuthority
        for (Role role : user.getRoles()) {
            // Include role itself if needed (prefixed with ROLE_)
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName().name()));

            // Map specific permissions into authorities (so hasAuthority('PAPER_CREATE')
            // works)
            for (Permission p : role.getPermissions()) {
                authorities.add(new SimpleGrantedAuthority(p.getName().name()));
            }
        }

        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return !user.isDeleted(); // Example soft delete check
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return !user.isDeleted();
    }

    public User getUser() {
        return user;
    }
}
