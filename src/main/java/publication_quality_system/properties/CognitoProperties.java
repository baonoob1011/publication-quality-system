package publication_quality_system.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "aws.cognito")
public class CognitoProperties {
    private String region;
    private String userPoolId;
    private String clientId;
    private String clientSecret;
    private String adminGroupName = "ADMIN";
}
