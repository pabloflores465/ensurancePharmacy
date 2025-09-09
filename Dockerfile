# ============================================================================
# Unified Dockerfile for Ensurance Pharmacy System
# ============================================================================
# This Dockerfile builds and runs both Ensurance and Pharmacy systems
# with their respective databases, backends, and frontends on different ports
# ============================================================================

# Stage 1: Build Ensurance Frontend
FROM node:20-alpine AS ensurance-frontend-build
WORKDIR /app/ensurance
ENV NPM_CONFIG_FETCH_RETRIES=5 \
    NPM_CONFIG_FETCH_RETRY_FACTOR=2 \
    NPM_CONFIG_FETCH_RETRY_MINTIMEOUT=10000 \
    NPM_CONFIG_FETCH_RETRY_MAXTIMEOUT=120000 \
    NPM_CONFIG_NETWORK_TIMEOUT=600000 \
    NPM_CONFIG_AUDIT=false \
    NPM_CONFIG_FUND=false \
    NPM_CONFIG_PROGRESS=false
COPY ensurance/package*.json ./
# Install full deps (dev needed for build tools like vite) with retries
RUN set -eux; \
    npm config set registry https://registry.npmjs.org/; \
    for i in 1 2 3; do npm ci && break || (npm cache clean --force; sleep $((i*5))); done
COPY ensurance/ ./
# Create .env file with localhost configuration
RUN echo "VITE_IP=localhost" > .env && \
    echo "VITE_ENSURANCE_API_URL=http://localhost:8081/api" >> .env && \
    echo "VITE_PHARMACY_API_URL=http://localhost:8082/api2" >> .env
RUN npm run build

# Stage 2: Build Pharmacy Frontend
FROM node:20-alpine AS pharmacy-frontend-build
WORKDIR /app/pharmacy
ENV NPM_CONFIG_FETCH_RETRIES=5 \
    NPM_CONFIG_FETCH_RETRY_FACTOR=2 \
    NPM_CONFIG_FETCH_RETRY_MINTIMEOUT=10000 \
    NPM_CONFIG_FETCH_RETRY_MAXTIMEOUT=120000 \
    NPM_CONFIG_NETWORK_TIMEOUT=600000 \
    NPM_CONFIG_AUDIT=false \
    NPM_CONFIG_FUND=false \
    NPM_CONFIG_PROGRESS=false
COPY pharmacy/package*.json ./
# Install full deps (dev needed for build tools like vue-cli-service) with retries
RUN set -eux; \
    npm config set registry https://registry.npmjs.org/; \
    for i in 1 2 3; do npm ci && break || (npm cache clean --force; sleep $((i*5))); done
COPY pharmacy/ ./
# Create .env file with localhost configuration
RUN echo "VUE_APP_API_HOST=localhost" > .env && \
    echo "VUE_APP_IP=localhost" >> .env && \
    echo "VUE_APP_PHARMACY_API_URL=http://localhost:8082/api2" >> .env && \
    echo "VUE_APP_ENSURANCE_API_URL=http://localhost:8081/api" >> .env
RUN npm run build

# Stage 3: Build Ensurance Backend (BackV4)
FROM eclipse-temurin:24-jdk-alpine AS ensurance-backend-build
WORKDIR /app/backv4
RUN apk add --no-cache maven
# Harden Maven network settings (TLS + retries)
ENV MAVEN_OPTS="-Dhttps.protocols=TLSv1.2,TLSv1.3 -Dmaven.wagon.http.retryHandler.count=5 -Dmaven.wagon.http.pool=false"
COPY backv4/pom.xml ./
# Retry go-offline to mitigate transient TLS/handshake issues
RUN set -eux; for i in 1 2 3; do mvn -B dependency:go-offline && break || sleep 5; done
COPY backv4/ ./
# Retry package as well (downloads may still be needed)
RUN set -eux; for i in 1 2 3; do mvn -B -DskipTests clean package && break || sleep 5; done

# Stage 4: Build Pharmacy Backend (BackV5)
FROM eclipse-temurin:24-jdk-alpine AS pharmacy-backend-build
WORKDIR /app/backv5
RUN apk add --no-cache maven
ENV MAVEN_OPTS="-Dhttps.protocols=TLSv1.2,TLSv1.3 -Dmaven.wagon.http.retryHandler.count=5 -Dmaven.wagon.http.pool=false"
COPY backv5/pom.xml ./
RUN set -eux; for i in 1 2 3; do mvn -B dependency:go-offline && break || sleep 5; done
COPY backv5/ ./
RUN set -eux; for i in 1 2 3; do mvn -B -DskipTests clean package && break || sleep 5; done

# Stage 5: Runtime Environment
FROM eclipse-temurin:24-jre-alpine AS runtime

# Build arguments for environment configuration
ARG ENVIRONMENT=main

# Install required packages
RUN apk add --no-cache \
    sqlite \
    supervisor \
    curl

# Create application directories
WORKDIR /app
RUN mkdir -p /app/ensurance-frontend \
             /app/pharmacy-frontend \
             /app/ensurance-backend \
             /app/pharmacy-backend \
             /app/databases \
             /app/logs \
             /var/log/supervisor \
             /run

# Copy built frontends
COPY --from=ensurance-frontend-build /app/ensurance/dist /app/ensurance-frontend/
COPY --from=pharmacy-frontend-build /app/pharmacy/dist /app/pharmacy-frontend/

# Copy built backends
COPY --from=ensurance-backend-build /app/backv4/target/*.jar /app/ensurance-backend/
COPY --from=pharmacy-backend-build /app/backv5/target/*.jar /app/pharmacy-backend/

# Normalize jar filenames to app.jar for supervisor commands
RUN set -eux; \
    ensJar=$(find /app/ensurance-backend -maxdepth 1 -type f -name "*.jar" | head -n1); \
    [ -n "$ensJar" ] && mv "$ensJar" /app/ensurance-backend/app.jar; \
    phaJar=$(find /app/pharmacy-backend -maxdepth 1 -type f -name "*.jar" | head -n1); \
    [ -n "$phaJar" ] && mv "$phaJar" /app/pharmacy-backend/app.jar

# Copy SQL seed files to a non-mounted path so we can initialize volumes at runtime
RUN mkdir -p /app/seed-sql/ensurance /app/seed-sql/pharmacy
COPY --from=ensurance-backend-build /app/backv4/sqlite/ /app/seed-sql/ensurance/
COPY --from=pharmacy-backend-build /app/backv5/sqlite/ /app/seed-sql/pharmacy/

# Copy backend dependencies and configurations
COPY --from=ensurance-backend-build /app/backv4/src/main/resources/ /app/ensurance-backend/resources/
COPY --from=pharmacy-backend-build /app/backv5/src/main/resources/ /app/pharmacy-backend/resources/

# Database initialization moved to start.sh to support named volumes

# Configure Supervisor to manage all services (no nginx, serve static via busybox httpd)
COPY <<EOF /etc/supervisor/conf.d/supervisord.conf
[supervisord]
nodaemon=true
logfile=/app/logs/supervisord.log
pidfile=/var/run/supervisord.pid

[program:ensurance-frontend]
command=/bin/busybox httpd -f -p 5175 -h /app/ensurance-frontend
autostart=true
autorestart=true
stderr_logfile=/app/logs/ensurance-frontend.err.log
stdout_logfile=/app/logs/ensurance-frontend.out.log

[program:pharmacy-frontend]
command=/bin/busybox httpd -f -p 8089 -h /app/pharmacy-frontend
autostart=true
autorestart=true
stderr_logfile=/app/logs/pharmacy-frontend.err.log
stdout_logfile=/app/logs/pharmacy-frontend.out.log

[program:ensurance-backend]
command=/bin/sh -lc "exec java -jar -Dserver.port=8081 -Dhibernate.connection.url=jdbc:sqlite:/app/databases/ensurance/USUARIO.sqlite -Dhibernate.connection.driver_class=org.sqlite.JDBC -Dhibernate.dialect=org.hibernate.community.dialect.SQLiteDialect /app/ensurance-backend/app.jar"
directory=/app/ensurance-backend
autostart=true
autorestart=true
stderr_logfile=/app/logs/ensurance-backend.err.log
stdout_logfile=/app/logs/ensurance-backend.out.log
environment=SERVER_HOST="0.0.0.0",SERVER_PORT="8081"

[program:pharmacy-backend]
command=/bin/sh -lc "exec java -jar -Dserver.port=8082 -Dhibernate.connection.url=jdbc:sqlite:/app/databases/pharmacy/USUARIO.sqlite -Dhibernate.connection.driver_class=org.sqlite.JDBC -Dhibernate.dialect=org.hibernate.community.dialect.SQLiteDialect /app/pharmacy-backend/app.jar"
directory=/app/pharmacy-backend
autostart=true
autorestart=true
stderr_logfile=/app/logs/pharmacy-backend.err.log
stdout_logfile=/app/logs/pharmacy-backend.out.log
environment=SERVER_HOST="0.0.0.0",SERVER_PORT="8082"
EOF

# Create startup script
COPY <<'EOF' /app/start.sh
#!/bin/sh
set -e

echo "Starting Ensurance Pharmacy System..."
echo "Ensurance Frontend: http://localhost:5175"
echo "Pharmacy Frontend: http://localhost:8089"
echo "Ensurance Backend: http://localhost:8081"
echo "Pharmacy Backend: http://localhost:8082"

# Ensure database directories exist
mkdir -p /app/databases/ensurance /app/databases/pharmacy || true

# Initialize Ensurance DB if missing
if [ ! -f /app/databases/ensurance/USUARIO.sqlite ]; then
  echo "Initializing Ensurance SQLite database..."
  if [ -f /app/seed-sql/ensurance/01_initial_schema.sql ]; then
    sqlite3 /app/databases/ensurance/USUARIO.sqlite < /app/seed-sql/ensurance/01_initial_schema.sql || true
  fi
  if [ -f /app/seed-sql/ensurance/02_add_missing_tables.sql ]; then
    sqlite3 /app/databases/ensurance/USUARIO.sqlite < /app/seed-sql/ensurance/02_add_missing_tables.sql || true
  fi
fi

# Initialize Pharmacy DB if missing
if [ ! -f /app/databases/pharmacy/USUARIO.sqlite ]; then
  echo "Initializing Pharmacy SQLite database..."
  if [ -f /app/seed-sql/pharmacy/01_initial_schema.sql ]; then
    sqlite3 /app/databases/pharmacy/USUARIO.sqlite < /app/seed-sql/pharmacy/01_initial_schema.sql || true
  fi
  if [ -f /app/seed-sql/pharmacy/02_add_missing_columns.sql ]; then
    sqlite3 /app/databases/pharmacy/USUARIO.sqlite < /app/seed-sql/pharmacy/02_add_missing_columns.sql || true
  fi
fi

# Start supervisor
exec /usr/bin/supervisord -c /etc/supervisor/conf.d/supervisord.conf
EOF

RUN chmod +x /app/start.sh

# Expose all ports
EXPOSE 5175 8089 8081 8082

# Health check: basic reachability of frontends and backend base path
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -fsS http://localhost:5175 >/dev/null && \
        curl -fsS http://localhost:8089 >/dev/null && \
        curl -fsS http://localhost:8081/ >/dev/null || exit 1

# Set environment variables based on build arg
ENV ENVIRONMENT=${ENVIRONMENT}
ENV NODE_ENV=production
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# Override NODE_ENV for dev environment
RUN if [ "$ENVIRONMENT" = "dev" ]; then \
        echo "export NODE_ENV=development" >> /etc/environment; \
    elif [ "$ENVIRONMENT" = "qa" ]; then \
        echo "export NODE_ENV=test" >> /etc/environment; \
    fi

# Start the application
CMD ["/app/start.sh"]

# ============================================================================
# Port Configuration:
# - Ensurance Frontend: 5175
# - Pharmacy Frontend: 8089  
# - Ensurance Backend (BackV4): 8081
# - Pharmacy Backend (BackV5): 8082
# ============================================================================
