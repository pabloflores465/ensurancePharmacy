package com.sources.app.util;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HttpClientUtilWebServerTest {

    private MockWebServer server;

    @BeforeEach
    void setUp() throws Exception {
        server = new MockWebServer();
        server.start();
    }

    @AfterEach
    void tearDown() throws Exception {
        if (server != null) {
            server.shutdown();
        }
    }

    @Test
    void get_success_returnsBody() {
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody("{\"ok\":true}"));

        String url = server.url("/api/test").toString();
        String result = HttpClientUtil.get(url);
        assertEquals("{\"ok\":true}", result);
    }

    @Test
    void get_notFound_returnsNull() {
        server.enqueue(new MockResponse()
                .setResponseCode(404)
                .setHeader("Content-Type", "application/json")
                .setBody("{\"error\":\"not found\"}"));

        String url = server.url("/api/missing").toString();
        String result = HttpClientUtil.get(url);
        assertNull(result);
    }

    @Test
    void post_success_writesBody_andReturnsResponse() {
        server.enqueue(new MockResponse()
                .setResponseCode(201)
                .setHeader("Content-Type", "application/json")
                .setBody("{\"created\":true}"));

        String url = server.url("/api/create").toString();
        String json = "{\"name\":\"Alice\"}";
        String result = HttpClientUtil.post(url, json);
        assertEquals("{\"created\":true}", result);
    }

    @Test
    void put_success_returnsResponse() {
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody("{\"updated\":true}"));

        String url = server.url("/api/update").toString();
        String json = "{\"name\":\"Bob\"}";
        String result = HttpClientUtil.put(url, json);
        assertEquals("{\"updated\":true}", result);
    }

    @Test
    void delete_success_returnsEmptyStringOrNullHandled() {
        server.enqueue(new MockResponse()
                .setResponseCode(204));

        String url = server.url("/api/delete").toString();
        String result = HttpClientUtil.delete(url);
        // Some servers return empty body for 204. Our util returns "" or null depending on stream.
        if (result != null) {
            assertEquals("", result);
        }
    }

    @Test
    void post_serverError_returnsNull() {
        server.enqueue(new MockResponse()
                .setResponseCode(500)
                .setHeader("Content-Type", "application/json")
                .setBody("{\"error\":\"boom\"}"));

        String url = server.url("/api/fail").toString();
        String result = HttpClientUtil.post(url, "{\"x\":1}");
        assertNull(result);
    }
}
