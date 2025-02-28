package com.sources.app;

import com.sources.app.dao.*;
import com.sources.app.handlers.*;
import com.sources.app.util.HibernateUtil;
import com.sun.net.httpserver.HttpServer;
import org.hibernate.Session;
import java.net.InetAddress;

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

        // Crear y configurar el servidor HTTP
        HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", 8080), 0);
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
        server.setExecutor(null); // Usa el executor por defecto
        server.start();
        InetAddress ip = InetAddress.getLocalHost();
        System.out.println("Servidor iniciado en http://" + ip.getHostAddress() + ":8080/api");
    }
}
