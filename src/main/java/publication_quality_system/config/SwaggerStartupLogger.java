package publication_quality_system.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SwaggerStartupLogger {

    private final ServletWebServerApplicationContext webServerApplicationContext;

    public SwaggerStartupLogger(ServletWebServerApplicationContext webServerApplicationContext) {
        this.webServerApplicationContext = webServerApplicationContext;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void logSwaggerUrl() {
        int port = webServerApplicationContext.getWebServer().getPort();
        String contextPath = webServerApplicationContext.getServletContext().getContextPath();
        String swaggerUrl = "http://localhost:" + port + contextPath + "/swagger-ui/index.html";
        String rootUrl = "http://localhost:" + port + contextPath + "/";

        log.info("Swagger UI: {}", swaggerUrl);
        log.info("Application root redirects to Swagger UI: {}", rootUrl);
    }
}
