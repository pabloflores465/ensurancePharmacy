package com.sources.app.dao;

import static org.junit.jupiter.api.Assertions.*;
import java.io.InputStream;
import org.junit.jupiter.api.Test;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sources.app.entities.Subcategory;

public class SubcategoryDAOTest {
    private SubcategoryDAO dao = new SubcategoryDAO();
    private ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testSubcategoryFromJson() throws Exception {
        InputStream is = getClass().getResourceAsStream("/com/sources/app/dao/subcategory.json");
        assertNotNull(is);
        Subcategory sub = mapper.readValue(is, Subcategory.class);
        assertNotNull(sub);
        assertEquals("Test Subcategory", sub.getName());
    }

    @Test
    public void testCreateSubcategoryFromJson() throws Exception {
        InputStream is = getClass().getResourceAsStream("/com/sources/app/dao/subcategory.json");
        assertNotNull(is);
        Subcategory subFromJson = mapper.readValue(is, Subcategory.class);
        Subcategory created = dao.create(subFromJson.getName());
        assertNotNull(created);
        assertNotNull(created.getIdSubcategory());
        assertEquals(subFromJson.getName(), created.getName());
    }
}