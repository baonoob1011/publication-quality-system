package publication_quality_system.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import publication_quality_system.base.BaseDataSeeder;

import java.util.Comparator;
import java.util.List;

@Component
@RequiredArgsConstructor
@Order(Ordered.LOWEST_PRECEDENCE)
public class DataInitializer implements CommandLineRunner {

    private final List<BaseDataSeeder> seeders;

    @Override
    public void run(String... args) {

        try {
            seeders.stream()
                    .sorted(Comparator.comparingInt(BaseDataSeeder::getOrder))
                    .forEach(BaseDataSeeder::seed);
        } finally {
            // SeederExecutionContext.exit();
        }
    }
}
