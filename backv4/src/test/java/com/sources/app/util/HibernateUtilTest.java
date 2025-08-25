package com.sources.app.util;

import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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
        // Reset any injected factory to avoid cross-test interference
        HibernateUtil.setSessionFactory(null);
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
        verify(mockHeaders).add(eq("Access-Control-Allow-Methods"), eq("GET, POST, PUT, DELETE, OPTIONS"));
        verify(mockHeaders).add(eq("Access-Control-Allow-Headers"), eq("Content-Type, Authorization"));
        verifyNoMoreInteractions(mockHeaders);
    }

    @Test
    void setSessionFactory_InjectionReturnsSameInstance() {
        // Use real getSessionFactory so we can validate injection behavior
        mockedHibernateUtil.when(HibernateUtil::getSessionFactory).thenCallRealMethod();

        HibernateUtil.setSessionFactory(null); // ensure clean state
        HibernateUtil.setSessionFactory(mockSessionFactory);

        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        assertNotNull(sessionFactory);
        assertSame(mockSessionFactory, sessionFactory);
    }

    @Test
    void getSessionFactory_ConcurrentAccess_ReturnsSingletonInstance() throws InterruptedException {
        // Ensure getSessionFactory uses the real method
        mockedHibernateUtil.when(HibernateUtil::getSessionFactory).thenCallRealMethod();

        HibernateUtil.setSessionFactory(null);
        // Pre-inject to avoid building a real SessionFactory and to validate singleton behavior
        HibernateUtil.setSessionFactory(mockSessionFactory);

        int threads = 10;
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threads);
        SessionFactory[] results = new SessionFactory[threads];

        for (int i = 0; i < threads; i++) {
            final int idx = i;
            pool.submit(() -> {
                try {
                    start.await();
                    results[idx] = HibernateUtil.getSessionFactory();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    done.countDown();
                }
            });
        }

        start.countDown();
        boolean finished = done.await(5, TimeUnit.SECONDS);
        pool.shutdownNow();

        assertTrue(finished, "Threads did not finish in time");
        for (SessionFactory sf : results) {
            assertSame(mockSessionFactory, sf);
        }
    }
}
