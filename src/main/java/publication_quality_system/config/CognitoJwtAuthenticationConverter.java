package publication_quality_system.config;

import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import publication_quality_system.entities.Permission;
import publication_quality_system.entities.Role;
import publication_quality_system.entities.User;
import publication_quality_system.repositories.UserRepository;
import publication_quality_system.security.CurrentUser;
import publication_quality_system.security.JwtService;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class CognitoJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private static final String ROLE_PREFIX = "ROLE_";
    private static final String SCOPE_PREFIX = "SCOPE_";

    private final UserRepository userRepository;
    private final JwtService jwtService;

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        String email = jwtService.getEmail(jwt);
        String sub = jwtService.getSub(jwt);
        Optional<User> localUser = findLocalUser(email);
        String fullName = localUser.map(User::getFullName).orElse(null);

        Set<GrantedAuthority> authorities = new HashSet<>();
        addGroupAuthorities(jwt, authorities);
        addScopeAuthorities(jwt, authorities);
        addLocalPermissionAuthorities(localUser, authorities);

        JwtAuthenticationToken authenticationToken = new JwtAuthenticationToken(jwt, authorities, resolvePrincipalName(email, sub));
        authenticationToken.setDetails(new CurrentUser(fullName, email, sub, authorities));
        return authenticationToken;
    }

    private void addGroupAuthorities(Jwt jwt, Set<GrantedAuthority> authorities) {
        jwtService.getGroups(jwt).stream()
                .filter(group -> group != null && !group.isBlank())
                .map(group -> group.startsWith(ROLE_PREFIX) ? group : ROLE_PREFIX + group)
                .map(SimpleGrantedAuthority::new)
                .forEach(authorities::add);
    }

    private void addScopeAuthorities(Jwt jwt, Set<GrantedAuthority> authorities) {
        String scope = jwt.getClaimAsString("scope");
        if (scope == null || scope.isBlank()) {
            return;
        }

        for (String item : scope.split(" ")) {
            if (!item.isBlank()) {
                authorities.add(new SimpleGrantedAuthority(SCOPE_PREFIX + item));
            }
        }
    }

    private void addLocalPermissionAuthorities(Optional<User> user, Set<GrantedAuthority> authorities) {
        user.map(User::getRoles)
                .stream()
                .flatMap(Collection::stream)
                .map(Role::getPermissions)
                .flatMap(Collection::stream)
                .map(Permission::getName)
                .filter(permission -> permission != null && !permission.isBlank())
                .map(SimpleGrantedAuthority::new)
                .forEach(authorities::add);
    }

    private Optional<User> findLocalUser(String email) {
        if (email != null && !email.isBlank()) {
            return userRepository.findByEmail(email);
        }
        return Optional.empty();
    }

    private String resolvePrincipalName(String email, String sub) {
        if (email != null && !email.isBlank()) {
            return email;
        }
        return sub;
    }
}
