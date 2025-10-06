package com.sources.app.metrics;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Counter;
import io.prometheus.client.hotspot.DefaultExports;

/**
 * Centralizes the Prometheus metrics definitions for Ensurance backend (backv4).
 */
public final class MetricsConfiguration {

    private static final CollectorRegistry REGISTRY = CollectorRegistry.defaultRegistry;

    public static final Counter DB_QUERIES_TOTAL = Counter.build()
            .name("ensurance_db_queries_total")
            .help("Total database queries executed by the Ensurance backend")
            .labelNames("operation", "entity", "status")
            .register(REGISTRY);

    private static volatile boolean initialized = false;

    private MetricsConfiguration() {
        // utility class
    }

    /**
     * Initializes the default JVM and system metrics exactly once.
     */
    public static synchronized void initialize() {
        if (!initialized) {
            DefaultExports.initialize();
            initialized = true;
        }
    }
}
