package publication_quality_system.services;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class JwtService {

    public String getUsername(Jwt jwt) {
        String username = jwt.getClaimAsString("username");
        if (username == null || username.isBlank()) {
            username = jwt.getClaimAsString("cognito:username");
        }
        if (username == null || username.isBlank()) {
            username = getEmail(jwt);
        }
        return username;
    }

    public String getEmail(Jwt jwt) {
        return jwt.getClaimAsString("email");
    }

    public String getSub(Jwt jwt) {
        return jwt.getSubject();
    }

    public List<String> getGroups(Jwt jwt) {
        List<String> groups = jwt.getClaimAsStringList("cognito:groups");
        return groups == null ? Collections.emptyList() : groups;
    }
}
