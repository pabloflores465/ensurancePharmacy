package com.sources.app;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;

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
}
