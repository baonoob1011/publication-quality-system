package publication_quality_system.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import publication_quality_system.base.BaseDataSeeder;

import java.util.Comparator;
import java.util.List;

@Component
@RequiredArgsConstructor
@Order(1)
public class DataInitializer implements CommandLineRunner {

    private final List<BaseDataSeeder> seeders;
    private final UserFullNameMigration userFullNameMigration;

    @Override
    public void run(String... args) {

        try {
            userFullNameMigration.migrate();
            seeders.stream()
                    .sorted(Comparator.comparingInt(BaseDataSeeder::getOrder))
                    .forEach(BaseDataSeeder::seed);
        } finally {
            // SeederExecutionContext.exit();
        }
    }
}
