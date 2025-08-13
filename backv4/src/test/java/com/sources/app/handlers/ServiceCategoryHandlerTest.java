package com.sources.app.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sources.app.dao.ServiceCategoryDAO;
import com.sources.app.entities.Category;
import com.sources.app.entities.Service;
import com.sources.app.entities.ServiceCategory;
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
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceCategoryHandlerTest {

    @Mock
    private ServiceCategoryDAO mockDAO;
    @Mock
    private HttpExchange mockHttpExchange;
    @Mock
    private Headers mockRequestHeaders;
    @Mock
    private Headers mockResponseHeaders;
    @Mock
    private OutputStream mockResponseBody;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private ServiceCategoryHandler handler;

    @Captor
    ArgumentCaptor<Integer> statusCodeCaptor;
    @Captor
    ArgumentCaptor<Long> responseLengthCaptor;
    @Captor
    ArgumentCaptor<byte[]> responseBodyCaptor;

    private static final String API_ENDPOINT = "/api/servicecategory";

    @BeforeEach
    void setUp() {
        lenient().when(mockHttpExchange.getRequestURI()).thenReturn(URI.create(API_ENDPOINT));
        lenient().when(mockHttpExchange.getResponseHeaders()).thenReturn(mockResponseHeaders);
        lenient().when(mockHttpExchange.getResponseBody()).thenReturn(mockResponseBody);
        lenient().when(mockHttpExchange.getRequestHeaders()).thenReturn(mockRequestHeaders);
    }

    @Test
    void handle_OptionsRequest_SendsNoContent() throws IOException {
        when(mockHttpExchange.getRequestMethod()).thenReturn("OPTIONS");
        handler.handle(mockHttpExchange);
        verify(mockHttpExchange).sendResponseHeaders(eq(204), eq(-1L));
        verifyNoInteractions(mockDAO);
    }

    @Test
    void handle_WrongPath_SendsNotFound() throws IOException {
        when(mockHttpExchange.getRequestURI()).thenReturn(URI.create("/api/wrong"));
        when(mockHttpExchange.getRequestMethod()).thenReturn("GET");
        handler.handle(mockHttpExchange);
        verify(mockHttpExchange).sendResponseHeaders(eq(404), eq(-1L));
        verifyNoInteractions(mockDAO);
    }

    // GET
    @Test
    void handleGet_FindAll_Success_WhenNoIdsProvided() throws IOException {
        when(mockHttpExchange.getRequestMethod()).thenReturn("GET");
        when(mockHttpExchange.getRequestURI()).thenReturn(URI.create(API_ENDPOINT));
        List<ServiceCategory> list = Arrays.asList(new ServiceCategory(), new ServiceCategory());
        when(mockDAO.findAll()).thenReturn(list);
        String expectedJson = objectMapper.writeValueAsString(list);
        byte[] expectedBytes = expectedJson.getBytes(StandardCharsets.UTF_8);

        handler.handle(mockHttpExchange);

        verify(mockDAO).findAll();
        verifyResponseSent(200, expectedBytes);
    }

    @Test
    void handleGet_FindByCompositeId_Success() throws IOException {
        when(mockHttpExchange.getRequestMethod()).thenReturn("GET");
        when(mockHttpExchange.getRequestURI()).thenReturn(URI.create(API_ENDPOINT + "?idService=1&idCategory=2"));
        ServiceCategory sc = new ServiceCategory();
        when(mockDAO.findById(1L, 2L)).thenReturn(sc);
        String expectedJson = objectMapper.writeValueAsString(sc);
        byte[] expectedBytes = expectedJson.getBytes(StandardCharsets.UTF_8);

        handler.handle(mockHttpExchange);

        verify(mockDAO).findById(1L, 2L);
        verifyResponseSent(200, expectedBytes);
    }

    @Test
    void handleGet_FindByCompositeId_InvalidParams_SendsBadRequest() throws IOException {
        when(mockHttpExchange.getRequestMethod()).thenReturn("GET");
        when(mockHttpExchange.getRequestURI()).thenReturn(URI.create(API_ENDPOINT + "?idService=a&idCategory=2"));

        handler.handle(mockHttpExchange);

        verify(mockHttpExchange).sendResponseHeaders(eq(400), eq(-1L));
        verify(mockDAO, never()).findById(anyLong(), anyLong());
    }

    @Test
    void handleGet_FindByCompositeId_NotFound() throws IOException {
        when(mockHttpExchange.getRequestMethod()).thenReturn("GET");
        when(mockHttpExchange.getRequestURI()).thenReturn(URI.create(API_ENDPOINT + "?idService=1&idCategory=2"));
        when(mockDAO.findById(1L, 2L)).thenReturn(null);

        handler.handle(mockHttpExchange);

        verify(mockDAO).findById(1L, 2L);
        verify(mockHttpExchange).sendResponseHeaders(eq(404), eq(-1L));
    }

    @Test
    void handlePut_JsonInvalid_InternalError() throws IOException {
        when(mockHttpExchange.getRequestMethod()).thenReturn("PUT");
        String invalidJson = "{\"service\":"; // intentionally broken JSON to trigger exception
        InputStream body = new ByteArrayInputStream(invalidJson.getBytes(StandardCharsets.UTF_8));
        when(mockHttpExchange.getRequestBody()).thenReturn(body);

        assertThrows(IOException.class, () -> handler.handle(mockHttpExchange));
        verify(mockDAO, never()).update(any(ServiceCategory.class));
    }

    // POST
    @Test
    void handlePost_Success() throws IOException {
        when(mockHttpExchange.getRequestMethod()).thenReturn("POST");
        Service service = new Service();
        service.setIdService(1L);
        Category category = new Category();
        category.setIdCategory(2L);
        ServiceCategory request = new ServiceCategory();
        request.setService(service);
        request.setCategory(category);
        String json = objectMapper.writeValueAsString(request);
        InputStream body = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
        when(mockHttpExchange.getRequestBody()).thenReturn(body);

        ServiceCategory created = new ServiceCategory();
        when(mockDAO.create(1L, 2L)).thenReturn(created);
        String expectedJson = objectMapper.writeValueAsString(created);
        byte[] expectedBytes = expectedJson.getBytes(StandardCharsets.UTF_8);

        handler.handle(mockHttpExchange);

        verify(mockDAO).create(1L, 2L);
        verifyResponseSent(201, expectedBytes);
    }

    @Test
    void handlePost_BadPayload_SendsBadRequest() throws IOException {
        when(mockHttpExchange.getRequestMethod()).thenReturn("POST");
        String invalidJson = "{\"service\":{}, \"category\":{}}"; // missing ids
        InputStream body = new ByteArrayInputStream(invalidJson.getBytes(StandardCharsets.UTF_8));
        when(mockHttpExchange.getRequestBody()).thenReturn(body);

        handler.handle(mockHttpExchange);

        verify(mockHttpExchange).sendResponseHeaders(eq(400), eq(-1L));
        verify(mockDAO, never()).create(anyLong(), anyLong());
    }

    @Test
    void handlePost_DaoReturnsNull_InternalError() throws IOException {
        when(mockHttpExchange.getRequestMethod()).thenReturn("POST");
        Service service = new Service();
        service.setIdService(1L);
        Category category = new Category();
        category.setIdCategory(2L);
        ServiceCategory request = new ServiceCategory();
        request.setService(service);
        request.setCategory(category);
        String json = objectMapper.writeValueAsString(request);
        InputStream body = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
        when(mockHttpExchange.getRequestBody()).thenReturn(body);
        when(mockDAO.create(1L, 2L)).thenReturn(null);

        handler.handle(mockHttpExchange);

        verify(mockHttpExchange).sendResponseHeaders(eq(500), eq(-1L));
    }

    // PUT
    @Test
    void handlePut_Success() throws IOException {
        when(mockHttpExchange.getRequestMethod()).thenReturn("PUT");
        ServiceCategory toUpdate = new ServiceCategory();
        String json = objectMapper.writeValueAsString(toUpdate);
        InputStream body = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
        when(mockHttpExchange.getRequestBody()).thenReturn(body);

        when(mockDAO.update(any(ServiceCategory.class))).thenReturn(toUpdate);
        String expectedJson = objectMapper.writeValueAsString(toUpdate);
        byte[] expectedBytes = expectedJson.getBytes(StandardCharsets.UTF_8);

        handler.handle(mockHttpExchange);

        verify(mockDAO).update(any(ServiceCategory.class));
        verifyResponseSent(200, expectedBytes);
    }

    @Test
    void handlePut_DaoReturnsNull_InternalError() throws IOException {
        when(mockHttpExchange.getRequestMethod()).thenReturn("PUT");
        ServiceCategory toUpdate = new ServiceCategory();
        String json = objectMapper.writeValueAsString(toUpdate);
        InputStream body = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
        when(mockHttpExchange.getRequestBody()).thenReturn(body);
        when(mockDAO.update(any(ServiceCategory.class))).thenReturn(null);

        handler.handle(mockHttpExchange);

        verify(mockHttpExchange).sendResponseHeaders(eq(500), eq(-1L));
    }

    private void verifyResponseSent(int expectedStatusCode, byte[] expectedBodyBytes) throws IOException {
        verify(mockResponseHeaders).set(eq("Content-Type"), eq("application/json"));
        verify(mockHttpExchange).sendResponseHeaders(statusCodeCaptor.capture(), responseLengthCaptor.capture());
        verify(mockResponseBody).write(responseBodyCaptor.capture());
        verify(mockResponseBody).close();

        assertEquals(expectedStatusCode, statusCodeCaptor.getValue());
        assertArrayEquals(expectedBodyBytes, responseBodyCaptor.getValue());
        assertEquals((long) expectedBodyBytes.length, responseLengthCaptor.getValue());
    }
}
