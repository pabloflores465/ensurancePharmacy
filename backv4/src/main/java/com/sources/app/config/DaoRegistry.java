package com.sources.app.config;

import com.sources.app.dao.*;

/**
 * Centralized registry for DAO instances used by the HTTP handlers.
 * This reduces the number of direct dependencies from the application
 * bootstrap class and improves maintainability.
 */
public class DaoRegistry {
    private final UserDAO userDAO = new UserDAO();
    private final PolicyDAO policyDAO = new PolicyDAO();
    private final AppointmentDAO appointmentDAO = new AppointmentDAO();
    private final AppointmentMadeDAO appointmentMadeDAO = new AppointmentMadeDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();
    private final ConfigurableAmountDAO configurableAmountDAO = new ConfigurableAmountDAO();
    private final HospitalDAO hospitalDAO = new HospitalDAO();
    private final MedicineDAO medicineDAO = new MedicineDAO();
    private final MedicinePresDAO medicinePresDAO = new MedicinePresDAO();
    private final PharmacyDAO pharmacyDAO = new PharmacyDAO();
    private final PrescriptionDAO prescriptionDAO = new PrescriptionDAO();
    private final ServiceDAO serviceDAO = new ServiceDAO();
    private final TotalHospitalDAO totalHospitalDAO = new TotalHospitalDAO();
    private final TotalPharmacyDAO totalPharmacyDAO = new TotalPharmacyDAO();
    private final TransactionsDAO transactionsDAO = new TransactionsDAO();
    private final TransactionPolicyDAO transactionPolicyDAO = new TransactionPolicyDAO();
    private final ServiceCategoryDAO serviceCategoryDAO = new ServiceCategoryDAO();
    private final InsuranceServiceDAO insuranceServiceDAO = new InsuranceServiceDAO();
    private final HospitalInsuranceServiceDAO hospitalInsuranceServiceDAO = new HospitalInsuranceServiceDAO();
    private final EnsuranceAppointmentDAO ensuranceAppointmentDAO = new EnsuranceAppointmentDAO();
    private final PrescriptionApprovalDAO prescriptionApprovalDAO = new PrescriptionApprovalDAO();

    public UserDAO getUserDAO() { return userDAO; }
    public PolicyDAO getPolicyDAO() { return policyDAO; }
    public AppointmentDAO getAppointmentDAO() { return appointmentDAO; }
    public AppointmentMadeDAO getAppointmentMadeDAO() { return appointmentMadeDAO; }
    public CategoryDAO getCategoryDAO() { return categoryDAO; }
    public ConfigurableAmountDAO getConfigurableAmountDAO() { return configurableAmountDAO; }
    public HospitalDAO getHospitalDAO() { return hospitalDAO; }
    public MedicineDAO getMedicineDAO() { return medicineDAO; }
    public MedicinePresDAO getMedicinePresDAO() { return medicinePresDAO; }
    public PharmacyDAO getPharmacyDAO() { return pharmacyDAO; }
    public PrescriptionDAO getPrescriptionDAO() { return prescriptionDAO; }
    public ServiceDAO getServiceDAO() { return serviceDAO; }
    public TotalHospitalDAO getTotalHospitalDAO() { return totalHospitalDAO; }
    public TotalPharmacyDAO getTotalPharmacyDAO() { return totalPharmacyDAO; }
    public TransactionsDAO getTransactionsDAO() { return transactionsDAO; }
    public TransactionPolicyDAO getTransactionPolicyDAO() { return transactionPolicyDAO; }
    public ServiceCategoryDAO getServiceCategoryDAO() { return serviceCategoryDAO; }
    public InsuranceServiceDAO getInsuranceServiceDAO() { return insuranceServiceDAO; }
    public HospitalInsuranceServiceDAO getHospitalInsuranceServiceDAO() { return hospitalInsuranceServiceDAO; }
    public EnsuranceAppointmentDAO getEnsuranceAppointmentDAO() { return ensuranceAppointmentDAO; }
    public PrescriptionApprovalDAO getPrescriptionApprovalDAO() { return prescriptionApprovalDAO; }
}
