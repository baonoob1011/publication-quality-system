package publication_quality_system.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Order(0)
public class RoleSchemaConstraintFixer implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) {
        jdbcTemplate.execute("""
                DO $$
                DECLARE
                    constraint_record RECORD;
                BEGIN
                    IF to_regclass('public.roles') IS NOT NULL THEN
                        FOR constraint_record IN
                            SELECT conname
                            FROM pg_constraint
                            WHERE conrelid = 'public.roles'::regclass
                              AND contype = 'c'
                              AND pg_get_constraintdef(oid) LIKE '%name%'
                        LOOP
                            EXECUTE format('ALTER TABLE roles DROP CONSTRAINT IF EXISTS %I', constraint_record.conname);
                        END LOOP;
                    END IF;
                END $$;
                """);
    }
}
