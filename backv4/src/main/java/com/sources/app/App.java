package com.sources.app;

import java.io.IOException;
import java.net.InetSocketAddress;
 

import org.hibernate.Session;

import com.sources.app.config.DaoRegistry;
import com.sources.app.config.ServerConfig;
import com.sources.app.config.ServerRoutes;
import com.sources.app.metrics.MetricsConfiguration;
import com.sources.app.scheduler.ServiceExpirationScheduler;
import com.sources.app.util.HibernateUtil;
import com.sun.net.httpserver.HttpServer;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.exporter.HTTPServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Clase principal de la aplicación para el backend de Ensurance Pharmacy.
 * Inicializa la conexión a la base de datos, los DAOs, el servidor HTTP y
 * configura los endpoints de la API. También incluye lógica para verificaciones
 * periódicas como la expiración de servicios.
 */
public class App {

    private static final Logger logger = LoggerFactory.getLogger(App.class);
    private static final DaoRegistry daoRegistry = new DaoRegistry();

    /**
     * Constructor privado para prevenir la instanciación de la clase de
     * utilidad.
     */
    private App() {
        // Prevent instantiation
    }

    // Método eliminado: no afecta funcionalidad y se retira por limpieza

    /**
     * El punto de entrada principal de la aplicación. Inicializa el servidor,
     * configura los manejadores de contexto para varios endpoints de la API,
     * realiza comprobaciones iniciales de la base de datos e inicia el servidor
     * HTTP. También programa una tarea diaria para verificar los servicios de
     * usuario expirados.
     *
     * @param args Argumentos de línea de comandos (no utilizados).
     * @throws Exception Si hay un error al iniciar el servidor o conectarse a
     * la base de datos.
     */
    public static void main(String[] args) throws Exception {
        // Prueba de conexión a la base de datos
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            if (session.isConnected()) {
                logger.info("Conexión exitosa a la base de datos!");
            } else {
                logger.error("No se pudo establecer conexión a la base de datos.");
            }
        } catch (Exception e) {
            logger.error("Error al conectar con la base de datos", e);
        }

        // Inicializa métricas y servidor de exposición Prometheus
        MetricsConfiguration.initialize();
        String metricsHost = System.getenv("METRICS_HOST");
        if (metricsHost == null || metricsHost.isEmpty()) {
            metricsHost = "0.0.0.0";
        }
        int metricsPort = 9465;
        String metricsPortEnv = System.getenv("METRICS_PORT");
        if (metricsPortEnv != null && !metricsPortEnv.isEmpty()) {
            try {
                metricsPort = Integer.parseInt(metricsPortEnv);
            } catch (NumberFormatException e) {
                logger.warn("Formato de puerto de métricas inválido. Se usará 9465.");
            }
        }
        try {
            new HTTPServer(new InetSocketAddress(metricsHost, metricsPort), CollectorRegistry.defaultRegistry, true);
            logger.info("Métricas Ensurance disponibles en http://{}:{}/metrics", metricsHost, metricsPort);
        } catch (IOException e) {
            logger.error("No se pudo iniciar el servidor HTTP de métricas", e);
        }

        // Host/puerto desde variables de entorno con valores por defecto (extraído a ServerConfig)
        String host = ServerConfig.host();
        int port = ServerConfig.port();

        // Verificar servicios expirados al iniciar
        logger.info("Verificando servicios expirados...");
        int updatedUsers = daoRegistry.getUserDAO().checkAllUsersServiceExpiration();
        logger.info("Se actualizaron {} usuarios con servicios expirados.", updatedUsers);

        // Programar tarea diaria para verificar servicios expirados
        new ServiceExpirationScheduler(daoRegistry.getUserDAO()).startDaily();

        // Crear y configurar el servidor HTTP
        HttpServer server = HttpServer.create(new InetSocketAddress(host, port), 0);
        // Registrar rutas en un único lugar para reducir acoplamiento
        ServerRoutes.register(server, daoRegistry);

        server.setExecutor(null); // Usa el executor por defecto
        server.start();
        logger.info("Servidor iniciado en http://{}:{}/api", host, port);
    }
}
