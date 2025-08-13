package com.sources.app.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ExternalServiceClientTest {

    @Test
    public void testExternalServiceClientInstantiation() {
        // TODO: implement tests for ExternalServiceClient
        ExternalServiceClient instance = new ExternalServiceClient();
        assertNotNull(instance);
    }

    @Test
    public void testGetBaseUrlValidation() {
        ExternalServiceClient client = new ExternalServiceClient();
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> client.get("unknown", "/ping"));
        assertTrue(ex.getMessage().toLowerCase().contains("tipo de servicio"));
    }
}
