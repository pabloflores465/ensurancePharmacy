package com.sources.app.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;

public class HospitalClientTest {

    @Test
    public void testHospitalClientInstantiation() {
        // TODO: implement tests for HospitalClient
        HospitalClient instance = new HospitalClient();
        assertNotNull(instance);
    }

    @Test
    public void testGetThrowsIOExceptionWhenServerUnavailable() {
        assertThrows(IOException.class, () -> HospitalClient.get("/unavailable"));
    }

    @Test
    public void testPostThrowsIOExceptionWhenServerUnavailable() {
        assertThrows(IOException.class, () -> HospitalClient.post("/unavailable", new Object()));
    }

    @Test
    public void testPutThrowsIOExceptionWhenServerUnavailable() {
        assertThrows(IOException.class, () -> HospitalClient.put("/unavailable", new Object()));
    }
}
