package com.sources.app.util;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HibernateUtilTest {

    // Note: Properly testing Hibernate initialization often involves 
    // setting up an in-memory database (like H2) and a test-specific hibernate.cfg.xml.
    // This basic test primarily checks that the static initialization doesn't immediately fail
    // and that the SessionFactory can be retrieved.

    @Test
    void getSessionFactory_ReturnsNonNullSessionFactory() {
        SessionFactory sessionFactory = null;
        ExceptionInInitializerError initError = null;
        try {
            // Attempt to get the session factory, which triggers static initialization if not already done.
            sessionFactory = HibernateUtil.getSessionFactory();
        } catch (ExceptionInInitializerError e) {
            initError = e; // Catch initialization errors
        }
        
        // Assert that no initialization error occurred AND the factory is not null.
        // This implies hibernate.cfg.xml was found and parsed without immediate exceptions.
        assertNull(initError, "HibernateUtil static initialization failed: " + (initError != null ? initError.getCause() : "Unknown"));
        assertNotNull(sessionFactory, "SessionFactory should not be null after initialization.");
        
        // Optionally, assert it returns the same instance (singleton pattern)
        // SessionFactory sessionFactory2 = HibernateUtil.getSessionFactory();
        // assertSame(sessionFactory, sessionFactory2, "getSessionFactory should return the same instance.");
    }

    @Test
    void setCorsHeaders_AddsCorrectHeaders() {
        // Arrange
        HttpExchange mockExchange = mock(HttpExchange.class);
        Headers mockHeaders = mock(Headers.class);
        when(mockExchange.getResponseHeaders()).thenReturn(mockHeaders);

        // Act
        HibernateUtil.setCorsHeaders(mockExchange);

        // Assert
        verify(mockHeaders).add(eq("Access-Control-Allow-Origin"), eq("*"));
        verify(mockHeaders).add(eq("Access-Control-Allow-Methods"), anyString());
        verify(mockHeaders).add(eq("Access-Control-Allow-Headers"), anyString());
        verifyNoMoreInteractions(mockHeaders);
    }
} 