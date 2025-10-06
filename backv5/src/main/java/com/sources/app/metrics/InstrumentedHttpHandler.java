package com.sources.app.metrics;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import io.prometheus.client.Histogram;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

/**
 * Decorates {@link HttpHandler} instances with Prometheus metrics instrumentation.
 */
public final class InstrumentedHttpHandler implements HttpHandler {

    private final String pathLabel;
    private final HttpHandler delegate;

    private InstrumentedHttpHandler(String pathLabel, HttpHandler delegate) {
        this.pathLabel = Objects.requireNonNull(pathLabel, "pathLabel");
        this.delegate = Objects.requireNonNull(delegate, "delegate");
    }

    public static HttpHandler of(String pathLabel, HttpHandler delegate) {
        return new InstrumentedHttpHandler(pathLabel, delegate);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        MetricsConfiguration.initialize();
        
        String method = exchange.getRequestMethod();
        MetricsConfiguration.HTTP_INFLIGHT_REQUESTS.labels(pathLabel).inc();
        Histogram.Timer timer = MetricsConfiguration.HTTP_REQUEST_DURATION_SECONDS
                .labels(pathLabel, method)
                .startTimer();
        
        // Wrap input stream to count bytes
        CountingInputStream countingStream = new CountingInputStream(exchange.getRequestBody());
        exchange.setStreams(countingStream, exchange.getResponseBody());
        
        int statusCode = 200;
        boolean handlerFailed = false;
        
        try {
            delegate.handle(exchange);
            statusCode = exchange.getResponseCode();
            if (statusCode == -1) {
                statusCode = 200;
            }
        } catch (IOException | RuntimeException e) {
            handlerFailed = true;
            statusCode = 500;
            throw e;
        } finally {
            timer.observeDuration();
            MetricsConfiguration.HTTP_INFLIGHT_REQUESTS.labels(pathLabel).dec();
            
            double bytes = countingStream.getCount();
            MetricsConfiguration.HTTP_REQUEST_SIZE_BYTES
                    .labels(pathLabel, method)
                    .observe(bytes);
            
            MetricsConfiguration.HTTP_REQUESTS_TOTAL
                    .labels(pathLabel, method, Integer.toString(statusCode))
                    .inc();
        }
    }

    private static final class CountingInputStream extends FilterInputStream {

        private long count;

        private CountingInputStream(InputStream in) {
            super(in);
        }

        private long getCount() {
            return count;
        }

        @Override
        public int read() throws IOException {
            int result = super.read();
            if (result != -1) {
                count++;
            }
            return result;
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            int result = super.read(b, off, len);
            if (result > 0) {
                count += result;
            }
            return result;
        }

        @Override
        public long skip(long n) throws IOException {
            long skipped = super.skip(n);
            if (skipped > 0) {
                count += skipped;
            }
            return skipped;
        }
    }
}
