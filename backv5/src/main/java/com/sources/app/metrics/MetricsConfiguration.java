package com.sources.app.metrics;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Counter;
import io.prometheus.client.Gauge;
import io.prometheus.client.Histogram;
import io.prometheus.client.Summary;
import io.prometheus.client.hotspot.DefaultExports;

/**
 * Centralizes the Prometheus metrics definitions used across the application.
 */
public final class MetricsConfiguration {

    private static final CollectorRegistry REGISTRY = CollectorRegistry.defaultRegistry;

    public static final Counter HTTP_REQUESTS_TOTAL = Counter.build()
            .name("ensurance_http_requests_total")
            .help("Total HTTP requests handled by the API")
            .labelNames("path", "method", "status")
            .register(REGISTRY);

    public static final Histogram HTTP_REQUEST_DURATION_SECONDS = Histogram.build()
            .name("ensurance_http_request_duration_seconds")
            .help("Time spent processing HTTP requests")
            .labelNames("path", "method")
            .buckets(0.005, 0.01, 0.025, 0.05, 0.1, 0.25, 0.5, 1, 2.5, 5, 10)
            .register(REGISTRY);

    public static final Gauge HTTP_INFLIGHT_REQUESTS = Gauge.build()
            .name("ensurance_http_inflight_requests")
            .help("Number of HTTP requests currently being processed")
            .labelNames("path")
            .register(REGISTRY);

    public static final Summary HTTP_REQUEST_SIZE_BYTES = Summary.build()
            .name("ensurance_http_request_size_bytes")
            .help("Size of HTTP request bodies in bytes")
            .labelNames("path", "method")
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
