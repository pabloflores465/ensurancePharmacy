package com.sources.app;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas de cobertura para la clase App sin arrancar el servidor ni tocar
 * red/DB.
 */
class AppCoverageTest {

    @Test
    void canInstantiatePrivateConstructorViaReflection() throws Exception {
        Constructor<App> ctor = App.class.getDeclaredConstructor();
        ctor.setAccessible(true);
        App instance = ctor.newInstance();
        assertNotNull(instance);
    }

    @Test
    void canInvokeGetLocalExternalIpViaReflection() throws Exception {
        Method m = App.class.getDeclaredMethod("getLocalExternalIp");
        m.setAccessible(true);
        Object result = m.invoke(null);
        assertNotNull(result);
        String ip = String.valueOf(result);
        assertFalse(ip.isBlank());
        // Simple valid IPv4 or fallback 127.0.0.1 check
        assertTrue(ip.matches("^(?:\\d{1,3}\\.){3}\\d{1,3}$"));
    }
}
