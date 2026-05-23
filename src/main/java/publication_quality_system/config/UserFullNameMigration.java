package publication_quality_system.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserFullNameMigration {

    private final JdbcTemplate jdbcTemplate;

    public void migrate() {
        try {
            jdbcTemplate.execute("ALTER TABLE users ADD COLUMN IF NOT EXISTS full_name VARCHAR(255)");
            jdbcTemplate.execute("UPDATE users SET full_name = username WHERE full_name IS NULL AND username IS NOT NULL");
            jdbcTemplate.execute("UPDATE users SET full_name = email WHERE full_name IS NULL AND email IS NOT NULL");
            jdbcTemplate.execute("ALTER TABLE users ALTER COLUMN full_name SET NOT NULL");
        } catch (RuntimeException exception) {
            log.warn("User full_name migration was skipped or failed: {}", exception.getMessage());
        }
    }
}
