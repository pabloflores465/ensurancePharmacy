package com.sources.app;

import com.sources.app.dao.*;
import com.sources.app.handlers.*;
import com.sources.app.util.HibernateUtil;
import com.sun.net.httpserver.HttpServer;
import org.hibernate.Session;
import java.net.InetAddress;
import java.net.*;
import java.util.Enumeration;

import java.net.InetSocketAddress;

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

    private static String getLocalExternalIp() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();
                // Ignora interfaces inactivas o de loopback
                if (!iface.isUp() || iface.isLoopback()) continue;
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

        String ip = getLocalExternalIp();

        // Crear y configurar el servidor HTTP
        HttpServer server = HttpServer.create(new InetSocketAddress(ip, 8080), 0);
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
        server.setExecutor(null); // Usa el executor por defecto
        server.start();
        System.out.println("Servidor iniciado en http://" + ip + ":8080/api");
    }
}
