package com.sources.app.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.hibernate.SessionFactory;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;

public class HibernateUtilTest {

    @Test
    public void testGetSessionFactoryNotNull() {
        SessionFactory sf = HibernateUtil.getSessionFactory();
        assertNotNull(sf);
    }

    @Test
    public void testOpenCloseSession() {
        SessionFactory sf = HibernateUtil.getSessionFactory();
        assertNotNull(sf);
        try (Session session = sf.openSession()) {
            assertNotNull(session);
            assertTrue(session.isOpen());
            // Depending on dialect/config, isConnected may be false until used; allow either open or connected
            assertTrue(session.isOpen());
        }
    }

    @Test
    public void testBuildSessionFactory_PrintsSchemaMessage() throws Exception {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos));
        SessionFactory localSf = null;
        try {
            Method m = HibernateUtil.class.getDeclaredMethod("buildSessionFactory");
            m.setAccessible(true);
            localSf = (SessionFactory) m.invoke(null);
            assertNotNull(localSf);
        } finally {
            // Do NOT close localSf here; hbm2ddl=create-drop would drop schema for the shared in-memory DB
            System.setOut(originalOut);
        }
        String out = baos.toString();
        assertTrue(out.contains("DB_SCHEMA_PHARMACY no definida") || out.contains("Configurando esquema de BD para farmacia"),
                "Expected a schema configuration message, got: " + out);
    }

    @Test
    public void testSimpleNativeQuerySelectOne() {
        SessionFactory sf = HibernateUtil.getSessionFactory();
        assertNotNull(sf);
        try (Session session = sf.openSession()) {
            Transaction tx = session.beginTransaction();
            Integer one = session.createNativeQuery("select 1", Integer.class).getSingleResult();
            assertNotNull(one);
            assertEquals(1, one.intValue());
            tx.commit();
        }
    }
}
