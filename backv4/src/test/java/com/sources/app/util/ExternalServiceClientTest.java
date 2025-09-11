package com.sources.app.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ExternalServiceClientTest {

    @Mock
    private HttpURLConnection mockConnection;
    @Mock
    private OutputStream mockOutputStream;

    

    @InjectMocks
    private ExternalServiceClient externalServiceClient; // Instance under test

    @Captor
    ArgumentCaptor<byte[]> outputStreamCaptor;
    @Captor
    ArgumentCaptor<Integer> offsetCaptor;
    @Captor
    ArgumentCaptor<Integer> lengthCaptor;
    

    private final String testEndpoint = "/test-data";
    private final String successResponse = "{\"data\":\"success\"}";
    private final String errorResponse = "{\"error\":\"failed\"}";
    private final Map<String, String> testBody = Map.of("key", "value");
    

    

    

    private MockedStatic<ExternalServiceClient> mockedClientStatic; // static mock for factory

    @BeforeEach
    void setUp() throws IOException {
        // Sin stubs globales para evitar UnnecessaryStubbing. Cada prueba configura lo que necesita.
    }

    @AfterEach
    void tearDown() {
        // Close static mock AFTER tests si fue creado
        if (mockedClientStatic != null) {
            mockedClientStatic.close();
            mockedClientStatic = null;
        }
    }

    // Helper to mock responses
    private void setupMockResponse(int statusCode, String responseBody) throws IOException {
        when(mockConnection.getResponseCode()).thenReturn(statusCode);
        InputStream responseStream = new ByteArrayInputStream(responseBody.getBytes(StandardCharsets.UTF_8));
        InputStream errorStream = new ByteArrayInputStream(responseBody.getBytes(StandardCharsets.UTF_8));

        if (statusCode >= 200 && statusCode < 300) {
            when(mockConnection.getInputStream()).thenReturn(responseStream);
            lenient().when(mockConnection.getErrorStream()).thenReturn(null);
        } else {
            lenient().when(mockConnection.getInputStream()).thenThrow(new IOException("Cannot get input stream on error"));
            lenient().when(mockConnection.getErrorStream()).thenReturn(errorStream);
        }
    }

    // --- Synchronous Tests ---
    @Test
    void get_Hospital_Success() throws Exception {
        // Mockear fábrica solo en esta prueba
        mockedClientStatic = Mockito.mockStatic(ExternalServiceClient.class, Mockito.CALLS_REAL_METHODS);
        mockedClientStatic.when(() -> ExternalServiceClient.open(anyString())).thenReturn(mockConnection);
        setupMockResponse(HttpURLConnection.HTTP_OK, successResponse);

        String result = externalServiceClient.get(testEndpoint, true);
        assertEquals(successResponse, result);

        verify(mockConnection).setRequestMethod("GET");
        verify(mockConnection).setRequestProperty("Content-Type", "application/json");
        verify(mockConnection).getResponseCode();
        verify(mockConnection).getInputStream();
        verify(mockConnection).disconnect();
    }

    @Test
    void post_Success_EmptyBody_ReturnsEmptyString() throws Exception {
        mockedClientStatic = Mockito.mockStatic(ExternalServiceClient.class, Mockito.CALLS_REAL_METHODS);
        mockedClientStatic.when(() -> ExternalServiceClient.open(anyString())).thenReturn(mockConnection);
        setupMockResponse(HttpURLConnection.HTTP_OK, "");
        when(mockConnection.getOutputStream()).thenReturn(mockOutputStream);

        String result = externalServiceClient.post(testEndpoint, testBody, false);
        assertEquals("", result);
        verify(mockConnection).disconnect();
    }

    @Test
    void put_Success_EmptyBody_ReturnsEmptyString() throws Exception {
        mockedClientStatic = Mockito.mockStatic(ExternalServiceClient.class, Mockito.CALLS_REAL_METHODS);
        mockedClientStatic.when(() -> ExternalServiceClient.open(anyString())).thenReturn(mockConnection);
        setupMockResponse(HttpURLConnection.HTTP_OK, "");
        when(mockConnection.getOutputStream()).thenReturn(mockOutputStream);

        String result = externalServiceClient.put(testEndpoint, testBody, true);
        assertEquals("", result);
        verify(mockConnection).disconnect();
    }

    @Test
    void get_Pharmacy_Success() throws Exception {
        mockedClientStatic = Mockito.mockStatic(ExternalServiceClient.class, Mockito.CALLS_REAL_METHODS);
        mockedClientStatic.when(() -> ExternalServiceClient.open(anyString())).thenReturn(mockConnection);
        setupMockResponse(HttpURLConnection.HTTP_OK, successResponse);

        String result = externalServiceClient.get(testEndpoint, false); // isHospital = false
        assertEquals(successResponse, result);
        verify(mockConnection).setRequestMethod("GET");
    }

    @Test
    void get_Success_EmptyBody_ReturnsEmptyString() throws Exception {
        mockedClientStatic = Mockito.mockStatic(ExternalServiceClient.class, Mockito.CALLS_REAL_METHODS);
        mockedClientStatic.when(() -> ExternalServiceClient.open(anyString())).thenReturn(mockConnection);
        setupMockResponse(HttpURLConnection.HTTP_OK, "");

        String result = externalServiceClient.get(testEndpoint, true);
        assertEquals("", result);
        verify(mockConnection).disconnect();
    }

    @Test
    void get_HttpError_ThrowsException() throws Exception {
        mockedClientStatic = Mockito.mockStatic(ExternalServiceClient.class, Mockito.CALLS_REAL_METHODS);
        mockedClientStatic.when(() -> ExternalServiceClient.open(anyString())).thenReturn(mockConnection);
        setupMockResponse(HttpURLConnection.HTTP_NOT_FOUND, errorResponse);

        Exception exception = assertThrows(Exception.class, () -> {
            externalServiceClient.get(testEndpoint, true);
        });
        assertTrue(exception.getMessage().contains("Error en la petición GET: 404"));
        verify(mockConnection).disconnect();
    }

    @Test
    void post_Hospital_Success() throws Exception {
        mockedClientStatic = Mockito.mockStatic(ExternalServiceClient.class, Mockito.CALLS_REAL_METHODS);
        mockedClientStatic.when(() -> ExternalServiceClient.open(anyString())).thenReturn(mockConnection);
        setupMockResponse(HttpURLConnection.HTTP_CREATED, successResponse);
        when(mockConnection.getOutputStream()).thenReturn(mockOutputStream);

        String result = externalServiceClient.post(testEndpoint, testBody, true);
        assertEquals(successResponse, result);

        verify(mockConnection).setRequestMethod("POST");
        verify(mockConnection).setDoOutput(true);
        verify(mockConnection).setRequestProperty("Content-Type", "application/json");
        verify(mockOutputStream).write(outputStreamCaptor.capture(), anyInt(), anyInt());
        String body = new String(outputStreamCaptor.getValue(), StandardCharsets.UTF_8);
        assertTrue(body.contains("\"key\":\"value\""));
        verify(mockConnection).getResponseCode();
        verify(mockConnection).getInputStream();
        verify(mockConnection).disconnect();
    }

    @Test
    void post_HttpError_ThrowsExceptionWithErrorBody() throws Exception {
        mockedClientStatic = Mockito.mockStatic(ExternalServiceClient.class, Mockito.CALLS_REAL_METHODS);
        mockedClientStatic.when(() -> ExternalServiceClient.open(anyString())).thenReturn(mockConnection);
        setupMockResponse(HttpURLConnection.HTTP_BAD_REQUEST, errorResponse);
        when(mockConnection.getOutputStream()).thenReturn(mockOutputStream);

        Exception exception = assertThrows(Exception.class, () -> {
            externalServiceClient.post(testEndpoint, testBody, true);
        });
        assertTrue(exception.getMessage().contains("Error en la petición POST: 400"));
        assertTrue(exception.getMessage().contains(errorResponse));
        verify(mockConnection).getErrorStream();
        verify(mockConnection).disconnect();
    }

    @Test
    void put_Hospital_Success() throws Exception {
        mockedClientStatic = Mockito.mockStatic(ExternalServiceClient.class, Mockito.CALLS_REAL_METHODS);
        mockedClientStatic.when(() -> ExternalServiceClient.open(anyString())).thenReturn(mockConnection);
        setupMockResponse(HttpURLConnection.HTTP_OK, successResponse);
        when(mockConnection.getOutputStream()).thenReturn(mockOutputStream);

        String result = externalServiceClient.put(testEndpoint, testBody, true);
        assertEquals(successResponse, result);

        verify(mockConnection).setRequestMethod("PUT");
        verify(mockConnection).setDoOutput(true);
        verify(mockConnection).setRequestProperty("Content-Type", "application/json");
        verify(mockOutputStream).write(outputStreamCaptor.capture(), offsetCaptor.capture(), lengthCaptor.capture());
        String body = new String(outputStreamCaptor.getValue(), StandardCharsets.UTF_8);
        assertTrue(body.contains("\"key\":\"value\""));
        assertEquals(0, offsetCaptor.getValue());
        verify(mockConnection).getResponseCode();
        verify(mockConnection).getInputStream();
        verify(mockConnection).disconnect();
    }

    @Test
    void put_HttpError_ThrowsException() throws Exception {
        mockedClientStatic = Mockito.mockStatic(ExternalServiceClient.class, Mockito.CALLS_REAL_METHODS);
        mockedClientStatic.when(() -> ExternalServiceClient.open(anyString())).thenReturn(mockConnection);
        setupMockResponse(HttpURLConnection.HTTP_BAD_REQUEST, errorResponse);
        when(mockConnection.getOutputStream()).thenReturn(mockOutputStream);

        Exception exception = assertThrows(Exception.class, () -> {
            externalServiceClient.put(testEndpoint, testBody, false);
        });
        assertTrue(exception.getMessage().contains("Error en la petición PUT: 400"));
        verify(mockConnection).disconnect();
    }

    // --- Async Tests (verify they delegate correctly) ---
    @Test
    void getAsync_DelegatesToGet() throws Exception {
        // We don't need full HTTP mocking here, just mock the sync method
        ExternalServiceClient spiedClient = spy(externalServiceClient);
        doReturn(successResponse).when(spiedClient).get(testEndpoint, true);

        CompletableFuture<String> future = spiedClient.getAsync(testEndpoint, true);
        String result = future.get(); // Wait for completion

        assertEquals(successResponse, result);
        verify(spiedClient).get(testEndpoint, true); // Verify delegation
    }

    @Test
    void getAsync_HandlesException() throws Exception {
        ExternalServiceClient spiedClient = spy(externalServiceClient);
        IOException testException = new IOException("GET failed");
        doThrow(testException).when(spiedClient).get(anyString(), anyBoolean());

        CompletableFuture<String> future = spiedClient.getAsync(testEndpoint, true);

        ExecutionException thrown = assertThrows(ExecutionException.class, () -> {
            try {
                future.get(); // This might throw InterruptedException as well, caught by outer assertThrows
            } catch (InterruptedException _) {
                Thread.currentThread().interrupt(); // Restore interrupt status
                fail("Test interrupted unexpectedly");
            }
        });

        assertTrue(thrown.getCause() instanceof RuntimeException);
        assertEquals(testException, thrown.getCause().getCause());
        verify(spiedClient).get(anyString(), anyBoolean());
    }

    @Test
    void postAsync_DelegatesToPost() throws Exception {
        ExternalServiceClient spiedClient = spy(externalServiceClient);
        doReturn(successResponse).when(spiedClient).post(testEndpoint, testBody, false);

        CompletableFuture<String> future = spiedClient.postAsync(testEndpoint, testBody, false);
        String result = future.get();

        assertEquals(successResponse, result);
        verify(spiedClient).post(testEndpoint, testBody, false);
    }

    @Test
    void putAsync_DelegatesToPut() throws Exception {
        ExternalServiceClient spiedClient = spy(externalServiceClient);
        doReturn(successResponse).when(spiedClient).put(testEndpoint, testBody, true);

        CompletableFuture<String> future = spiedClient.putAsync(testEndpoint, testBody, true);
        String result = future.get();

        assertEquals(successResponse, result);
        verify(spiedClient).put(testEndpoint, testBody, true);
    }

    @Test
    void postAsync_HandlesException() throws Exception {
        ExternalServiceClient spiedClient = spy(externalServiceClient);
        IOException testException = new IOException("POST failed");
        doThrow(testException).when(spiedClient).post(testEndpoint, testBody, true);

        CompletableFuture<String> future = spiedClient.postAsync(testEndpoint, testBody, true);

        ExecutionException thrown = assertThrows(ExecutionException.class, () -> {
            try {
                future.get();
            } catch (InterruptedException _) {
                Thread.currentThread().interrupt();
                fail("Test interrupted unexpectedly");
            }
        });

        assertTrue(thrown.getCause() instanceof RuntimeException);
        assertEquals(testException, thrown.getCause().getCause());
        verify(spiedClient).post(testEndpoint, testBody, true);
    }

    @Test
    void putAsync_HandlesException() throws Exception {
        ExternalServiceClient spiedClient = spy(externalServiceClient);
        IOException testException = new IOException("PUT failed");
        doThrow(testException).when(spiedClient).put(testEndpoint, testBody, false);

        CompletableFuture<String> future = spiedClient.putAsync(testEndpoint, testBody, false);

        ExecutionException thrown = assertThrows(ExecutionException.class, () -> {
            try {
                future.get();
            } catch (InterruptedException _) {
                Thread.currentThread().interrupt();
                fail("Test interrupted unexpectedly");
            }
        });

        assertTrue(thrown.getCause() instanceof RuntimeException);
        assertEquals(testException, thrown.getCause().getCause());
        verify(spiedClient).put(testEndpoint, testBody, false);
    }

    @Test
    void post_Error_NullErrorStream_ThrowsException() throws Exception {
        mockedClientStatic = Mockito.mockStatic(ExternalServiceClient.class, Mockito.CALLS_REAL_METHODS);
        mockedClientStatic.when(() -> ExternalServiceClient.open(anyString())).thenReturn(mockConnection);
        when(mockConnection.getOutputStream()).thenReturn(mockOutputStream);
        when(mockConnection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_BAD_REQUEST);
        // Return null error stream to exercise else-branch with null stream
        lenient().when(mockConnection.getErrorStream()).thenReturn(null);
        // Input stream should not be called on error
        lenient().when(mockConnection.getInputStream()).thenThrow(new IOException("No input on error"));

        assertThrows(com.sources.app.exceptions.ExternalServiceException.class, () -> externalServiceClient.post(testEndpoint, testBody, true));
        verify(mockConnection).disconnect();
    }

    @Test
    void post_OutputStreamThrowsIOException_PropagatesAndDisconnects() throws Exception {
        mockedClientStatic = Mockito.mockStatic(ExternalServiceClient.class, Mockito.CALLS_REAL_METHODS);
        mockedClientStatic.when(() -> ExternalServiceClient.open(anyString())).thenReturn(mockConnection);
        when(mockConnection.getOutputStream()).thenThrow(new IOException("OS failure"));

        assertThrows(IOException.class, () -> externalServiceClient.post(testEndpoint, testBody, false));
    }

    @Test
    void post_InputStreamThrowsIOException_PropagatesAndDisconnects() throws Exception {
        mockedClientStatic = Mockito.mockStatic(ExternalServiceClient.class, Mockito.CALLS_REAL_METHODS);
        mockedClientStatic.when(() -> ExternalServiceClient.open(anyString())).thenReturn(mockConnection);
        when(mockConnection.getOutputStream()).thenReturn(mockOutputStream);
        when(mockConnection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_OK);
        when(mockConnection.getInputStream()).thenThrow(new IOException("IS failure on POST"));

        assertThrows(IOException.class, () -> externalServiceClient.post(testEndpoint, testBody, true));
        verify(mockConnection).disconnect();
    }

    @Test
    void get_InputStreamThrowsIOException_PropagatesAndDisconnects() throws Exception {
        mockedClientStatic = Mockito.mockStatic(ExternalServiceClient.class, Mockito.CALLS_REAL_METHODS);
        mockedClientStatic.when(() -> ExternalServiceClient.open(anyString())).thenReturn(mockConnection);
        when(mockConnection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_OK);
        when(mockConnection.getInputStream()).thenThrow(new IOException("IS failure"));

        assertThrows(IOException.class, () -> externalServiceClient.get(testEndpoint, true));
        verify(mockConnection).disconnect();
    }

    @Test
    void put_OutputStreamThrowsIOException_Propagates() throws Exception {
        mockedClientStatic = Mockito.mockStatic(ExternalServiceClient.class, Mockito.CALLS_REAL_METHODS);
        mockedClientStatic.when(() -> ExternalServiceClient.open(anyString())).thenReturn(mockConnection);
        when(mockConnection.getOutputStream()).thenThrow(new IOException("OS failure on PUT"));

        assertThrows(IOException.class, () -> externalServiceClient.put(testEndpoint, testBody, true));
        // Do not strictly verify disconnect; production code may not reach finally when OS retrieval fails
    }

    @Test
    void put_InputStreamThrowsIOException_PropagatesAndDisconnects() throws Exception {
        mockedClientStatic = Mockito.mockStatic(ExternalServiceClient.class, Mockito.CALLS_REAL_METHODS);
        mockedClientStatic.when(() -> ExternalServiceClient.open(anyString())).thenReturn(mockConnection);
        when(mockConnection.getOutputStream()).thenReturn(mockOutputStream);
        when(mockConnection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_OK);
        when(mockConnection.getInputStream()).thenThrow(new IOException("IS failure on PUT"));

        assertThrows(IOException.class, () -> externalServiceClient.put(testEndpoint, testBody, false));
        verify(mockConnection).disconnect();
    }

    @Test
    void open_StaticMethod_ReturnsHttpURLConnectionWithoutConnecting() throws Exception {
        HttpURLConnection conn = ExternalServiceClient.open("http://127.0.0.1");
        assertNotNull(conn);
        assertTrue(conn.getURL().toString().startsWith("http://127.0.0.1"));
        // Avoid calling methods that force a network connection
        conn.disconnect();
    }

}
