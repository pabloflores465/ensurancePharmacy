package com.sources.app.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.sources.app.dao.PrescriptionDAO;
import com.sources.app.entities.Hospital;
import com.sources.app.entities.Medicine;
import com.sources.app.entities.Pharmacy;
import com.sources.app.entities.Prescription;
import com.sources.app.entities.User;
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
import java.math.BigDecimal;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PrescriptionHandlerTest {

    @Mock
    private PrescriptionDAO mockPrescriptionDAO;
    @Mock
    private HttpExchange mockHttpExchange;
    @Mock
    private Headers mockRequestHeaders;
    @Mock
    private Headers mockResponseHeaders;
    @Mock
    private OutputStream mockResponseBody;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper()
            .findAndRegisterModules()
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            .setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));

    @InjectMocks
    private PrescriptionHandler prescriptionHandler;

    @Captor
    ArgumentCaptor<Integer> statusCodeCaptor;
    @Captor
    ArgumentCaptor<Long> responseLengthCaptor;
    @Captor
    ArgumentCaptor<byte[]> responseBodyCaptor;

    private static final String API_ENDPOINT = "/api/prescription";

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
        prescriptionHandler.handle(mockHttpExchange);
        verify(mockHttpExchange).sendResponseHeaders(eq(204), eq(-1L));
        verifyNoInteractions(mockPrescriptionDAO);
    }

    @Test
    void handle_WrongPath_SendsNotFound() throws IOException {
        when(mockHttpExchange.getRequestURI()).thenReturn(URI.create("/api/prescriptions"));
        when(mockHttpExchange.getRequestMethod()).thenReturn("GET");
        prescriptionHandler.handle(mockHttpExchange);
        verify(mockHttpExchange).sendResponseHeaders(eq(404), eq(-1L));
        verifyNoInteractions(mockPrescriptionDAO);
    }

    // --- GET tests ---
    @Test
    void handleGet_FindAll_Success_NoQuery() throws IOException {
        when(mockHttpExchange.getRequestMethod()).thenReturn("GET");
        when(mockHttpExchange.getRequestURI()).thenReturn(URI.create(API_ENDPOINT));
        List<Prescription> list = Arrays.asList(new Prescription(), new Prescription());
        when(mockPrescriptionDAO.findAll()).thenReturn(list);
        String expectedJson = objectMapper.writeValueAsString(list);
        byte[] expectedBytes = expectedJson.getBytes(StandardCharsets.UTF_8);

        prescriptionHandler.handle(mockHttpExchange);

        verify(mockPrescriptionDAO).findAll();
        verifyResponseSent(200, expectedBytes);
    }

    @Test
    void handleGet_FindAll_Success_EmptyQuery() throws IOException {
        when(mockHttpExchange.getRequestMethod()).thenReturn("GET");
        when(mockHttpExchange.getRequestURI()).thenReturn(URI.create(API_ENDPOINT + "?"));
        List<Prescription> list = Arrays.asList(new Prescription(), new Prescription());
        when(mockPrescriptionDAO.findAll()).thenReturn(list);
        String expectedJson = objectMapper.writeValueAsString(list);
        byte[] expectedBytes = expectedJson.getBytes(StandardCharsets.UTF_8);

        prescriptionHandler.handle(mockHttpExchange);

        verify(mockPrescriptionDAO).findAll();
        verifyResponseSent(200, expectedBytes);
    }

    @Test
    void handleGet_FindByUserId_Success() throws IOException {
        when(mockHttpExchange.getRequestMethod()).thenReturn("GET");
        when(mockHttpExchange.getRequestURI()).thenReturn(URI.create(API_ENDPOINT + "?user_id=5"));
        List<Prescription> list = Arrays.asList(new Prescription());
        when(mockPrescriptionDAO.findByUserId(5L)).thenReturn(list);
        String expectedJson = objectMapper.writeValueAsString(list);
        byte[] expectedBytes = expectedJson.getBytes(StandardCharsets.UTF_8);

        prescriptionHandler.handle(mockHttpExchange);

        verify(mockPrescriptionDAO).findByUserId(5L);
        verifyResponseSent(200, expectedBytes);
    }

    @Test
    void handleGet_FindByUserId_InvalidId() throws IOException {
        when(mockHttpExchange.getRequestMethod()).thenReturn("GET");
        when(mockHttpExchange.getRequestURI()).thenReturn(URI.create(API_ENDPOINT + "?user_id=abc"));

        prescriptionHandler.handle(mockHttpExchange);

        verify(mockHttpExchange).sendResponseHeaders(eq(400), eq(-1L));
        verify(mockPrescriptionDAO, never()).findByUserId(anyLong());
    }

    @Test
    void handleGet_FindById_Success() throws IOException {
        when(mockHttpExchange.getRequestMethod()).thenReturn("GET");
        when(mockHttpExchange.getRequestURI()).thenReturn(URI.create(API_ENDPOINT + "?id=10"));
        Prescription p = new Prescription();
        p.setAuth("AUTH");
        when(mockPrescriptionDAO.findById(10L)).thenReturn(p);
        String expectedJson = objectMapper.writeValueAsString(p);
        byte[] expectedBytes = expectedJson.getBytes(StandardCharsets.UTF_8);

        prescriptionHandler.handle(mockHttpExchange);

        verify(mockPrescriptionDAO).findById(10L);
        verifyResponseSent(200, expectedBytes);
    }

    @Test
    void handleGet_FindById_NotFound() throws IOException {
        when(mockHttpExchange.getRequestMethod()).thenReturn("GET");
        when(mockHttpExchange.getRequestURI()).thenReturn(URI.create(API_ENDPOINT + "?id=99"));
        when(mockPrescriptionDAO.findById(99L)).thenReturn(null);

        prescriptionHandler.handle(mockHttpExchange);

        verify(mockPrescriptionDAO).findById(99L);
        verify(mockHttpExchange).sendResponseHeaders(eq(404), eq(-1L));
    }

    @Test
    void handleGet_FindById_InvalidId() throws IOException {
        when(mockHttpExchange.getRequestMethod()).thenReturn("GET");
        when(mockHttpExchange.getRequestURI()).thenReturn(URI.create(API_ENDPOINT + "?id=abc"));

        prescriptionHandler.handle(mockHttpExchange);

        verify(mockPrescriptionDAO, never()).findById(anyLong());
        verify(mockHttpExchange).sendResponseHeaders(eq(400), eq(-1L));
    }

    // --- POST tests ---
    private Prescription buildPrescriptionPayload() {
        Hospital hospital = new Hospital();
        hospital.setIdHospital(1L);
        User user = new User();
        user.setIdUser(2L);
        Medicine medicine = new Medicine();
        medicine.setIdMedicine(3L);
        Pharmacy pharmacy = new Pharmacy();
        pharmacy.setIdPharmacy(4L);

        Prescription p = new Prescription();
        p.setHospital(hospital);
        p.setUser(user);
        p.setMedicine(medicine);
        p.setPharmacy(pharmacy);
        p.setPrescriptionDate(new Date());
        p.setTotal(new BigDecimal("100.50"));
        p.setCopay(new BigDecimal("20.00"));
        p.setPrescriptionComment("Test");
        p.setSecured(1);
        p.setAuth("AUTHCODE");
        return p;
    }

    @Test
    void handlePost_Success() throws IOException {
        when(mockHttpExchange.getRequestMethod()).thenReturn("POST");
        Prescription request = buildPrescriptionPayload();
        String json = objectMapper.writeValueAsString(request);
        InputStream body = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
        when(mockHttpExchange.getRequestBody()).thenReturn(body);

        Prescription created = buildPrescriptionPayload();
        when(mockPrescriptionDAO.create(anyLong(), anyLong(), anyLong(), anyLong(), any(Date.class), any(BigDecimal.class), any(BigDecimal.class), anyString(), anyInt(), anyString()))
                .thenReturn(created);

        String expectedJson = objectMapper.writeValueAsString(created);
        byte[] expectedBytes = expectedJson.getBytes(StandardCharsets.UTF_8);

        prescriptionHandler.handle(mockHttpExchange);

        verify(mockPrescriptionDAO).create(anyLong(), anyLong(), anyLong(), anyLong(), any(Date.class), any(BigDecimal.class), any(BigDecimal.class), anyString(), anyInt(), anyString());
        verifyResponseSent(201, expectedBytes);
    }

    @Test
    void handlePost_DaoReturnsNull_InternalError() throws IOException {
        when(mockHttpExchange.getRequestMethod()).thenReturn("POST");
        Prescription request = buildPrescriptionPayload();
        String json = objectMapper.writeValueAsString(request);
        InputStream body = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
        when(mockHttpExchange.getRequestBody()).thenReturn(body);
        when(mockPrescriptionDAO.create(anyLong(), anyLong(), anyLong(), anyLong(), any(Date.class), any(BigDecimal.class), any(BigDecimal.class), anyString(), anyInt(), anyString()))
                .thenReturn(null);

        prescriptionHandler.handle(mockHttpExchange);

        verify(mockHttpExchange).sendResponseHeaders(eq(500), eq(-1L));
    }

    @Test
    void handlePost_InvalidJson_InternalError() throws IOException {
        when(mockHttpExchange.getRequestMethod()).thenReturn("POST");
        String invalidJson = "{\"hospital\":{}}"; // Missing required nested IDs will cause NPE during handling
        InputStream body = new ByteArrayInputStream(invalidJson.getBytes(StandardCharsets.UTF_8));
        when(mockHttpExchange.getRequestBody()).thenReturn(body);

        prescriptionHandler.handle(mockHttpExchange);

        verify(mockHttpExchange).sendResponseHeaders(eq(500), anyLong());
    }

    @Test
    void handleGet_UnknownParams_ReturnsAll() throws IOException {
        when(mockHttpExchange.getRequestMethod()).thenReturn("GET");
        when(mockHttpExchange.getRequestURI()).thenReturn(URI.create(API_ENDPOINT + "?foo=bar"));
        List<Prescription> list = Arrays.asList(new Prescription());
        when(mockPrescriptionDAO.findAll()).thenReturn(list);
        String expectedJson = objectMapper.writeValueAsString(list);
        byte[] expectedBytes = expectedJson.getBytes(StandardCharsets.UTF_8);

        prescriptionHandler.handle(mockHttpExchange);

        verify(mockPrescriptionDAO).findAll();
        verifyResponseSent(200, expectedBytes);
    }

    // --- PUT tests ---
    @Test
    void handlePut_Success() throws IOException {
        when(mockHttpExchange.getRequestMethod()).thenReturn("PUT");
        Prescription toUpdate = buildPrescriptionPayload();
        String json = objectMapper.writeValueAsString(toUpdate);
        InputStream body = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
        when(mockHttpExchange.getRequestBody()).thenReturn(body);

        when(mockPrescriptionDAO.update(any(Prescription.class))).thenReturn(toUpdate);
        String expectedJson = objectMapper.writeValueAsString(toUpdate);
        byte[] expectedBytes = expectedJson.getBytes(StandardCharsets.UTF_8);

        prescriptionHandler.handle(mockHttpExchange);

        verify(mockPrescriptionDAO).update(any(Prescription.class));
        verifyResponseSent(200, expectedBytes);
    }

    @Test
    void handlePut_DaoReturnsNull_InternalError() throws IOException {
        when(mockHttpExchange.getRequestMethod()).thenReturn("PUT");
        Prescription toUpdate = buildPrescriptionPayload();
        String json = objectMapper.writeValueAsString(toUpdate);
        InputStream body = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
        when(mockHttpExchange.getRequestBody()).thenReturn(body);
        when(mockPrescriptionDAO.update(any(Prescription.class))).thenReturn(null);

        prescriptionHandler.handle(mockHttpExchange);

        verify(mockHttpExchange).sendResponseHeaders(eq(500), eq(-1L));
    }

    @Test
    void handlePut_InvalidJson_ThrowsIOException() {
        when(mockHttpExchange.getRequestMethod()).thenReturn("PUT");
        String invalidJson = "{\"idAppointment\":1, \"total\":"; // malformed JSON
        InputStream body = new ByteArrayInputStream(invalidJson.getBytes(StandardCharsets.UTF_8));
        when(mockHttpExchange.getRequestBody()).thenReturn(body);

        assertThrows(IOException.class, () -> prescriptionHandler.handle(mockHttpExchange));
        verify(mockPrescriptionDAO, never()).update(any(Prescription.class));
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
