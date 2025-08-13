package com.sources.app.handlers;

import com.sources.app.dao.HospitalDAO;
import com.sources.app.entities.Hospital;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.URI;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HospitalServiceProxyHandlerTest {

    @Mock
    private HospitalDAO mockHospitalDAO;
    @Mock
    private HttpExchange mockHttpExchange;
    @Mock
    private Headers mockRequestHeaders;
    @Mock
    private Headers mockResponseHeaders;
    @Mock
    private OutputStream mockResponseBody;

    @InjectMocks
    private HospitalServiceProxyHandler handler;

    @Captor
    ArgumentCaptor<Integer> statusCodeCaptor;
    @Captor
    ArgumentCaptor<Long> responseLengthCaptor;
    @Captor
    ArgumentCaptor<byte[]> responseBodyCaptor;

    private int ephemeralPort;

    @BeforeEach
    void setUp() throws IOException {
        lenient().when(mockHttpExchange.getResponseHeaders()).thenReturn(mockResponseHeaders);
        lenient().when(mockHttpExchange.getResponseBody()).thenReturn(mockResponseBody);
        lenient().when(mockHttpExchange.getRequestHeaders()).thenReturn(mockRequestHeaders);

        // Assign random available port for the embedded test server
        try (ServerSocket socket = new ServerSocket(0)) {
            ephemeralPort = socket.getLocalPort();
        }
    }

    @Test
    void handle_OptionsRequest_SendsNoContent() throws IOException {
        when(mockHttpExchange.getRequestMethod()).thenReturn("OPTIONS");
        handler.handle(mockHttpExchange);
        verify(mockHttpExchange).sendResponseHeaders(eq(204), eq(-1L));
        verifyNoInteractions(mockHospitalDAO);
    }

    @Test
    void handle_WrongBasePath_SendsNotFound() throws IOException {
        when(mockHttpExchange.getRequestURI()).thenReturn(URI.create("/api/wrong"));
        when(mockHttpExchange.getRequestMethod()).thenReturn("GET");
        handler.handle(mockHttpExchange);
        verify(mockHttpExchange).sendResponseHeaders(eq(404), anyLong());
    }

    @Test
    void handle_MissingHospitalId_BadRequest() throws IOException {
        when(mockHttpExchange.getRequestURI()).thenReturn(URI.create("/api/hospital-proxy"));
        when(mockHttpExchange.getRequestMethod()).thenReturn("GET");
        handler.handle(mockHttpExchange);
        verify(mockHttpExchange).sendResponseHeaders(eq(400), anyLong());
    }

    @Test
    void handle_InvalidHospitalIdInPath_BadRequest() throws IOException {
        when(mockHttpExchange.getRequestURI()).thenReturn(URI.create("/api/hospital-proxy/abc/status"));
        when(mockHttpExchange.getRequestMethod()).thenReturn("GET");

        handler.handle(mockHttpExchange);

        verify(mockHttpExchange).sendResponseHeaders(eq(400), anyLong());
        verifyNoInteractions(mockHospitalDAO);
    }

    @Test
    void handle_HospitalNotFound_NotFound() throws IOException {
        when(mockHttpExchange.getRequestURI()).thenReturn(URI.create("/api/hospital-proxy/123/status"));
        when(mockHttpExchange.getRequestMethod()).thenReturn("GET");
        when(mockHospitalDAO.findById(123L)).thenReturn(null);
        handler.handle(mockHttpExchange);
        verify(mockHospitalDAO).findById(123L);
        verify(mockHttpExchange).sendResponseHeaders(eq(404), anyLong());
    }

    @Test
    void handle_ForwardsGetToEmbeddedServer_UsesHeaderPortWhenPresent() throws Exception {
        // Start minimal HTTP server responding on ephemeralPort
        com.sun.net.httpserver.HttpServer server = com.sun.net.httpserver.HttpServer.create(new InetSocketAddress("localhost", ephemeralPort), 0);
        // Note: current handler forwards path including the hospitalId segment ("/1/status")
        server.createContext("/1/status", exchange -> {
            byte[] payload = "{\"ok\":true}".getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, payload.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(payload);
            }
        });
        server.start();
        try {
            Hospital hospital = new Hospital();
            hospital.setPort("9999"); // Should be overridden by header
            when(mockHospitalDAO.findById(1L)).thenReturn(hospital);

            when(mockRequestHeaders.getFirst("X-Hospital-Port")).thenReturn(String.valueOf(ephemeralPort));
            when(mockHttpExchange.getRequestURI()).thenReturn(URI.create("/api/hospital-proxy/1/status"));
            when(mockHttpExchange.getRequestMethod()).thenReturn("GET");

            handler.handle(mockHttpExchange);

            verify(mockHttpExchange).sendResponseHeaders(statusCodeCaptor.capture(), responseLengthCaptor.capture());
            verify(mockResponseBody).write(responseBodyCaptor.capture());
            verify(mockResponseBody).close();
            assertEquals(200, statusCodeCaptor.getValue());
            assertEquals("{\"ok\":true}", new String(responseBodyCaptor.getValue(), StandardCharsets.UTF_8));
        } finally {
            server.stop(0);
        }
    }

    @Test
    void handle_ForwardsPostAndCopiesBody() throws Exception {
        com.sun.net.httpserver.HttpServer server = com.sun.net.httpserver.HttpServer.create(new InetSocketAddress("localhost", ephemeralPort), 0);
        // Note: current handler forwards path including the hospitalId segment ("/7/patients")
        server.createContext("/7/patients", exchange -> {
            byte[] body = exchange.getRequestBody().readAllBytes();
            // Echo back the request body
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, body.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(body);
            }
        });
        server.start();
        try {
            Hospital hospital = new Hospital();
            when(mockHospitalDAO.findById(7L)).thenReturn(hospital); // No port set -> defaults to 8000, but we set header

            when(mockRequestHeaders.getFirst("X-Hospital-Port")).thenReturn(String.valueOf(ephemeralPort));
            when(mockHttpExchange.getRequestURI()).thenReturn(URI.create("/api/hospital-proxy/7/patients"));
            when(mockHttpExchange.getRequestMethod()).thenReturn("POST");

            String payload = "{\"name\":\"Jane\"}";
            when(mockHttpExchange.getRequestBody()).thenReturn(new ByteArrayInputStream(payload.getBytes(StandardCharsets.UTF_8)));

            handler.handle(mockHttpExchange);

            verify(mockHttpExchange).sendResponseHeaders(statusCodeCaptor.capture(), responseLengthCaptor.capture());
            verify(mockResponseBody).write(responseBodyCaptor.capture());
            verify(mockResponseBody).close();
            assertEquals(200, statusCodeCaptor.getValue());
            assertEquals(payload, new String(responseBodyCaptor.getValue(), StandardCharsets.UTF_8));
        } finally {
            server.stop(0);
        }
    }

    @Test
    void handle_BackendUnreachable_SendsBadGateway() throws Exception {
        // Do not start a server on ephemeralPort to force connection failure
        Hospital hospital = new Hospital();
        when(mockHospitalDAO.findById(9L)).thenReturn(hospital);

        when(mockRequestHeaders.getFirst("X-Hospital-Port")).thenReturn(String.valueOf(ephemeralPort));
        when(mockHttpExchange.getRequestURI()).thenReturn(URI.create("/api/hospital-proxy/9/status"));
        when(mockHttpExchange.getRequestMethod()).thenReturn("GET");

        handler.handle(mockHttpExchange);

        verify(mockHttpExchange).sendResponseHeaders(statusCodeCaptor.capture(), responseLengthCaptor.capture());
        verify(mockResponseBody).write(responseBodyCaptor.capture());
        verify(mockResponseBody).close();
        assertEquals(502, statusCodeCaptor.getValue());
        String body = new String(responseBodyCaptor.getValue(), StandardCharsets.UTF_8);
        assertTrue(body.contains("Error al comunicarse con el servicio del hospital"));
    }

    @Test
    void handle_SetsDefaultContentTypeWhenBackendOmitsIt() throws Exception {
        com.sun.net.httpserver.HttpServer server = com.sun.net.httpserver.HttpServer.create(new InetSocketAddress("localhost", ephemeralPort), 0);
        server.createContext("/2/no-content-type", exchange -> {
            byte[] payload = "{\"echo\":true}".getBytes(StandardCharsets.UTF_8);
            // Intentionally do not set Content-Type header
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, payload.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(payload);
            }
        });
        server.start();
        try {
            Hospital hospital = new Hospital();
            when(mockHospitalDAO.findById(2L)).thenReturn(hospital);
            when(mockRequestHeaders.getFirst("X-Hospital-Port")).thenReturn(String.valueOf(ephemeralPort));
            when(mockHttpExchange.getRequestURI()).thenReturn(URI.create("/api/hospital-proxy/2/no-content-type"));
            when(mockHttpExchange.getRequestMethod()).thenReturn("GET");

            handler.handle(mockHttpExchange);

            verify(mockResponseHeaders, atLeastOnce()).add(eq("Content-Type"), eq("application/json"));
            verify(mockHttpExchange).sendResponseHeaders(statusCodeCaptor.capture(), responseLengthCaptor.capture());
            verify(mockResponseBody).write(responseBodyCaptor.capture());
            assertEquals(200, statusCodeCaptor.getValue());
            assertEquals("{\"echo\":true}", new String(responseBodyCaptor.getValue(), StandardCharsets.UTF_8));
        } finally {
            server.stop(0);
        }
    }

    @Test
    void handle_ForwardsCustomHeadersToBackend() throws Exception {
        com.sun.net.httpserver.HttpServer server = com.sun.net.httpserver.HttpServer.create(new InetSocketAddress("localhost", ephemeralPort), 0);
        server.createContext("/3/echo-header", exchange -> {
            String headerVal = exchange.getRequestHeaders().getFirst("X-Custom-Token");
            byte[] payload = (headerVal == null ? "" : headerVal).getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "text/plain");
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, payload.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(payload);
            }
        });
        server.start();
        try {
            Hospital hospital = new Hospital();
            when(mockHospitalDAO.findById(3L)).thenReturn(hospital);
            when(mockRequestHeaders.getFirst("X-Hospital-Port")).thenReturn(String.valueOf(ephemeralPort));
            when(mockHttpExchange.getRequestURI()).thenReturn(URI.create("/api/hospital-proxy/3/echo-header"));
            when(mockHttpExchange.getRequestMethod()).thenReturn("GET");
            when(mockRequestHeaders.entrySet()).thenReturn(java.util.Map.of("X-Custom-Token", java.util.List.of("abc123")).entrySet());

            handler.handle(mockHttpExchange);

            verify(mockHttpExchange).sendResponseHeaders(statusCodeCaptor.capture(), responseLengthCaptor.capture());
            verify(mockResponseBody).write(responseBodyCaptor.capture());
            verify(mockResponseBody).close();
            assertEquals(200, statusCodeCaptor.getValue());
            assertEquals("abc123", new String(responseBodyCaptor.getValue(), StandardCharsets.UTF_8));
        } finally {
            server.stop(0);
        }
    }

    @Test
    void handle_BackendReturns404_ProxyPassesThroughStatusAndBody() throws Exception {
        com.sun.net.httpserver.HttpServer server = com.sun.net.httpserver.HttpServer.create(new InetSocketAddress("localhost", ephemeralPort), 0);
        server.createContext("/4/not-found", exchange -> {
            byte[] payload = "{\"error\":\"missing\"}".getBytes(StandardCharsets.UTF_8);
            // Send 404 with an error body (as error stream)
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_NOT_FOUND, payload.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(payload);
            }
        });
        server.start();
        try {
            Hospital hospital = new Hospital();
            when(mockHospitalDAO.findById(4L)).thenReturn(hospital);
            when(mockRequestHeaders.getFirst("X-Hospital-Port")).thenReturn(String.valueOf(ephemeralPort));
            when(mockHttpExchange.getRequestURI()).thenReturn(URI.create("/api/hospital-proxy/4/not-found"));
            when(mockHttpExchange.getRequestMethod()).thenReturn("GET");

            handler.handle(mockHttpExchange);

            verify(mockHttpExchange).sendResponseHeaders(statusCodeCaptor.capture(), responseLengthCaptor.capture());
            verify(mockResponseBody).write(responseBodyCaptor.capture());
            verify(mockResponseBody).close();
            assertEquals(404, statusCodeCaptor.getValue());
            assertEquals("{\"error\":\"missing\"}", new String(responseBodyCaptor.getValue(), StandardCharsets.UTF_8));
        } finally {
            server.stop(0);
        }
    }
}
