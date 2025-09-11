package com.sources.app.scheduler;

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sources.app.dao.UserDAO;

/**
 * Schedules a daily task to verify and update users with expired services.
 */
public class ServiceExpirationScheduler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceExpirationScheduler.class);
    private static final long PERIOD_MILLIS = Duration.ofDays(1).toMillis();

    private final UserDAO userDAO;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public ServiceExpirationScheduler(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    /**
     * Starts a daily fixed-rate job. First run is after PERIOD_MILLIS.
     */
    public void startDaily() {
        scheduler.scheduleAtFixedRate(() -> {
            try {
                LOGGER.info("Ejecutando verificación programada de servicios expirados...");
                int count = userDAO.checkAllUsersServiceExpiration();
                LOGGER.info("Verificación programada: se actualizaron {} usuarios con servicios expirados.", count);
            } catch (Exception e) {
                LOGGER.error("Error en verificación programada de servicios expirados", e);
            }
        }, PERIOD_MILLIS, PERIOD_MILLIS, TimeUnit.MILLISECONDS);
    }

    public void shutdown() {
        scheduler.shutdownNow();
    }
}
