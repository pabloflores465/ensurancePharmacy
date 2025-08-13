package com.sources.app.dao;

import static org.junit.jupiter.api.Assertions.*;

import java.io.InputStream;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sources.app.entities.Bill;
import com.sources.app.entities.Prescription;
import com.sources.app.entities.Hospital;
import com.sources.app.entities.User;

public class BillDAOTest {

    private BillDAO billDAO = new BillDAO();
    private PrescriptionDAO prescriptionDAO = new PrescriptionDAO();
    private HospitalDAO hospitalDAO = new HospitalDAO();
    private UserDAO userDAO = new UserDAO();
    private ObjectMapper mapper = new ObjectMapper();

    /**
     * Prueba que deserializa un objeto Bill desde el archivo JSON externo.
     */
    @Test
    public void testBillFromJsonFile() throws Exception {
        // Cargar el archivo bill.json desde el mismo paquete (se espera que esté en el classpath)
        InputStream is = getClass().getResourceAsStream("bill.json");
        assertNotNull(is, "No se encontró el archivo bill.json");

        // Deserializar el JSON en un objeto Bill
        Bill billFromJson = mapper.readValue(is, Bill.class);
        assertNotNull(billFromJson, "El objeto Bill deserializado no debe ser nulo");
        assertEquals("125.0", billFromJson.getTotal(), "El campo 'total' debe coincidir con el valor esperado");
    }

    /**
     * Prueba la creación de un Bill utilizando datos del JSON.
     */
    @Test
    public void testCreateBillFromJson() throws Exception {
        // Cargar y deserializar el archivo bill.json
        InputStream is = getClass().getResourceAsStream("/com/sources/app/dao/bill.json");
        assertNotNull(is, "No se encontró el archivo bill.json");
        Bill billFromJson = mapper.readValue(is, Bill.class);

        // Preparar entidades relacionadas persistidas: Hospital, User y Prescription
        Hospital hospital = hospitalDAO.create("Hosp Test", "555-0000", "h@test.com", "Addr", '1');
        assertNotNull(hospital, "El hospital debe persistirse");
        User user = userDAO.create("User Test", "123", "555", "u@test.com", new java.util.Date(), "Addr", "pwd");
        assertNotNull(user, "El usuario debe persistirse");
        Prescription prescription = prescriptionDAO.create(hospital, user, 'Y');
        assertNotNull(prescription, "La prescripción debe persistirse");

        // Usar los datos del JSON para crear un Bill a través del DAO
        Bill createdBill = billDAO.create(prescription, billFromJson.getTaxes(), billFromJson.getSubtotal(), billFromJson.getCopay(), billFromJson.getTotal());
        assertNotNull(createdBill, "El Bill creado no debe ser nulo");
        assertNotNull(createdBill.getIdBill(), "El ID del Bill debe ser generado");
        assertEquals("125.0", createdBill.getTotal(), "El campo 'total' debe coincidir con el valor esperado");
    }
}
