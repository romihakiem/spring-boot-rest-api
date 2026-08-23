package com.skeleton.api.config;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;

/**
 * Spring Boot already performs graceful HTTP shutdown when
 * `server.shutdown=graceful` + `spring.lifecycle.timeout-per-shutdown-phase`
 * are set (see application.yml) -- Tomcat stops accepting new requests and
 * waits for in-flight ones to finish before the context closes.
 *
 * This class just makes the shutdown sequence observable and gives a single
 * place to release any extra resources (custom thread pools, schedulers,
 * open file handles, etc.) before the JVM exits.
 */
@Slf4j
@Configuration
public class GracefulShutdownConfig {
    @EventListener(ContextClosedEvent.class)
    public void onContextClosed(ContextClosedEvent event) {
        log.info(">>> Shutdown signal received. Waiting for in-flight requests to complete...");
    }

    @PreDestroy
    public void onDestroy() {
        log.info(">>> Application context is closing. Releasing resources...");
        // Close any custom resources here, e.g.:
        // customExecutorService.shutdown();
        // scheduledTaskRegistrar.destroy();
        log.info(">>> Cleanup complete. Bye!");
    }
}
