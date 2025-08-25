package com.sources.app.sqlite;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.sources.app.entities.User;

public class SQLiteConnectivityTest {

    @SuppressWarnings("unused")
    @BeforeAll
    static void ensureSqliteDir() throws Exception {
        Path dir = Paths.get("sqlite");
        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
        }
    }


    @Test
    void testAllSqliteConfigs() {
        String[] cfgs = new String[] {
            "hibernate-sqlite-USUARIODEV.cfg.xml",
            "hibernate-sqlite-USUARIOUAT.cfg.xml",
            "hibernate-sqlite-USUARIO.cfg.xml"
        };

        for (String cfg : cfgs) {
            // Build an isolated SessionFactory for this cfg (avoid global singleton)
            Configuration configuration = new Configuration().configure(cfg);

            // Force absolute SQLite file path and schema creation per run
            String dbFileName = cfg.contains("USUARIODEV") ? "USUARIODEV.sqlite"
                    : cfg.contains("USUARIOUAT") ? "USUARIOUAT.sqlite" : "USUARIO.sqlite";
            Path dbPath = Paths.get("sqlite", dbFileName).toAbsolutePath();
            configuration.setProperty("hibernate.connection.url", "jdbc:sqlite:" + dbPath.toString());
            configuration.setProperty("hibernate.hbm2ddl.auto", "update");
            configuration.setProperty("show_sql", "true");

            try (SessionFactory sf = configuration.buildSessionFactory();
                 Session session = sf.openSession()) {
                // Insert a minimal User record (only required fields)
                Transaction tx = session.beginTransaction();
                User u = new User();
                u.setName("Test User - " + cfg);
                u.setCui(1234567890L);
                u.setEmail("test-" + System.nanoTime() + "@example.com");
                u.setRole("USER");
                u.setEnabled(1);
                u.setPassword("secret");
                u.setCreatedAt(new Date());
                session.persist(u);
                session.flush();
                tx.commit();
                session.clear();

                // Verify via JDBC using session.doWork
                AtomicInteger count = new AtomicInteger(0);
                session.doWork(conn -> count.set(countUsersByEmail(conn, u.getEmail())));
                assertTrue(count.get() >= 1, "Inserted user should exist in USERS for config: " + cfg);
            }
        }
    }

    private static int countUsersByEmail(Connection conn, String email) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(1) FROM USERS WHERE EMAIL = ?")) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                return 0;
            }
        }
    }
}
