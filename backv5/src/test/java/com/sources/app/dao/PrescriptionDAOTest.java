package com.sources.app.dao;

import static org.junit.jupiter.api.Assertions.*;
import java.io.InputStream;
import org.junit.jupiter.api.Test;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sources.app.entities.Prescription;
import com.sources.app.entities.Hospital;
import com.sources.app.entities.User;

public class PrescriptionDAOTest {

    private PrescriptionDAO dao = new PrescriptionDAO();
    private ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testPrescriptionFromJsonFile() throws Exception {
        InputStream is = getClass().getResourceAsStream("/com/sources/app/dao/prescription.json");
        assertNotNull(is);
        Prescription p = mapper.readValue(is, Prescription.class);
        assertNotNull(p);
        assertEquals("Y", String.valueOf(p.getApproved()));
    }

    @Test
    public void testCreatePrescriptionFromJson() throws Exception {
        InputStream is = getClass().getResourceAsStream("/com/sources/app/dao/prescription.json");
        assertNotNull(is);
        Prescription pFromJson = mapper.readValue(is, Prescription.class);
        // Persistir hospital y usuario antes de crear la prescripción
        HospitalDAO hospitalDAO = new HospitalDAO();
        UserDAO userDAO = new UserDAO();
        Hospital h = hospitalDAO.create("Hosp Test", "555-0000", "h@test.com", "Addr", '1');
        assertNotNull(h);
        User u = userDAO.create("User Test", "123", "555", "u@test.com", new java.util.Date(), "Addr", "pwd");
        assertNotNull(u);
        Prescription created = dao.create(h, u, pFromJson.getApproved());
        assertNotNull(created);
        assertNotNull(created.getIdPrescription());
        assertEquals(pFromJson.getApproved(), created.getApproved());
    }
}
