package com.sources.app.config;

/**
 * Reads server configuration from environment variables with sane defaults.
 */
public final class ServerConfig {
    private ServerConfig() {}

    public static String host() {
        String host = System.getenv("SERVER_HOST");
        return (host == null || host.isEmpty()) ? "0.0.0.0" : host;
    }

    public static int port() {
        String portEnv = System.getenv("SERVER_PORT");
        if (portEnv == null || portEnv.isEmpty()) {
            portEnv = System.getenv("PORT");
        }
        int port = 8080;
        try {
            if (portEnv != null && !portEnv.isEmpty()) {
                port = Integer.parseInt(portEnv);
            }
        } catch (NumberFormatException ignored) {
            // fall back to default 8080
        }
        return port;
    }
}
