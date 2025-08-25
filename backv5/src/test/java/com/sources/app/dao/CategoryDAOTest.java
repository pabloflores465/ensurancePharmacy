package com.sources.app.dao;

import static org.junit.jupiter.api.Assertions.*;
import java.io.InputStream;
import org.junit.jupiter.api.Test;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sources.app.entities.Category;

public class CategoryDAOTest {
    private final CategoryDAO dao = new CategoryDAO();
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testCategoryFromJson() throws Exception {
        InputStream is = getClass().getResourceAsStream("/com/sources/app/dao/category.json");
        assertNotNull(is);
        Category cat = mapper.readValue(is, Category.class);
        assertNotNull(cat);
        assertEquals("Electronics", cat.getName());
    }

    @Test
    public void testCreateCategoryFromJson() throws Exception {
        InputStream is = getClass().getResourceAsStream("/com/sources/app/dao/category.json");
        assertNotNull(is);
        Category cat = mapper.readValue(is, Category.class);
        Category created = dao.create(cat.getName());
        assertNotNull(created);
        assertNotNull(created.getIdCategory());
        assertEquals(cat.getName(), created.getName());
    }

    @Test
    public void testGetByIdAndUpdateCategory() throws Exception {
        InputStream is = getClass().getResourceAsStream("/com/sources/app/dao/category.json");
        assertNotNull(is);
        Category cat = mapper.readValue(is, Category.class);
        Category created = dao.create(cat.getName());
        assertNotNull(created);
        Long id = created.getIdCategory();
        assertNotNull(id);
        // getById
        Category fetched = dao.getById(id);
        assertNotNull(fetched);
        assertEquals(created.getName(), fetched.getName());
        // update
        fetched.setName(created.getName() + " Updated");
        Category updated = dao.update(fetched);
        assertNotNull(updated);
        assertEquals(fetched.getName(), updated.getName());
    }

    @Test
    public void testGetAllReturnsListWithAtLeastOne() throws Exception {
        // Ensure at least one record exists
        InputStream is = getClass().getResourceAsStream("/com/sources/app/dao/category.json");
        assertNotNull(is);
        Category cat = mapper.readValue(is, Category.class);
        Category created = dao.create(cat.getName() + System.currentTimeMillis());
        assertNotNull(created);
        assertNotNull(created.getIdCategory());
        assertTrue(dao.getAll() != null && !dao.getAll().isEmpty());
    }
}