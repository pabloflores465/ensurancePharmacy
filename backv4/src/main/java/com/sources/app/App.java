package com.sources.app;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import org.hibernate.Session;

import com.sources.app.dao.AppointmentDAO;
import com.sources.app.dao.AppointmentMadeDAO;
import com.sources.app.dao.CategoryDAO;
import com.sources.app.dao.ConfigurableAmountDAO;
import com.sources.app.dao.EnsuranceAppointmentDAO;
import com.sources.app.dao.HospitalDAO;
import com.sources.app.dao.HospitalInsuranceServiceDAO;
import com.sources.app.dao.InsuranceServiceDAO;
import com.sources.app.dao.MedicineDAO;
import com.sources.app.dao.MedicinePresDAO;
import com.sources.app.dao.PharmacyDAO;
import com.sources.app.dao.PolicyDAO;
import com.sources.app.dao.PrescriptionApprovalDAO;
import com.sources.app.dao.PrescriptionDAO;
import com.sources.app.dao.ServiceCategoryDAO;
import com.sources.app.dao.ServiceDAO;
import com.sources.app.dao.TotalHospitalDAO;
import com.sources.app.dao.TotalPharmacyDAO;
import com.sources.app.dao.TransactionPolicyDAO;
import com.sources.app.dao.TransactionsDAO;
import com.sources.app.dao.UserDAO;
import com.sources.app.handlers.AppointmentHandler;
import com.sources.app.handlers.AppointmentMadeHandler;
import com.sources.app.handlers.CategoryHandler;
import com.sources.app.handlers.ConfigurableAmountHandler;
import com.sources.app.handlers.EnsuranceAppointmentHandler;
import com.sources.app.handlers.HospitalHandler;
import com.sources.app.handlers.HospitalInsuranceServiceHandler;
import com.sources.app.handlers.HospitalRedirectHandler;
import com.sources.app.handlers.HospitalServiceProxyHandler;
import com.sources.app.handlers.InsuranceServiceHandler;
import com.sources.app.handlers.LoginHandler;
import com.sources.app.handlers.MedicineHandler;
import com.sources.app.handlers.MedicinePresHandler;
import com.sources.app.handlers.NotificationHandler;
import com.sources.app.handlers.PharmacyHandler;
import com.sources.app.handlers.PolicyHandler;
import com.sources.app.handlers.PrescriptionApprovalHandler;
import com.sources.app.handlers.PrescriptionHandler;
import com.sources.app.handlers.ServiceCategoryHandler;
import com.sources.app.handlers.ServiceHandler;
import com.sources.app.handlers.TotalHospitalHandler;
import com.sources.app.handlers.TotalPharmacyHandler;
import com.sources.app.handlers.TransactionPolicyHandler;
import com.sources.app.handlers.TransactionsHandler;
import com.sources.app.handlers.UserByEmailHandler;
import com.sources.app.handlers.UserHandler;
import com.sources.app.util.HibernateUtil;
import com.sun.net.httpserver.HttpServer;

//para probar cada actualizacion
/**
 * Clase principal de la aplicación para el backend de Ensurance Pharmacy.
 * Inicializa la conexión a la base de datos, los DAOs, el servidor HTTP y
 * configura los endpoints de la API. También incluye lógica para verificaciones
 * periódicas como la expiración de servicios.
 */
public class App {

    private static final UserDAO userDAO = new UserDAO();
    private static final PolicyDAO policyDAO = new PolicyDAO();
    private static final AppointmentDAO appointmentDAO = new AppointmentDAO();
    private static final AppointmentMadeDAO appointmentMadeDAO = new AppointmentMadeDAO();
    private static final CategoryDAO categoryDAO = new CategoryDAO();
    private static final ConfigurableAmountDAO configurableAmountDAO = new ConfigurableAmountDAO();
    private static final HospitalDAO hospitalDAO = new HospitalDAO();
    private static final MedicineDAO medicineDAO = new MedicineDAO();
    private static final MedicinePresDAO medicinePresDAO = new MedicinePresDAO();
    private static final PharmacyDAO pharmacyDAO = new PharmacyDAO();
    private static final PrescriptionDAO prescriptionDAO = new PrescriptionDAO();
    private static final ServiceDAO serviceDAO = new ServiceDAO();
    private static final TotalHospitalDAO totalHospitalDAO = new TotalHospitalDAO();
    private static final TotalPharmacyDAO totalPharmacyDAO = new TotalPharmacyDAO();
    private static final TransactionsDAO transactionsDAO = new TransactionsDAO();
    private static final TransactionPolicyDAO transactionPolicyDAO = new TransactionPolicyDAO();
    private static final ServiceCategoryDAO serviceCategoryDAO = new ServiceCategoryDAO();
    private static final InsuranceServiceDAO insuranceServiceDAO = new InsuranceServiceDAO();
    private static final HospitalInsuranceServiceDAO hospitalInsuranceServiceDAO = new HospitalInsuranceServiceDAO();
    private static final EnsuranceAppointmentDAO ensuranceAppointmentDAO = new EnsuranceAppointmentDAO();
    private static final PrescriptionApprovalDAO prescriptionApprovalDAO = new PrescriptionApprovalDAO();

    /**
     * Constructor privado para prevenir la instanciación de la clase de
     * utilidad.
     */
    private App() {
        // Prevent instantiation
    }

    private static String getLocalExternalIp() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();
                // Ignora interfaces inactivas o de loopback
                if (!iface.isUp() || iface.isLoopback()) {
                    continue;
                }
                Enumeration<InetAddress> addresses = iface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    // Solo direcciones IPv4 que no sean loopback o de enlace local
                    if (addr instanceof Inet4Address && !addr.isLoopbackAddress() && !addr.isLinkLocalAddress()) {
                        return addr.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return "127.0.0.1";
    }

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
                System.out.println("Conexión exitosa a la base de datos!");
            } else {
                System.err.println("No se pudo establecer conexión a la base de datos.");
            }
        } catch (Exception e) {
            System.err.println("Error al conectar con la base de datos:");
            e.printStackTrace();
        }

        // Host/puerto desde variables de entorno con valores por defecto
        String host = System.getenv("SERVER_HOST");
        if (host == null || host.isEmpty()) {
            host = "0.0.0.0";
        }

        String portEnv = System.getenv("SERVER_PORT");
        if (portEnv == null || portEnv.isEmpty()) {
            portEnv = System.getenv("PORT");
        }

        int port = 8080; // Puerto predeterminado
        try {
            if (portEnv != null && !portEnv.isEmpty()) {
                port = Integer.parseInt(portEnv);
            }
        } catch (NumberFormatException e) {
            System.out.println("Formato de puerto inválido. Se usará el puerto predeterminado 8080.");
        }

        // Verificar servicios expirados al iniciar
        System.out.println("Verificando servicios expirados...");
        int updatedUsers = userDAO.checkAllUsersServiceExpiration();
        System.out.println("Se actualizaron " + updatedUsers + " usuarios con servicios expirados.");

        // Programar tarea para verificar servicios expirados cada día
        java.util.Timer timer = new java.util.Timer(true);
        timer.scheduleAtFixedRate(new java.util.TimerTask() {
            @Override
            public void run() {
                System.out.println("Ejecutando verificación programada de servicios expirados...");
                int count = userDAO.checkAllUsersServiceExpiration();
                System.out.println("Verificación programada: se actualizaron " + count + " usuarios con servicios expirados.");
            }
        },
                // Ejecutar cada 24 horas (en milisegundos)
                86400000, 86400000);

        // Crear y configurar el servidor HTTP
        HttpServer server = HttpServer.create(new InetSocketAddress(host, port), 0);
        server.createContext("/api/login", new LoginHandler(userDAO));
        server.createContext("/api/users", new UserHandler(userDAO));
        server.createContext("/api/policy", new PolicyHandler(policyDAO));
        server.createContext("/api/appointment", new AppointmentHandler(appointmentDAO));
        server.createContext("/api/appointmentmade", new AppointmentMadeHandler(appointmentMadeDAO));
        server.createContext("/api/category", new CategoryHandler(categoryDAO));
        server.createContext("/api/configurableamount", new ConfigurableAmountHandler(configurableAmountDAO));
        server.createContext("/api/hospital", new HospitalHandler(hospitalDAO));
        server.createContext("/api/medicine", new MedicineHandler(medicineDAO));
        server.createContext("/api/medicinepres", new MedicinePresHandler(medicinePresDAO));
        server.createContext("/api/pharmacy", new PharmacyHandler(pharmacyDAO));
        server.createContext("/api/prescription", new PrescriptionHandler(prescriptionDAO));
        server.createContext("/api/service", new ServiceHandler(serviceDAO));
        server.createContext("/api/totalhospital", new TotalHospitalHandler(totalHospitalDAO));
        server.createContext("/api/totalpharmacy", new TotalPharmacyHandler(totalPharmacyDAO));
        server.createContext("/api/transactions", new TransactionsHandler(transactionsDAO));
        server.createContext("/api/transactionpolicy", new TransactionPolicyHandler(transactionPolicyDAO));
        server.createContext("/api/servicecategory", new ServiceCategoryHandler(serviceCategoryDAO));
        server.createContext("/api/notifications/email", new NotificationHandler());
        // Nuevos endpoints para servicios de seguro y relaciones con hospitales
        server.createContext("/api/insurance-services", new InsuranceServiceHandler(insuranceServiceDAO, categoryDAO));
        server.createContext("/api/hospital-services", new HospitalInsuranceServiceHandler(hospitalInsuranceServiceDAO, hospitalDAO, insuranceServiceDAO));
        // Nuevo handler para la integración con el hospital
        server.createContext("/api/hospital-integration", new HospitalRedirectHandler());
        // Registrar el nuevo handler para buscar usuarios por email
        server.createContext("/api/users/by-email", new UserByEmailHandler(userDAO));
        // Nuevo handler para las citas de seguro
        server.createContext("/api/ensurance-appointments", new EnsuranceAppointmentHandler(ensuranceAppointmentDAO));
        // Handler para aprobaciones de recetas
        server.createContext("/api/prescriptions/", new PrescriptionApprovalHandler(prescriptionApprovalDAO, userDAO, configurableAmountDAO));
        // Actualizar handler para monto configurable
        server.createContext("/api/configurable-amount/", new ConfigurableAmountHandler(configurableAmountDAO));
        // Registrar el nuevo handler para proxy de servicios de hospital
        server.createContext("/api/hospital-proxy", new HospitalServiceProxyHandler(hospitalDAO));

        server.setExecutor(null); // Usa el executor por defecto
        server.start();
        System.out.println("Servidor iniciado en http://" + host + ":" + port + "/api");
    }
}
