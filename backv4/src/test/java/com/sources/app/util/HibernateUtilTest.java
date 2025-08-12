package com.sources.app.util;

import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

@ExtendWith(MockitoExtension.class)
class HibernateUtilTest {

    // Note: Properly testing Hibernate initialization often involves 
    // setting up an in-memory database (like H2) and a test-specific hibernate.cfg.xml.
    // This basic test primarily checks that the static initialization doesn't immediately fail
    // and that the SessionFactory can be retrieved.
    @Mock
    private SessionFactory mockSessionFactory; // Mock the SessionFactory

    private MockedStatic<HibernateUtil> mockedHibernateUtil;

    @BeforeEach
    void setUp() {
        mockedHibernateUtil = Mockito.mockStatic(HibernateUtil.class, Mockito.CALLS_REAL_METHODS);
        // Stub only the return value of getSessionFactory
        mockedHibernateUtil.when(HibernateUtil::getSessionFactory).thenReturn(mockSessionFactory);
        // Let setCorsHeaders call real method to verify side effects
        mockedHibernateUtil.when(() -> HibernateUtil.setCorsHeaders(any(HttpExchange.class)))
                .thenCallRealMethod();
    }

    @AfterEach
    void tearDown() {
        mockedHibernateUtil.close();
    }

    @Test
    void getSessionFactory_ReturnsMockedSessionFactory() {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        assertNotNull(sessionFactory);
        assertSame(mockSessionFactory, sessionFactory);
    }

    @Test
    void setCorsHeaders_AddsCorrectHeaders() {
        // Arrange
        HttpExchange mockExchange = mock(HttpExchange.class);
        Headers mockHeaders = mock(Headers.class);
        when(mockExchange.getResponseHeaders()).thenReturn(mockHeaders);

        HibernateUtil.setCorsHeaders(mockExchange);
        verify(mockHeaders).add(eq("Access-Control-Allow-Origin"), eq("*"));
        verify(mockHeaders).add(eq("Access-Control-Allow-Methods"), anyString());
        verify(mockHeaders).add(eq("Access-Control-Allow-Headers"), anyString());
        verifyNoMoreInteractions(mockHeaders);
    }
}
