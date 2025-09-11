package com.sources.app.config;

import com.sources.app.handlers.*;
import com.sun.net.httpserver.HttpServer;

/**
 * Centralizes all HTTP route registrations. This reduces coupling in
 * the application bootstrap and makes routes easier to maintain.
 */
public final class ServerRoutes {
    private ServerRoutes() {}

    public static void register(HttpServer server, DaoRegistry dao) {
        // Auth & users
        server.createContext("/api/login", new LoginHandler(dao.getUserDAO()));
        server.createContext("/api/users", new UserHandler(dao.getUserDAO()));
        server.createContext("/api/users/by-email", new UserByEmailHandler(dao.getUserDAO()));

        // Core entities
        server.createContext("/api/policy", new PolicyHandler(dao.getPolicyDAO()));
        server.createContext("/api/appointment", new AppointmentHandler(dao.getAppointmentDAO()));
        server.createContext("/api/appointmentmade", new AppointmentMadeHandler(dao.getAppointmentMadeDAO()));
        server.createContext("/api/category", new CategoryHandler(dao.getCategoryDAO()));
        server.createContext("/api/configurableamount", new ConfigurableAmountHandler(dao.getConfigurableAmountDAO()));
        server.createContext("/api/hospital", new HospitalHandler(dao.getHospitalDAO()));
        server.createContext("/api/medicine", new MedicineHandler(dao.getMedicineDAO()));
        server.createContext("/api/medicinepres", new MedicinePresHandler(dao.getMedicinePresDAO()));
        server.createContext("/api/pharmacy", new PharmacyHandler(dao.getPharmacyDAO()));
        server.createContext("/api/prescription", new PrescriptionHandler(dao.getPrescriptionDAO()));
        server.createContext("/api/service", new ServiceHandler(dao.getServiceDAO()));
        server.createContext("/api/totalhospital", new TotalHospitalHandler(dao.getTotalHospitalDAO()));
        server.createContext("/api/totalpharmacy", new TotalPharmacyHandler(dao.getTotalPharmacyDAO()));
        server.createContext("/api/transactions", new TransactionsHandler(dao.getTransactionsDAO()));
        server.createContext("/api/transactionpolicy", new TransactionPolicyHandler(dao.getTransactionPolicyDAO()));
        server.createContext("/api/servicecategory", new ServiceCategoryHandler(dao.getServiceCategoryDAO()));

        // Notifications
        server.createContext("/api/notifications/email", new NotificationHandler());

        // Insurance services
        server.createContext("/api/insurance-services", new InsuranceServiceHandler(dao.getInsuranceServiceDAO(), dao.getCategoryDAO()));
        server.createContext("/api/hospital-services", new HospitalInsuranceServiceHandler(dao.getHospitalInsuranceServiceDAO(), dao.getHospitalDAO(), dao.getInsuranceServiceDAO()));

        // Integrations / proxies
        server.createContext("/api/hospital-integration", new HospitalRedirectHandler());
        server.createContext("/api/hospital-proxy", new HospitalServiceProxyHandler(dao.getHospitalDAO()));

        // Ensurance appointments and approvals
        server.createContext("/api/ensurance-appointments", new EnsuranceAppointmentHandler(dao.getEnsuranceAppointmentDAO()));
        server.createContext("/api/prescriptions/", new PrescriptionApprovalHandler(dao.getPrescriptionApprovalDAO(), dao.getUserDAO(), dao.getConfigurableAmountDAO()));
        server.createContext("/api/configurable-amount/", new ConfigurableAmountHandler(dao.getConfigurableAmountDAO()));
    }
}
