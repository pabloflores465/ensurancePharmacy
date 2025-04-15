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
    // Instanciamos los DAO correspondientes
    private static final UserDAO userDAO = new UserDAO();
    private static final BillDAO billDAO = new BillDAO();
    private static final BillMedicineDAO billMedicineDAO = new BillMedicineDAO();
    private static final CategoryDAO categoryDAO = new CategoryDAO();
    private static final CommentsDAO commentsDAO = new CommentsDAO();
    private static final HospitalDAO hospitalDAO = new HospitalDAO();
    private static final MedicineDAO medicineDAO = new MedicineDAO();
    private static final MedicineCatSubcatDAO medicineCatSubcatDAO = new MedicineCatSubcatDAO();
    private static final OrdersDAO ordersDAO = new OrdersDAO();
    private static final OrderMedicineDAO orderMedicineDAO = new OrderMedicineDAO();
    private static final PolicyDAO policyDAO = new PolicyDAO();
    private static final PrescriptionDAO prescriptionDAO = new PrescriptionDAO();
    private static final PrescriptionMedicineDAO prescriptionMedicineDAO = new PrescriptionMedicineDAO();
    private static final SubcategoryDAO subcategoryDAO = new SubcategoryDAO();
    private static final ExternalMedicineDAO externalMedicineDAO = new ExternalMedicineDAO();
    
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

        HttpServer server = HttpServer.create(new InetSocketAddress(8081), 0);

        // Asignamos cada contexto con su respectivo Handler usando el prefijo "/api2"
        server.createContext("/api2/login", new LoginHandler(userDAO));
        server.createContext("/api2/users", new UserHandler(userDAO));
        server.createContext("/api2/bills", new BillHandler(billDAO));
        server.createContext("/api2/bill_medicines", new BillMedicineHandler(billMedicineDAO));
        server.createContext("/api2/categories", new CategoryHandler(categoryDAO));
        server.createContext("/api2/comments", new CommentsHandler(commentsDAO));
        server.createContext("/api2/hospitals", new HospitalHandler(hospitalDAO));
        server.createContext("/api2/medicines", new MedicineHandler(medicineDAO));
        server.createContext("/api2/medicine_catsubcats", new MedicineCatSubcatHandler(medicineCatSubcatDAO));
        server.createContext("/api2/orders", new OrdersHandler(ordersDAO));
        server.createContext("/api2/order_medicines", new OrderMedicineHandler(orderMedicineDAO));
        server.createContext("/api2/policies", new PolicyHandler(policyDAO));
        server.createContext("/api2/prescriptions", new PrescriptionHandler(prescriptionDAO));
        server.createContext("/api2/prescription_medicines", new PrescriptionMedicineHandler(prescriptionMedicineDAO));
        server.createContext("/api2/subcategories", new SubcategoryHandler(subcategoryDAO));
        server.createContext("/api2/external_medicines", new ExternalMedicineHandler(externalMedicineDAO));
        server.createContext("/api2/verification", new VerificationHandler());
        server.setExecutor(null); // Usa el executor por defecto
        server.start();
        System.out.println("Servidor iniciado en http://" + ip + ":8081/api2");
    }
}
