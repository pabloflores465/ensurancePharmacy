package com.sources.app.entities;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BillMedicineIdTest {

    @Test
    public void testBillMedicineIdInstantiation() {
        BillMedicineId instance = new BillMedicineId();
        assertNotNull(instance);
    }

    @Test
    public void testBillMedicineIdConstructorWithParameters() {
        Long billId = 1L;
        Long medicineId = 2L;
        BillMedicineId id = new BillMedicineId(billId, medicineId);
        
        assertEquals(billId, id.getBillId());
        assertEquals(medicineId, id.getMedicineId());
    }

    @Test
    public void testBillMedicineIdSettersAndGetters() {
        BillMedicineId id = new BillMedicineId();
        Long billId = 10L;
        Long medicineId = 20L;
        
        id.setBillId(billId);
        id.setMedicineId(medicineId);
        
        assertEquals(billId, id.getBillId());
        assertEquals(medicineId, id.getMedicineId());
    }

    @Test
    public void testBillMedicineIdEquals() {
        BillMedicineId id1 = new BillMedicineId(1L, 2L);
        BillMedicineId id2 = new BillMedicineId(1L, 2L);
        BillMedicineId id3 = new BillMedicineId(2L, 3L);
        
        // Test equality
        assertEquals(id1, id2);
        assertNotEquals(id1, id3);
        
        // Test reflexivity
        assertEquals(id1, id1);
        
        // Test null comparison
        assertNotEquals(id1, null);
        
        // Test different class comparison
        assertNotEquals(id1, "not a BillMedicineId");
    }

    @Test
    public void testBillMedicineIdHashCode() {
        BillMedicineId id1 = new BillMedicineId(1L, 2L);
        BillMedicineId id2 = new BillMedicineId(1L, 2L);
        BillMedicineId id3 = new BillMedicineId(2L, 3L);
        
        // Equal objects should have equal hash codes
        assertEquals(id1.hashCode(), id2.hashCode());
        
        // Different objects should typically have different hash codes
        assertNotEquals(id1.hashCode(), id3.hashCode());
    }

    @Test
    public void testBillMedicineIdWithNullValues() {
        BillMedicineId id1 = new BillMedicineId(null, null);
        BillMedicineId id2 = new BillMedicineId(null, null);
        BillMedicineId id3 = new BillMedicineId(1L, null);
        
        assertEquals(id1, id2);
        assertNotEquals(id1, id3);
        assertEquals(id1.hashCode(), id2.hashCode());
    }
}
