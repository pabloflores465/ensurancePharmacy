package com.sources.app.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.hibernate.SessionFactory;

public class HibernateUtilTest {

    @Test
    public void testGetSessionFactoryNotNull() {
        SessionFactory sf = HibernateUtil.getSessionFactory();
        assertNotNull(sf);
    }
}
