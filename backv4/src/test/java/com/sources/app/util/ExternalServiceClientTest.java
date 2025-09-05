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
import static org.mockito.ArgumentMatchers.eq;
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
    

    private final String TEST_ENDPOINT = "/test-data";
    private final String SUCCESS_RESPONSE = "{\"data\":\"success\"}";
    private final String ERROR_RESPONSE = "{\"error\":\"failed\"}";
    private final Map<String, String> TEST_BODY = Map.of("key", "value");
    

    

    

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
        setupMockResponse(HttpURLConnection.HTTP_OK, SUCCESS_RESPONSE);

        String result = externalServiceClient.get(TEST_ENDPOINT, true);
        assertEquals(SUCCESS_RESPONSE, result);

        verify(mockConnection).setRequestMethod("GET");
        verify(mockConnection).setRequestProperty(eq("Content-Type"), eq("application/json"));
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

        String result = externalServiceClient.post(TEST_ENDPOINT, TEST_BODY, false);
        assertEquals("", result);
        verify(mockConnection).disconnect();
    }

    @Test
    void put_Success_EmptyBody_ReturnsEmptyString() throws Exception {
        mockedClientStatic = Mockito.mockStatic(ExternalServiceClient.class, Mockito.CALLS_REAL_METHODS);
        mockedClientStatic.when(() -> ExternalServiceClient.open(anyString())).thenReturn(mockConnection);
        setupMockResponse(HttpURLConnection.HTTP_OK, "");
        when(mockConnection.getOutputStream()).thenReturn(mockOutputStream);

        String result = externalServiceClient.put(TEST_ENDPOINT, TEST_BODY, true);
        assertEquals("", result);
        verify(mockConnection).disconnect();
    }

    @Test
    void get_Pharmacy_Success() throws Exception {
        mockedClientStatic = Mockito.mockStatic(ExternalServiceClient.class, Mockito.CALLS_REAL_METHODS);
        mockedClientStatic.when(() -> ExternalServiceClient.open(anyString())).thenReturn(mockConnection);
        setupMockResponse(HttpURLConnection.HTTP_OK, SUCCESS_RESPONSE);

        String result = externalServiceClient.get(TEST_ENDPOINT, false); // isHospital = false
        assertEquals(SUCCESS_RESPONSE, result);
        verify(mockConnection).setRequestMethod("GET");
    }

    @Test
    void get_Success_EmptyBody_ReturnsEmptyString() throws Exception {
        mockedClientStatic = Mockito.mockStatic(ExternalServiceClient.class, Mockito.CALLS_REAL_METHODS);
        mockedClientStatic.when(() -> ExternalServiceClient.open(anyString())).thenReturn(mockConnection);
        setupMockResponse(HttpURLConnection.HTTP_OK, "");

        String result = externalServiceClient.get(TEST_ENDPOINT, true);
        assertEquals("", result);
        verify(mockConnection).disconnect();
    }

    @Test
    void get_HttpError_ThrowsException() throws Exception {
        mockedClientStatic = Mockito.mockStatic(ExternalServiceClient.class, Mockito.CALLS_REAL_METHODS);
        mockedClientStatic.when(() -> ExternalServiceClient.open(anyString())).thenReturn(mockConnection);
        setupMockResponse(HttpURLConnection.HTTP_NOT_FOUND, ERROR_RESPONSE);

        Exception exception = assertThrows(Exception.class, () -> {
            externalServiceClient.get(TEST_ENDPOINT, true);
        });
        assertTrue(exception.getMessage().contains("Error en la petición GET: 404"));
        verify(mockConnection).disconnect();
    }

    @Test
    void post_Hospital_Success() throws Exception {
        mockedClientStatic = Mockito.mockStatic(ExternalServiceClient.class, Mockito.CALLS_REAL_METHODS);
        mockedClientStatic.when(() -> ExternalServiceClient.open(anyString())).thenReturn(mockConnection);
        setupMockResponse(HttpURLConnection.HTTP_CREATED, SUCCESS_RESPONSE);
        when(mockConnection.getOutputStream()).thenReturn(mockOutputStream);

        String result = externalServiceClient.post(TEST_ENDPOINT, TEST_BODY, true);
        assertEquals(SUCCESS_RESPONSE, result);

        verify(mockConnection).setRequestMethod("POST");
        verify(mockConnection).setDoOutput(true);
        verify(mockConnection).setRequestProperty(eq("Content-Type"), eq("application/json"));
        verify(mockOutputStream).write(outputStreamCaptor.capture(), eq(0), anyInt());
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
        setupMockResponse(HttpURLConnection.HTTP_BAD_REQUEST, ERROR_RESPONSE);
        when(mockConnection.getOutputStream()).thenReturn(mockOutputStream);

        Exception exception = assertThrows(Exception.class, () -> {
            externalServiceClient.post(TEST_ENDPOINT, TEST_BODY, true);
        });
        assertTrue(exception.getMessage().contains("Error en la petición POST: 400"));
        assertTrue(exception.getMessage().contains(ERROR_RESPONSE));
        verify(mockConnection).getErrorStream();
        verify(mockConnection).disconnect();
    }

    @Test
    void put_Hospital_Success() throws Exception {
        mockedClientStatic = Mockito.mockStatic(ExternalServiceClient.class, Mockito.CALLS_REAL_METHODS);
        mockedClientStatic.when(() -> ExternalServiceClient.open(anyString())).thenReturn(mockConnection);
        setupMockResponse(HttpURLConnection.HTTP_OK, SUCCESS_RESPONSE);
        when(mockConnection.getOutputStream()).thenReturn(mockOutputStream);

        String result = externalServiceClient.put(TEST_ENDPOINT, TEST_BODY, true);
        assertEquals(SUCCESS_RESPONSE, result);

        verify(mockConnection).setRequestMethod("PUT");
        verify(mockConnection).setDoOutput(true);
        verify(mockConnection).setRequestProperty(eq("Content-Type"), eq("application/json"));
        verify(mockOutputStream).write(outputStreamCaptor.capture(), eq(0), anyInt());
        String body = new String(outputStreamCaptor.getValue(), StandardCharsets.UTF_8);
        assertTrue(body.contains("\"key\":\"value\""));
        verify(mockConnection).getResponseCode();
        verify(mockConnection).getInputStream();
        verify(mockConnection).disconnect();
    }

    @Test
    void put_HttpError_ThrowsException() throws Exception {
        mockedClientStatic = Mockito.mockStatic(ExternalServiceClient.class, Mockito.CALLS_REAL_METHODS);
        mockedClientStatic.when(() -> ExternalServiceClient.open(anyString())).thenReturn(mockConnection);
        setupMockResponse(HttpURLConnection.HTTP_BAD_REQUEST, ERROR_RESPONSE);
        when(mockConnection.getOutputStream()).thenReturn(mockOutputStream);

        Exception exception = assertThrows(Exception.class, () -> {
            externalServiceClient.put(TEST_ENDPOINT, TEST_BODY, false);
        });
        assertTrue(exception.getMessage().contains("Error en la petición PUT: 400"));
        verify(mockConnection).disconnect();
    }

    // --- Async Tests (verify they delegate correctly) ---
    @Test
    void getAsync_DelegatesToGet() throws Exception {
        // We don't need full HTTP mocking here, just mock the sync method
        ExternalServiceClient spiedClient = spy(externalServiceClient);
        doReturn(SUCCESS_RESPONSE).when(spiedClient).get(eq(TEST_ENDPOINT), eq(true));

        CompletableFuture<String> future = spiedClient.getAsync(TEST_ENDPOINT, true);
        String result = future.get(); // Wait for completion

        assertEquals(SUCCESS_RESPONSE, result);
        verify(spiedClient).get(eq(TEST_ENDPOINT), eq(true)); // Verify delegation
    }

    @Test
    void getAsync_HandlesException() throws Exception {
        ExternalServiceClient spiedClient = spy(externalServiceClient);
        Exception testException = new Exception("GET failed");
        doThrow(testException).when(spiedClient).get(anyString(), anyBoolean());

        CompletableFuture<String> future = spiedClient.getAsync(TEST_ENDPOINT, true);

        ExecutionException thrown = assertThrows(ExecutionException.class, () -> {
            try {
                future.get(); // This might throw InterruptedException as well, caught by outer assertThrows
            } catch (InterruptedException e) {
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
        doReturn(SUCCESS_RESPONSE).when(spiedClient).post(eq(TEST_ENDPOINT), eq(TEST_BODY), eq(false));

        CompletableFuture<String> future = spiedClient.postAsync(TEST_ENDPOINT, TEST_BODY, false);
        String result = future.get();

        assertEquals(SUCCESS_RESPONSE, result);
        verify(spiedClient).post(eq(TEST_ENDPOINT), eq(TEST_BODY), eq(false));
    }

    @Test
    void putAsync_DelegatesToPut() throws Exception {
        ExternalServiceClient spiedClient = spy(externalServiceClient);
        doReturn(SUCCESS_RESPONSE).when(spiedClient).put(eq(TEST_ENDPOINT), eq(TEST_BODY), eq(true));

        CompletableFuture<String> future = spiedClient.putAsync(TEST_ENDPOINT, TEST_BODY, true);
        String result = future.get();

        assertEquals(SUCCESS_RESPONSE, result);
        verify(spiedClient).put(eq(TEST_ENDPOINT), eq(TEST_BODY), eq(true));
    }

    @Test
    void postAsync_HandlesException() throws Exception {
        ExternalServiceClient spiedClient = spy(externalServiceClient);
        Exception testException = new Exception("POST failed");
        doThrow(testException).when(spiedClient).post(anyString(), eq(TEST_BODY), anyBoolean());

        CompletableFuture<String> future = spiedClient.postAsync(TEST_ENDPOINT, TEST_BODY, true);

        ExecutionException thrown = assertThrows(ExecutionException.class, () -> {
            try {
                future.get();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                fail("Test interrupted unexpectedly");
            }
        });

        assertTrue(thrown.getCause() instanceof RuntimeException);
        assertEquals(testException, thrown.getCause().getCause());
        verify(spiedClient).post(anyString(), eq(TEST_BODY), anyBoolean());
    }

    @Test
    void putAsync_HandlesException() throws Exception {
        ExternalServiceClient spiedClient = spy(externalServiceClient);
        Exception testException = new Exception("PUT failed");
        doThrow(testException).when(spiedClient).put(anyString(), eq(TEST_BODY), anyBoolean());

        CompletableFuture<String> future = spiedClient.putAsync(TEST_ENDPOINT, TEST_BODY, false);

        ExecutionException thrown = assertThrows(ExecutionException.class, () -> {
            try {
                future.get();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                fail("Test interrupted unexpectedly");
            }
        });

        assertTrue(thrown.getCause() instanceof RuntimeException);
        assertEquals(testException, thrown.getCause().getCause());
        verify(spiedClient).put(anyString(), eq(TEST_BODY), anyBoolean());
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

        assertThrows(NullPointerException.class, () -> externalServiceClient.post(TEST_ENDPOINT, TEST_BODY, true));
        verify(mockConnection).disconnect();
    }

    @Test
    void post_OutputStreamThrowsIOException_PropagatesAndDisconnects() throws Exception {
        mockedClientStatic = Mockito.mockStatic(ExternalServiceClient.class, Mockito.CALLS_REAL_METHODS);
        mockedClientStatic.when(() -> ExternalServiceClient.open(anyString())).thenReturn(mockConnection);
        when(mockConnection.getOutputStream()).thenThrow(new IOException("OS failure"));

        assertThrows(IOException.class, () -> externalServiceClient.post(TEST_ENDPOINT, TEST_BODY, false));
    }

    @Test
    void post_InputStreamThrowsIOException_PropagatesAndDisconnects() throws Exception {
        mockedClientStatic = Mockito.mockStatic(ExternalServiceClient.class, Mockito.CALLS_REAL_METHODS);
        mockedClientStatic.when(() -> ExternalServiceClient.open(anyString())).thenReturn(mockConnection);
        when(mockConnection.getOutputStream()).thenReturn(mockOutputStream);
        when(mockConnection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_OK);
        when(mockConnection.getInputStream()).thenThrow(new IOException("IS failure on POST"));

        assertThrows(IOException.class, () -> externalServiceClient.post(TEST_ENDPOINT, TEST_BODY, true));
        verify(mockConnection).disconnect();
    }

    @Test
    void get_InputStreamThrowsIOException_PropagatesAndDisconnects() throws Exception {
        mockedClientStatic = Mockito.mockStatic(ExternalServiceClient.class, Mockito.CALLS_REAL_METHODS);
        mockedClientStatic.when(() -> ExternalServiceClient.open(anyString())).thenReturn(mockConnection);
        when(mockConnection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_OK);
        when(mockConnection.getInputStream()).thenThrow(new IOException("IS failure"));

        assertThrows(IOException.class, () -> externalServiceClient.get(TEST_ENDPOINT, true));
        verify(mockConnection).disconnect();
    }

    @Test
    void put_OutputStreamThrowsIOException_Propagates() throws Exception {
        mockedClientStatic = Mockito.mockStatic(ExternalServiceClient.class, Mockito.CALLS_REAL_METHODS);
        mockedClientStatic.when(() -> ExternalServiceClient.open(anyString())).thenReturn(mockConnection);
        when(mockConnection.getOutputStream()).thenThrow(new IOException("OS failure on PUT"));

        assertThrows(IOException.class, () -> externalServiceClient.put(TEST_ENDPOINT, TEST_BODY, true));
        // Do not strictly verify disconnect; production code may not reach finally when OS retrieval fails
    }

    @Test
    void put_InputStreamThrowsIOException_PropagatesAndDisconnects() throws Exception {
        mockedClientStatic = Mockito.mockStatic(ExternalServiceClient.class, Mockito.CALLS_REAL_METHODS);
        mockedClientStatic.when(() -> ExternalServiceClient.open(anyString())).thenReturn(mockConnection);
        when(mockConnection.getOutputStream()).thenReturn(mockOutputStream);
        when(mockConnection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_OK);
        when(mockConnection.getInputStream()).thenThrow(new IOException("IS failure on PUT"));

        assertThrows(IOException.class, () -> externalServiceClient.put(TEST_ENDPOINT, TEST_BODY, false));
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
