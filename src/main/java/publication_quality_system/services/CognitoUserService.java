package publication_quality_system.services;

public interface CognitoUserService {
    boolean existsByEmail(String email);

    boolean ensureDefaultAdminUser(String email, String password);
}
